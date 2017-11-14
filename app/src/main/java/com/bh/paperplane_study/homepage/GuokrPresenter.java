/*
 * Copyright 2017 lizhaotailang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bh.paperplane_study.homepage;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.bh.paperplane_study.application.App;
import com.bh.paperplane_study.bean.BeanType;
import com.bh.paperplane_study.bean.Guokr.GuokrHandpickNews;
import com.bh.paperplane_study.bean.Guokr.GuokrHandpickNewsResult;
import com.bh.paperplane_study.detail.DetailActivity;
import com.bh.paperplane_study.entity.BeanTypeConverter;
import com.bh.paperplane_study.entity.HistoryEntity;
import com.bh.paperplane_study.gen.DaoSession;
import com.bh.paperplane_study.gen.HistoryEntityDao;
import com.bh.paperplane_study.http.HttpMethods;
import com.bh.paperplane_study.service.CacheService;
import com.bh.paperplane_study.util.Api;
import com.bh.paperplane_study.util.NetworkState;
import com.google.gson.Gson;

import org.greenrobot.greendao.rx.RxQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GuokrPresenter implements GuokrContract.Presenter {

    private GuokrContract.View view;
    private Context context;
    private HttpMethods httpMethods;
    private ArrayList<GuokrHandpickNewsResult> list = new ArrayList<GuokrHandpickNewsResult>();
    private Gson gson = new Gson();
    private RxQuery<HistoryEntity> historysQuery;
    private DaoSession daoSession;
    private HistoryEntityDao historyEntityDao;
    private BeanTypeConverter converter;
    private int mOffset = 0;

    public GuokrPresenter(Context context, GuokrContract.View view) {
        this.view = view;
        this.context = context;
        this.view.setPresenter(this);
        httpMethods = HttpMethods.getInstance();
        daoSession = ((App) context.getApplicationContext()).getDaoSession();
        historyEntityDao = daoSession.getHistoryEntityDao();
        converter = new BeanTypeConverter();
    }

    @Override
    public void startReading(int position) {
        context.startActivity(new Intent(context, DetailActivity.class)
                .putExtra("type", BeanType.TYPE_GUOKR)
                .putExtra("id", list.get(position).getId())
                .putExtra("title", list.get(position).getTitle())
                .putExtra("coverUrl", list.get(position).getImageInfo().getUrl()));
    }

    @Override
    public void loadPosts(long date, final boolean clearing) {
        if(clearing) {
            view.showLoading();
        }

        if (NetworkState.networkConnected(context)) {
            httpMethods.loadGuokr(Api.GUOKR_ARTICLES, mOffset,
                    new Subscriber<GuokrHandpickNews>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            view.stopLoading();
                            view.showError();
                        }

                        @Override
                        public void onNext(GuokrHandpickNews post) {
                            ContentValues values = new ContentValues();
                            if(clearing) {
                                list.clear();
                            }
                            list.addAll(post.getResult());
                            view.showResults(list);
                            view.stopLoading();

                            for (GuokrHandpickNewsResult item : post.getResult()) {
                                if ( !queryIfIDExists(item.getId())) {
                                    try {
                                        HistoryEntity entity = new HistoryEntity();
                                        entity.setContentId(item.getId());
                                        entity.setNews(gson.toJson(item));
                                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                        entity.setDate(format.parse(item.getDatePublished()));
                                        entity.setType(BeanType.TYPE_GUOKR);
                                        historyEntityDao.insert(entity);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                Intent intent = new Intent("com.marktony.zhihudaily.LOCAL_BROADCAST");
                                intent.putExtra("type", CacheService.TYPE_GUOKR);
                                intent.putExtra("id", item.getId());
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            }
                        }
                    });
        } else {
            if(clearing) {
                list.clear();
                // query all notes, sorted a-z by their text
                historysQuery = daoSession.getHistoryEntityDao()
                        .queryBuilder()
                        .where(HistoryEntityDao.Properties.Type.eq(converter.convertToDatabaseValue(BeanType.TYPE_GUOKR)))
                        .orderAsc(HistoryEntityDao.Properties.Id)
                        .rx();

                historysQuery
                        .list()
                        .map(new Func1<List<HistoryEntity>, List<GuokrHandpickNewsResult>>() {
                            @Override
                            public List<GuokrHandpickNewsResult> call(List<HistoryEntity> historyEntities) {
                                List<GuokrHandpickNewsResult> tmps = new ArrayList<>();
                                for (HistoryEntity history : historyEntities) {
                                    GuokrHandpickNewsResult result = gson.fromJson(history.getNews(), GuokrHandpickNewsResult.class);
                                    tmps.add(result);
                                }
                                return tmps;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<GuokrHandpickNewsResult>>() {
                            @Override
                            public void call(List<GuokrHandpickNewsResult> results) {
                                list.addAll(results);
                                view.stopLoading();
                                if (list.size() > 0) {
                                    view.showResults(list);
                                } else {
                                    view.showError();
                                }
                            }
                        });
            }else {
                view.stopLoading();
                view.showError();
            }
        }
    }

    @Override
    public void refresh() {
        mOffset = 0;
        loadPosts(0, true);
    }

    @Override
    public void feelLucky() {
        if (list.isEmpty()) {
            view.showError();
            return;
        }
        startReading(new Random().nextInt(list.size()));
    }

    @Override
    public void setDate(int year, int monthOfYear, int dayOfMonth) {

    }

    @Override
    public void start() {
        mOffset = 0;
        loadPosts(0, true);
    }

    private boolean queryIfIDExists(int id){
        List<HistoryEntity> query = daoSession.getHistoryEntityDao()
                .queryBuilder()
                .where(HistoryEntityDao.Properties.ContentId.eq(id), HistoryEntityDao.Properties.Type.eq(converter.convertToDatabaseValue(BeanType.TYPE_GUOKR)))
                .list();

        if(query.size()>0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void loadMore() {
        mOffset = list.size();
        loadPosts(0, false);
    }
}
