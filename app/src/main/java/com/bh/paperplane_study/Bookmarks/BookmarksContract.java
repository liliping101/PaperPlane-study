package com.bh.paperplane_study.Bookmarks;

import com.bh.paperplane_study.BasePresenter;
import com.bh.paperplane_study.BaseView;
import com.bh.paperplane_study.bean.BeanType;
import com.bh.paperplane_study.bean.DoubanMomentNews;
import com.bh.paperplane_study.bean.GuokrHandpickNews;
import com.bh.paperplane_study.bean.ZhihuDailyNews;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/10.
 */

public interface BookmarksContract {
    interface View extends BaseView<Presenter> {

        void showResults(ArrayList<ZhihuDailyNews.Question> zhihuList,
                         ArrayList<GuokrHandpickNews.result> guokrList,
                         ArrayList<DoubanMomentNews.posts> doubanList,
                         ArrayList<Integer> types);

        void notifyDataChanged();

        void showLoading();

        void stopLoading();

    }

    interface Presenter extends BasePresenter {

        void loadResults(boolean refresh);

        void startReading(BeanType type, int position);

        void checkForFreshData();

        void feelLucky();

    }
}
