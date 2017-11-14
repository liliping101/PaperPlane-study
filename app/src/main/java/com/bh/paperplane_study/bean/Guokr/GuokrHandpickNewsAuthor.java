/*
 * Copyright 2016 lizhaotailang
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

package com.bh.paperplane_study.bean.Guokr;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Immutable model class for guokr handpick new author. See the json string for more details.
 * Entity class for {@link com.google.gson.Gson} and {@link android.arch.persistence.room.Room}.
 */
public class GuokrHandpickNewsAuthor {

    @Expose
    @SerializedName("ukey")
    private String ukey;

    @Expose
    @SerializedName("is_title_authorized")
    private String isTitleAuthorized;

    @Expose
    @SerializedName("nickname")
    private String nickname;

    @Expose
    @SerializedName("master_category")
    private String masterCategory;

    @Expose
    @SerializedName("amended_reliability")
    private String amendedReliability;

    @Expose
    @SerializedName("is_exists")
    private boolean isExists;

    @Expose
    @SerializedName("title")
    private String title;

    @Expose
    @SerializedName("url")
    private String url;

    @Expose
    @SerializedName("gender")
    private String gender;

    @Expose
    @SerializedName("followers_count")
    private int followersCount;

    @Expose
    @SerializedName("avatar")
    private GuokrHandpickNewsAvatar avatar;

    @Expose
    @SerializedName("resource_url")
    private String resourceUrl;

    public GuokrHandpickNewsAuthor() {
    }

    public String getUkey() {
        return ukey;
    }

    public void setUkey(String ukey) {
        this.ukey = ukey;
    }

    public String getIsTitleAuthorized() {
        return isTitleAuthorized;
    }

    public void setIsTitleAuthorized(String isTitleAuthorized) {
        this.isTitleAuthorized = isTitleAuthorized;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMasterCategory() {
        return masterCategory;
    }

    public void setMasterCategory(String masterCategory) {
        this.masterCategory = masterCategory;
    }

    public String getAmendedReliability() {
        return amendedReliability;
    }

    public void setAmendedReliability(String amendedReliability) {
        this.amendedReliability = amendedReliability;
    }

    public boolean isExists() {
        return isExists;
    }

    public void setExists(boolean exists) {
        isExists = exists;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public GuokrHandpickNewsAvatar getAvatar() {
        return avatar;
    }

    public void setAvatar(GuokrHandpickNewsAvatar avatar) {
        this.avatar = avatar;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

}
