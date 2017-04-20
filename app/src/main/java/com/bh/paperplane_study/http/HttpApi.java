package com.bh.paperplane_study.http;

import com.bh.paperplane_study.bean.DoubanMomentNews;
import com.bh.paperplane_study.bean.DoubanMomentStory;
import com.bh.paperplane_study.bean.GuokrHandpickNews;
import com.bh.paperplane_study.bean.ZhihuDailyNews;
import com.bh.paperplane_study.bean.ZhihuDailyStory;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Administrator on 2017/3/13.
 */

public interface HttpApi {

    @GET
    Observable<ZhihuDailyNews> getZhihuDaily(@Url String url);

    @GET
    Observable<ZhihuDailyStory> getZhihuDailyDetail(@Url String url);

    @GET
    Observable<GuokrHandpickNews> getGuokr(@Url String url);

    @GET
    Observable<ResponseBody> getStringRequest(@Url String url);

    @GET
    Observable<DoubanMomentNews> getDoubanMoment(@Url String url);

    @GET
    Observable<DoubanMomentStory> getDoubanMomentDetail(@Url String url);

}
