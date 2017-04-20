package com.bh.paperplane_study.homepage;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.bh.paperplane_study.application.App;
import com.bh.paperplane_study.bean.BeanType;
import com.bh.paperplane_study.bean.DoubanMomentNews;
import com.bh.paperplane_study.detail.DetailActivity;
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

public class DoubanMomentPresenter implements DoubanMomentContract.Presenter {

    private int mYear = Calendar.getInstance().get(Calendar.YEAR);
    private int mMonth = Calendar.getInstance().get(Calendar.MONTH);
    private int mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    private DoubanMomentContract.View view;
    private Context context;
    private HttpMethods httpMethods;
    private RxQuery<HistoryEntity> historysQuery;
    private DaoSession daoSession;
    private HistoryEntityDao historyEntityDao;
    private Gson gson = new Gson();

    private ArrayList<DoubanMomentNews.posts> list = new ArrayList<>();

    public DoubanMomentPresenter(Context context, DoubanMomentContract.View view) {
        this.view = view;
        this.context = context;
        this.view.setPresenter(this);
        httpMethods = HttpMethods.getInstance();
        daoSession = ((App) context.getApplicationContext()).getDaoSession();
        historyEntityDao = daoSession.getHistoryEntityDao();
    }

    @Override
    public void startReading(int position) {
        DoubanMomentNews.posts item = list.get(position);
        Intent intent = new Intent(context, DetailActivity.class);

        intent.putExtra("type", BeanType.TYPE_DOUBAN);
        intent.putExtra("id", item.getId());
        intent.putExtra("title", item.getTitle());
        if (item.getThumbs().size() == 0){
            intent.putExtra("coverUrl", "");
        } else {
            intent.putExtra("coverUrl", item.getThumbs().get(0).getMedium().getUrl());
        }
        context.startActivity(intent);
    }

    @Override
    public void loadPosts(long date, final boolean clearing) {
        if(clearing) {
            view.showLoading();
        }

        if(NetworkState.networkConnected(context)) {
            httpMethods.loadDoubanMoment(Api.DOUBAN_MOMENT + new DateFormatter().DoubanDateFormat(date),
                    new Subscriber<DoubanMomentNews>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            view.stopLoading();
                            view.showError();
                        }

                        @Override
                        public void onNext(DoubanMomentNews post) {
                            if(clearing) {
                                list.clear();
                            }
                            list.addAll(post.getPosts());
                            view.showResults(list);
                            view.stopLoading();

                            for (DoubanMomentNews.posts item : post.getPosts()) {

                                if ( !queryIfIDExists(item.getId())) {
                                    try {
                                        HistoryEntity entity = new HistoryEntity();
                                        entity.setContentId(item.getId());
                                        entity.setNews(gson.toJson(item));
                                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                        entity.setDate(format.parse(item.getPublished_time()));
                                        entity.setType(BeanType.TYPE_DOUBAN);
                                        historyEntityDao.insert(entity);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                Intent intent = new Intent("com.marktony.zhihudaily.LOCAL_BROADCAST");
                                intent.putExtra("type", CacheService.TYPE_DOUBAN);
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
                        .where(HistoryEntityDao.Properties.Type.eq(BeanType.TYPE_DOUBAN.name()))
                        .orderAsc(HistoryEntityDao.Properties.Id)
                        .rx();
                historysQuery
                    .list()
                    .map(new Func1<List<HistoryEntity>, List<DoubanMomentNews.posts>>() {
                        @Override
                        public List<DoubanMomentNews.posts> call(List<HistoryEntity> historyEntities) {
                            List<DoubanMomentNews.posts> tmps = new ArrayList<>();
                            for(HistoryEntity history : historyEntities) {
                                DoubanMomentNews.posts post = gson.fromJson(history.getNews(),DoubanMomentNews.posts.class);
                                tmps.add(post);
                            }
                            return tmps;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<DoubanMomentNews.posts>>() {
                        @Override
                        public void call(List<DoubanMomentNews.posts> posts) {
                            list.addAll(posts);
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
    public void feelLucky() {
        if (list.isEmpty()) {
            view.showError();
            return;
        }
        startReading(new Random().nextInt(list.size()));
    }

    @Override
    public void setDate(int year, int monthOfYear, int dayOfMonth) {
        mDay = dayOfMonth;
        mMonth = monthOfYear;
        mYear = year;
    }

    @Override
    public void start() {
        loadPosts(Calendar.getInstance().getTimeInMillis(), true);
    }

    @Override
    public void loadMore() {
        Calendar temp = Calendar.getInstance();
        temp.clear();
        temp.set(mYear, mMonth, mDay);
        loadPosts(temp.getTimeInMillis(), false);
    }

    private boolean queryIfIDExists(int id){
        List<HistoryEntity> query = daoSession.getHistoryEntityDao()
                .queryBuilder()
                .where(HistoryEntityDao.Properties.ContentId.eq(id), HistoryEntityDao.Properties.Type.eq(BeanType.TYPE_DOUBAN.name()))
                .list();

        if(query.size()>0) {
            return true;
        } else {
            return false;
        }
    }
}
