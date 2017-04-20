package com.bh.paperplane_study;

/**
 * Created by Administrator on 2017/3/10.
 */

public interface BaseView<T> {

    /**
     * set the presenter of mvp
     *
     * @param presenter
     */
    void setPresenter(T presenter);

    /**
     * init the views of fragment
     */
    void initViews();
}
