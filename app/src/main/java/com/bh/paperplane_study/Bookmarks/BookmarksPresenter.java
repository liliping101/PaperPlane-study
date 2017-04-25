package com.bh.paperplane_study.Bookmarks;

import android.content.Context;
import android.content.Intent;

import com.bh.paperplane_study.application.App;
import com.bh.paperplane_study.bean.BeanType;
import com.bh.paperplane_study.bean.DoubanMomentNews;
import com.bh.paperplane_study.bean.GuokrHandpickNews;
import com.bh.paperplane_study.bean.ZhihuDailyNews;
import com.bh.paperplane_study.detail.DetailActivity;
import com.bh.paperplane_study.entity.BeanTypeConverter;
import com.bh.paperplane_study.entity.HistoryEntity;
import com.bh.paperplane_study.gen.DaoSession;
import com.bh.paperplane_study.gen.HistoryEntityDao;
import com.google.gson.Gson;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.bh.paperplane_study.adapter.BookmarksAdapter.TYPE_DOUBAN_NORMAL;
import static com.bh.paperplane_study.adapter.BookmarksAdapter.TYPE_DOUBAN_WITH_HEADER;
import static com.bh.paperplane_study.adapter.BookmarksAdapter.TYPE_GUOKR_NORMAL;
import static com.bh.paperplane_study.adapter.BookmarksAdapter.TYPE_GUOKR_WITH_HEADER;
import static com.bh.paperplane_study.adapter.BookmarksAdapter.TYPE_ZHIHU_NORMAL;
import static com.bh.paperplane_study.adapter.BookmarksAdapter.TYPE_ZHIHU_WITH_HEADER;

/**
 * Created by Administrator on 2017/3/10.
 */

public class BookmarksPresenter implements BookmarksContract.Presenter{

    private Context context;
    private BookmarksContract.View view;
    private BookmarksContract.Presenter presenter;

    private ArrayList<DoubanMomentNews.posts> doubanList;
    private ArrayList<GuokrHandpickNews.result> guokrList;
    private ArrayList<ZhihuDailyNews.Question> zhihuList;
    private ArrayList<Integer> types;
    private List<HistoryEntity> historysQuery;
    private Query<HistoryEntity> query;
    private DaoSession daoSession;
    private Gson gson;
    private BeanTypeConverter converter;

    public BookmarksPresenter(Context context, BookmarksContract.View view) {
        this.context = context;
        this.view = view;
        this.view.setPresenter(this);

        zhihuList = new ArrayList<>();
        guokrList = new ArrayList<>();
        doubanList = new ArrayList<>();

        types = new ArrayList<>();

        gson = new Gson();
        daoSession = ((App) context.getApplicationContext()).getDaoSession();
        converter = new BeanTypeConverter();
    }

    @Override
    public void loadResults(boolean refresh) {
        if (!refresh) {
            view.showLoading();
        } else {
            zhihuList.clear();
            guokrList.clear();
            doubanList.clear();
            types.clear();
        }

        checkForFreshData();

        view.showResults(zhihuList, guokrList, doubanList, types);

        view.stopLoading();
    }

    @Override
    public void startReading(BeanType type, int position) {
        Intent intent = new Intent(context, DetailActivity.class);
        switch (type) {
            case TYPE_ZHIHU:
                ZhihuDailyNews.Question q = zhihuList.get(position - 1);
                intent.putExtra("type", BeanType.TYPE_ZHIHU);
                intent.putExtra("id",q.getId());
                intent.putExtra("title", q.getTitle());
                intent.putExtra("coverUrl", q.getImages().get(0));
                break;

            case TYPE_GUOKR:
                GuokrHandpickNews.result r = guokrList.get(position - zhihuList.size() - 2);
                intent.putExtra("type", BeanType.TYPE_GUOKR);
                intent.putExtra("id", r.getId());
                intent.putExtra("title", r.getTitle());
                intent.putExtra("coverUrl", r.getHeadline_img());
                break;
            case TYPE_DOUBAN:
                DoubanMomentNews.posts p = doubanList.get(position - zhihuList.size() - guokrList.size() - 3);
                intent.putExtra("type", BeanType.TYPE_DOUBAN);
                intent.putExtra("id", p.getId());
                intent.putExtra("title", p.getTitle());
                if (p.getThumbs().size() == 0){
                    intent.putExtra("coverUrl", "");
                } else {
                    intent.putExtra("image", p.getThumbs().get(0).getMedium().getUrl());
                }
                break;
            default:
                break;
        }
        context.startActivity(intent);
    }

    @Override
    public void checkForFreshData() {
        query = daoSession.getHistoryEntityDao()
                .queryBuilder()
                .where(HistoryEntityDao.Properties.Bookmark.eq(1), HistoryEntityDao.Properties.Type.eq(converter.convertToDatabaseValue(BeanType.TYPE_ZHIHU)))
                .build();
        historysQuery = query.list();
        types.add(TYPE_ZHIHU_WITH_HEADER);
        for(HistoryEntity history : historysQuery) {
            ZhihuDailyNews.Question question = gson.fromJson(history.getNews(), ZhihuDailyNews.Question.class);
            zhihuList.add(question);
            types.add(TYPE_ZHIHU_NORMAL);
        }

        query.setParameter(1, converter.convertToDatabaseValue(BeanType.TYPE_GUOKR));
        historysQuery = query.list();
        types.add(TYPE_GUOKR_WITH_HEADER);
        for(HistoryEntity history : historysQuery) {
            GuokrHandpickNews.result result = gson.fromJson(history.getNews(), GuokrHandpickNews.result.class);
            guokrList.add(result);
            types.add(TYPE_GUOKR_NORMAL);
        }

        query.setParameter(1, converter.convertToDatabaseValue(BeanType.TYPE_DOUBAN));
        historysQuery = query.list();
        types.add(TYPE_DOUBAN_WITH_HEADER);
        for(HistoryEntity history : historysQuery) {
            DoubanMomentNews.posts post = gson.fromJson(history.getNews(), DoubanMomentNews.posts.class);
            doubanList.add(post);
            types.add(TYPE_DOUBAN_NORMAL);
        }

    }

    @Override
    public void feelLucky() {
        if(zhihuList.isEmpty()&&guokrList.isEmpty()&&doubanList.isEmpty()) {
            return;
        }

        Random random = new Random();
        int p = random.nextInt(types.size());
        while (true) {
            if (types.get(p) == TYPE_ZHIHU_NORMAL) {
                startReading(BeanType.TYPE_ZHIHU, p);
                break;
            } else if (types.get(p) == TYPE_GUOKR_NORMAL) {
                startReading(BeanType.TYPE_GUOKR, p);
                break;
            } else if (types.get(p) == TYPE_DOUBAN_NORMAL) {
                startReading(BeanType.TYPE_DOUBAN, p);
                break;
            } else {
                p = random.nextInt(types.size());
            }
        }
    }

    @Override
    public void start() {

    }
}
