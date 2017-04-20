package com.bh.paperplane_study.homepage;

import com.bh.paperplane_study.bean.ZhihuDailyNews;

import java.util.ArrayList;

/**
 * Created by Lizhaotailang on 2016/9/16.
 */

public interface ZhihuDailyContract {

    interface View extends BaseFragmentContract.View {
        void showResults(ArrayList<ZhihuDailyNews.Question> list);

        void setPresenter(ZhihuDailyContract.Presenter presenter);

    }

    interface Presenter extends BaseFragmentContract.Presenter {
        void loadMore();
    }

}
