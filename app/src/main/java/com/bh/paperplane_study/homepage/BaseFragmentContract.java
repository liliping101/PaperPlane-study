package com.bh.paperplane_study.homepage;

import com.bh.paperplane_study.BasePresenter;
import com.bh.paperplane_study.BaseView;

/**
 * Created by Administrator on 2017/3/13.
 */

public interface BaseFragmentContract {
    interface View extends BaseView {

        void showLoading();

        void stopLoading();

        void showError();
    }

    interface Presenter extends BasePresenter {

        void startReading(int position);

        void loadPosts(long date, boolean clearing);

        void refresh();

        void feelLucky();

        void setDate(int year, int monthOfYear, int dayOfMonth);

    }
}
