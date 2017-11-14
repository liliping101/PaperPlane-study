package com.bh.paperplane_study.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bh.paperplane_study.application.App;
import com.bh.paperplane_study.bean.BeanType;
import com.bh.paperplane_study.bean.ZhihuDailyStory;
import com.bh.paperplane_study.entity.BeanTypeConverter;
import com.bh.paperplane_study.entity.HistoryEntity;
import com.bh.paperplane_study.gen.DaoSession;
import com.bh.paperplane_study.gen.HistoryEntityDao;
import com.bh.paperplane_study.http.HttpMethods;
import com.bh.paperplane_study.util.Api;
import com.bh.paperplane_study.util.NetworkState;

import org.greenrobot.greendao.rx.RxQuery;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Subscriber;

public class CacheService extends Service {

    public static final int TYPE_ZHIHU = 0x00;
    public static final int TYPE_GUOKR = 0x01;
    public static final int TYPE_DOUBAN = 0x02;
    private RxQuery<HistoryEntity> historysQuery;
    private DaoSession daoSession;
    private HistoryEntityDao historyEntityDao;
    private HttpMethods httpMethods;
    private BeanTypeConverter converter;

    @Override
    public IBinder onBind(Intent intent) {
            return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.marktony.zhihudaily.LOCAL_BROADCAST");
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(new LocalReceiver(), filter);
        daoSession = ((App) getApplicationContext()).getDaoSession();
        historyEntityDao = daoSession.getHistoryEntityDao();
        httpMethods = HttpMethods.getInstance();
        converter = new BeanTypeConverter();
    }

    class LocalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int id = intent.getIntExtra("id", 0);
            switch (intent.getIntExtra("type", -1)) {
                case TYPE_ZHIHU:
                    startZhihuCache(id);
                    break;
                case TYPE_GUOKR:
                    startGuokrCache(id);
                    break;
                case TYPE_DOUBAN:
                    startDoubanCache(id);
                    break;
                default:
                    break;
            }
        }
    }

    private void startDoubanCache(final int id) {
        HistoryEntity entity = historyEntityDao.queryBuilder()
                .where(HistoryEntityDao.Properties.ContentId.eq(id), HistoryEntityDao.Properties.Type.eq(converter.convertToDatabaseValue(BeanType.TYPE_DOUBAN)))
                .build()
                .unique();

        if (entity != null && entity.getContent() != null) {
            return;
        }

        Log.d("llp cache douban", "id="+id);

        if (NetworkState.networkConnected(getApplicationContext())) {
            httpMethods.loadStringRequest(Api.DOUBAN_ARTICLE_DETAIL + id, new Subscriber<ResponseBody>() {
                @Override
                public void onCompleted() {}

                @Override
                public void onError(Throwable e){
                    e.printStackTrace();
                }

                @Override
                public void onNext(ResponseBody body) {
                    if(body!=null) {
                        HistoryEntity entity = historyEntityDao.queryBuilder()
                               .where(HistoryEntityDao.Properties.ContentId.eq(id), HistoryEntityDao.Properties.Type.eq(converter.convertToDatabaseValue(BeanType.TYPE_DOUBAN)))
                               .build()
                               .unique();
                        if (entity != null) {
                            try {
                                entity.setContent(new String(body.bytes()));
                                historyEntityDao.update(entity);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            });
        }
    }

    private void startGuokrCache(final int id) {
        HistoryEntity entity = historyEntityDao.queryBuilder()
                .where(HistoryEntityDao.Properties.ContentId.eq(id), HistoryEntityDao.Properties.Type.eq(converter.convertToDatabaseValue(BeanType.TYPE_GUOKR)))
                .build()
                .unique();
        if (entity != null && entity.getContent() != null) {
            return;
        }

        Log.d("llp cache guokr", "id="+id);

        if (NetworkState.networkConnected(getApplicationContext())) {
            httpMethods.loadStringRequest(Api.GUOKR_ARTICLE_LINK_V1 + id,
                new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                       e.printStackTrace();
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        if(response!=null) {
                            HistoryEntity entity = historyEntityDao.queryBuilder()
                                    .where(HistoryEntityDao.Properties.ContentId.eq(id), HistoryEntityDao.Properties.Type.eq(converter.convertToDatabaseValue(BeanType.TYPE_GUOKR)))
                                    .build()
                                    .unique();
                            if (entity != null) {
                                try {
                                    entity.setContent(new String(response.bytes()));
                                    historyEntityDao.update(entity);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
        }
    }

    private void startZhihuCache(final int id) {
        HistoryEntity entity = historyEntityDao.queryBuilder()
                .where(HistoryEntityDao.Properties.ContentId.eq(id), HistoryEntityDao.Properties.Type.eq(converter.convertToDatabaseValue(BeanType.TYPE_ZHIHU)))
                .build()
                .unique();
        if (entity != null && entity.getContent() != null) {
            return;
        }

        Log.d("llp cache zhihu", "id="+id);

        if (NetworkState.networkConnected(getApplicationContext())) {
            httpMethods.loadZhihuDailyDetail(Api.ZHIHU_NEWS + id, new Subscriber<ZhihuDailyStory>() {
                @Override
                public void onCompleted() {}

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onNext(ZhihuDailyStory zhihuDailyStory) {
                    if (zhihuDailyStory != null) {
                        if(zhihuDailyStory.getType()==1) {
                            httpMethods.loadStringRequest(zhihuDailyStory.getShare_url(), new Subscriber<ResponseBody>() {
                                @Override
                                public void onCompleted() {}

                                @Override
                                public void onError(Throwable e) {}

                                @Override
                                public void onNext(ResponseBody body) {
                                    try {
                                        HistoryEntity entity = historyEntityDao.queryBuilder()
                                                .where(HistoryEntityDao.Properties.ContentId.eq(id), HistoryEntityDao.Properties.Type.eq(converter.convertToDatabaseValue(BeanType.TYPE_ZHIHU)))
                                                .build()
                                                .unique();
                                        entity.setContent(new String(body.bytes()));
                                        historyEntityDao.update(entity);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            HistoryEntity entity = historyEntityDao.queryBuilder()
                                    .where(HistoryEntityDao.Properties.ContentId.eq(id), HistoryEntityDao.Properties.Type.eq(converter.convertToDatabaseValue(BeanType.TYPE_ZHIHU)))
                                    .build()
                                    .unique();
                            if (entity != null) {
                                entity.setContent(zhihuDailyStory.getBody());
                                historyEntityDao.update(entity);
                            }
                        }
                    }
                }

            });

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        deleteTimeoutPosts();
    }

    private void deleteTimeoutPosts(){

        SharedPreferences sp = getSharedPreferences("user_settings",MODE_PRIVATE);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -(Integer.parseInt(sp.getString("time_of_saving_articles", "7"))));

        List<HistoryEntity> querys = daoSession.getHistoryEntityDao()
                .queryBuilder()
                .where(HistoryEntityDao.Properties.Bookmark.notEq(1), HistoryEntityDao.Properties.Date.lt(c.getTime()))
                .list();
        for (HistoryEntity query : querys) {
            historyEntityDao.delete(query);
        }
    }
}
