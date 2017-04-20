/*
 * Copyright 2017 lizhaotailang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bh.paperplane_study.http;

import com.bh.paperplane_study.util.Api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Lizhaotailang on 2016/9/15.
 */

public class HttpMethods {
    public static final String BASE_URL = Api.ZHIHU_NEWS;

    private static final int DEFAULT_TIMEOUT = 5;

    private HttpApi httpService;

    private HttpMethods() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        httpService = retrofit.create(HttpApi.class);
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder{
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    //获取单例
    public static HttpMethods getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public void loadZhihuDaily(String url, Subscriber s) {
        Observable observable = httpService.getZhihuDaily(url);
        toSubscribe(observable, s);
    }

    public void loadZhihuDailyDetail(String url, Subscriber s) {
        Observable observable = httpService.getZhihuDailyDetail(url);
        toSubscribe(observable, s);
    }

    public void loadGuokr(String url, Subscriber s) {
        Observable observable = httpService.getGuokr(url);
        toSubscribe(observable, s);
    }

    public void loadStringRequest(String url, Subscriber s) {
        Observable observable = httpService.getStringRequest(url);
        toSubscribe(observable, s);
    }

    public void loadDoubanMoment(String url, Subscriber s) {
        Observable observable = httpService.getDoubanMoment(url);
        toSubscribe(observable, s);
    }

    public void loadDoubanMomentDetail(String url, Subscriber s) {
        Observable observable = httpService.getDoubanMomentDetail(url);
        toSubscribe(observable, s);
    }

    private <T> void toSubscribe(Observable<T> o, Subscriber<T> s){
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }


}
