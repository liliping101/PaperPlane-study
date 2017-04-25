package com.bh.paperplane_study.bean;

/**
 * Created by Administrator on 2017/3/10.
 */

public enum BeanType {
    DEFAULT(0), TYPE_ZHIHU(1),TYPE_GUOKR(2),TYPE_DOUBAN(3);

    public final int id;

    BeanType(int id) {
        this.id = id;
    }
}
