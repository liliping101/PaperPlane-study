package com.bh.paperplane_study.settings;

import android.support.v7.preference.Preference;

import com.bh.paperplane_study.BasePresenter;
import com.bh.paperplane_study.BaseView;

/**
 * Created by Administrator on 2017/3/10.
 */

public interface SettingsContract {

    interface View extends BaseView<Presenter>
    {
        void showcleanGlideCache();
    }

    interface Presenter extends BasePresenter
    {
        void setNoPictureMode(Preference preference);

        void setInAppBrowser(Preference preference);

        void cleanGlideCache();

        void setTimeOfSavingArticles(Preference preference, Object newValue);

        String getTimeSummary();
    }
}
