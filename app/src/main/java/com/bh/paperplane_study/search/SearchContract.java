package com.bh.paperplane_study.search;

import com.bh.paperplane_study.BasePresenter;
import com.bh.paperplane_study.BaseView;
import com.bh.paperplane_study.bean.BeanType;
import com.bh.paperplane_study.bean.DoubanMomentNews;
import com.bh.paperplane_study.bean.Guokr.GuokrHandpickNewsResult;
import com.bh.paperplane_study.bean.ZhihuDailyNews;

import java.util.ArrayList;

public interface SearchContract {

    interface View extends BaseView<Presenter> {

        void showResults(ArrayList<ZhihuDailyNews.Question> zhihuList,
                         ArrayList<GuokrHandpickNewsResult> guokrList,
                         ArrayList<DoubanMomentNews.posts> doubanList,
                         ArrayList<Integer> types);

        void showLoading();

        void stopLoading();

    }

    interface Presenter extends BasePresenter {

        void loadResults(String queryWords);

        void startReading(BeanType type, int position);

    }

}
