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

import java.util.List;

/**
 *
 * Immutable model class for guokr handpick content result.
 */

public class GuokrHandpickContentResult {

    @Expose
    @SerializedName("image")
    private String image;

    @Expose
    @SerializedName("is_replyable")
    private boolean isReplyable;

    @Expose
    @SerializedName("channels")
    private List<GuokrHandpickContentChannel> channels;

    @Expose
    @SerializedName("channel_keys")
    private List<String> channelKeys;

    @Expose
    @SerializedName("preface")
    private String preface;

    @Expose
    @SerializedName("subject")
    private GuokrHandpickContentChannel subject;

    @Expose
    @SerializedName("copyright")
    private String copyright;

    @Expose
    @SerializedName("author")
    private GuokrHandpickNewsAuthor author;

    @Expose
    @SerializedName("image_description")
    private String imageDescription;

    @Expose
    @SerializedName("content")
    private String content;

    @Expose
    @SerializedName("is_show_summary")
    private boolean isShowSummary;

    @Expose
    @SerializedName("minisite_key")
    private String minisiteKey;

    @Expose
    @SerializedName("image_info")
    private GuokrHandpickContentImageInfo imageInfo;

    @Expose
    @SerializedName("subject_key")
    private String subjectKey;

    @Expose
    @SerializedName("minisite")
    private GuokrHandpickContentMinisite minisite;

    @Expose
    @SerializedName("tags")
    private List<String> tags;

    @Expose
    @SerializedName("date_published")
    private String datePublished;

    @Expose
    @SerializedName("replies_count")
    private int repliesCount;

    @Expose
    @SerializedName("is_author_external")
    private boolean isAuthorExternal;

    @Expose
    @SerializedName("recommends_count")
    private int recommendsCount;

    @Expose
    @SerializedName("title_hide")
    private String titleHide;

    @Expose
    @SerializedName("date_modified")
    private String dateModified;

    @Expose
    @SerializedName("url")
    private String url;

    @Expose
    @SerializedName("title")
    private String title;

    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("small_image")
    private String smallImage;

    @Expose
    @SerializedName("summary")
    private String summary;

    @Expose
    @SerializedName("ukey_author")
    private String ukeyAuthor;

    @Expose
    @SerializedName("date_created")
    private String dateCreated;

    @Expose
    @SerializedName("resource_url")
    private String resourceUrl;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isReplyable() {
        return isReplyable;
    }

    public void setReplyable(boolean replyable) {
        isReplyable = replyable;
    }

    public List<GuokrHandpickContentChannel> getChannels() {
        return channels;
    }

    public void setChannels(List<GuokrHandpickContentChannel> channels) {
        this.channels = channels;
    }

    public List<String> getChannelKeys() {
        return channelKeys;
    }

    public void setChannelKeys(List<String> channelKeys) {
        this.channelKeys = channelKeys;
    }

    public String getPreface() {
        return preface;
    }

    public void setPreface(String preface) {
        this.preface = preface;
    }

    public GuokrHandpickContentChannel getSubject() {
        return subject;
    }

    public void setSubject(GuokrHandpickContentChannel subject) {
        this.subject = subject;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public GuokrHandpickNewsAuthor getAuthor() {
        return author;
    }

    public void setAuthor(GuokrHandpickNewsAuthor author) {
        this.author = author;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isShowSummary() {
        return isShowSummary;
    }

    public void setShowSummary(boolean showSummary) {
        isShowSummary = showSummary;
    }

    public String getMinisiteKey() {
        return minisiteKey;
    }

    public void setMinisiteKey(String minisiteKey) {
        this.minisiteKey = minisiteKey;
    }

    public GuokrHandpickContentImageInfo getImageInfo() {
        return imageInfo;
    }

    public void setImageInfo(GuokrHandpickContentImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }

    public String getSubjectKey() {
        return subjectKey;
    }

    public void setSubjectKey(String subjectKey) {
        this.subjectKey = subjectKey;
    }

    public GuokrHandpickContentMinisite getMinisite() {
        return minisite;
    }

    public void setMinisite(GuokrHandpickContentMinisite minisite) {
        this.minisite = minisite;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }

    public int getRepliesCount() {
        return repliesCount;
    }

    public void setRepliesCount(int repliesCount) {
        this.repliesCount = repliesCount;
    }

    public boolean isAuthorExternal() {
        return isAuthorExternal;
    }

    public void setAuthorExternal(boolean authorExternal) {
        isAuthorExternal = authorExternal;
    }

    public int getRecommendsCount() {
        return recommendsCount;
    }

    public void setRecommendsCount(int recommendsCount) {
        this.recommendsCount = recommendsCount;
    }

    public String getTitleHide() {
        return titleHide;
    }

    public void setTitleHide(String titleHide) {
        this.titleHide = titleHide;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(String smallImage) {
        this.smallImage = smallImage;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUkeyAuthor() {
        return ukeyAuthor;
    }

    public void setUkeyAuthor(String ukeyAuthor) {
        this.ukeyAuthor = ukeyAuthor;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
