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

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.bh.paperplane_study.application.App;
import com.bh.paperplane_study.bean.BeanType;
import com.bh.paperplane_study.bean.ZhihuDailyNews;
import com.bh.paperplane_study.detail.DetailActivity;
import com.bh.paperplane_study.entity.BeanTypeConverter;
import com.bh.paperplane_study.entity.HistoryEntity;
import com.bh.paperplane_study.gen.DaoSession;
import com.bh.paperplane_study.gen.HistoryEntityDao;
import com.bh.paperplane_study.http.HttpMethods;
import com.bh.paperplane_study.service.CacheService;
import com.bh.paperplane_study.util.Api;
import com.bh.paperplane_study.util.DateFormatter;
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

public class ZhihuDailyPresenter implements ZhihuDailyContract.Presenter {

    private int mYear = Calendar.getInstance().get(Calendar.YEAR);
    private int mMonth = Calendar.getInstance().get(Calendar.MONTH);
    private int mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    private ZhihuDailyContract.View view;
    private Context context;
    private HttpMethods httpMethods;

    private DateFormatter formatter = new DateFormatter();
    private Gson gson = new Gson();

    private ArrayList<ZhihuDailyNews.Question> list = new ArrayList<ZhihuDailyNews.Question>();

    private RxQuery<HistoryEntity> historysQuery;
    private DaoSession daoSession;
    private HistoryEntityDao historyEntityDao;
    private BeanTypeConverter converter;

    public ZhihuDailyPresenter(Context context, final ZhihuDailyContract.View view) {
        this.context = context;
        this.view = view;
        this.view.setPresenter(this);
        httpMethods = HttpMethods.getInstance();
        daoSession = ((App) context.getApplicationContext()).getDaoSession();
        historyEntityDao = daoSession.getHistoryEntityDao();
        converter = new BeanTypeConverter();
    }

    @Override
    public void loadPosts(long date, final boolean clearing) {
        if (clearing) {
            view.showLoading();
        }

        if (NetworkState.networkConnected(context)) {
            httpMethods.loadZhihuDaily(Api.ZHIHU_HISTORY + formatter.ZhihuDailyDateFormat(date),
                    new Subscriber<ZhihuDailyNews>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            view.stopLoading();
                            view.showError();
                        }

                        @Override
                        public void onNext(ZhihuDailyNews post) {
                            if (clearing) {
                                list.clear();
                            }
                            list.addAll(post.getStories());
                            view.showResults(list);
                            view.stopLoading();

                            for (ZhihuDailyNews.Question item : post.getStories()) {

                                if ( !queryIfIDExists(item.getId())) {
                                    try {
                                        HistoryEntity entity = new HistoryEntity();
                                        entity.setContentId(item.getId());
                                        entity.setNews(gson.toJson(item));
                                        DateFormat format = new SimpleDateFormat("yyyyMMdd");
                                        entity.setDate(format.parse(post.getDate()));
                                        entity.setType(BeanType.TYPE_ZHIHU);
                                        historyEntityDao.insert(entity);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                Intent intent = new Intent("com.marktony.zhihudaily.LOCAL_BROADCAST");
                                intent.putExtra("type", CacheService.TYPE_ZHIHU);
                                intent.putExtra("id", item.getId());
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                            }
                        }
                    });

        } else {
            if (clearing) {

                list.clear();
                // query all notes, sorted a-z by their text
                historysQuery = daoSession.getHistoryEntityDao()
                                        .queryBuilder()
                                        .where(HistoryEntityDao.Properties.Type.eq(converter.convertToDatabaseValue(BeanType.TYPE_ZHIHU)))
                                        .orderAsc(HistoryEntityDao.Properties.Id)
                                        .rx();
                historysQuery.list()
                    .map(new Func1<List<HistoryEntity>, List<ZhihuDailyNews.Question>>() {
                        @Override
                        public List<ZhihuDailyNews.Question> call(List<HistoryEntity> historyEntities) {
                            List<ZhihuDailyNews.Question> tmps = new ArrayList<>();
                            for(HistoryEntity history : historyEntities) {
                                ZhihuDailyNews.Question question = gson.fromJson(history.getNews(), ZhihuDailyNews.Question.class);
                                tmps.add(question);
                            }
                            return tmps;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<ZhihuDailyNews.Question>>() {
                        @Override
                        public void call(List<ZhihuDailyNews.Question> questions) {
                            list.addAll(questions);
                            view.stopLoading();
                            if(list.size()>0) {
                                view.showResults(list);
                            } else {
                                view.showError();
                            }
                        }
                    });
            } else {
                view.stopLoading();
                view.showError();
            }
        }
    }

    @Override
    public void refresh() {
        loadPosts(Calendar.getInstance().getTimeInMillis(), true);
    }

    @Override
    public void loadMore() {
        Calendar temp = Calendar.getInstance();
        temp.clear();
        temp.set(mYear, mMonth, --mDay);
        loadPosts(temp.getTimeInMillis(), false);
    }

    @Override
    public void startReading(int position) {

        context.startActivity(new Intent(context, DetailActivity.class)
                .putExtra("type", BeanType.TYPE_ZHIHU)
                .putExtra("id", list.get(position).getId())
                .putExtra("title", list.get(position).getTitle())
                .putExtra("coverUrl", list.get(position).getImages().get(0)));

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
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
    }

    @Override
    public void start() {
        loadPosts(Calendar.getInstance().getTimeInMillis(), true);
    }

    private boolean queryIfIDExists(int id){
        List<HistoryEntity> query = daoSession.getHistoryEntityDao()
                .queryBuilder()
                .where(HistoryEntityDao.Properties.ContentId.eq(id), HistoryEntityDao.Properties.Type.eq(converter.convertToDatabaseValue(BeanType.TYPE_ZHIHU)))
                .list();

        if(query.size()>0) {
            return true;
        } else {
            return false;
        }
    }

}
