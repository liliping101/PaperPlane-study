package com.bh.paperplane_study.entity;

import com.bh.paperplane_study.bean.BeanType;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2017/4/11.
 */

@Entity
public class HistoryEntity {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private Integer contentId;

    private String news;
    private String content;
    private java.util.Date date;
    private Integer bookmark;
    @Convert(converter = BeanTypeConverter.class, columnType = String.class)
    private BeanType type;

    @Generated(hash = 1272611929)
    @Keep
    public HistoryEntity() {
    }

    @Generated(hash = 1686394253)
    @Keep
    public HistoryEntity(@NotNull Integer contentId, String news, String content, java.util.Date date, BeanType type) {
        this.contentId = contentId;
        this.news = news;
        this.content = content;
        this.date = date;
        this.type = type;
    }

    @Generated(hash = 2114219149)
    @Keep
    public HistoryEntity(Long id, @NotNull Integer contentId, String news, String content, java.util.Date date,
                         Integer bookmark, BeanType type) {
        this.id = id;
        this.contentId = contentId;
        this.news = news;
        this.content = content;
        this.date = date;
        this.bookmark = bookmark;
        this.type = type;
    }

    public Integer getContentId() {
        return contentId;
    }

    public void setContentId(Integer contentId) {
        this.contentId = contentId;
    }

    @NotNull
    public String getNews() {
        return news;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setNews(String news) {
        this.news = news;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public BeanType getType() {
        return type;
    }

    public void setType(BeanType type) {
        this.type = type;
    }

    public Integer getBookmark() {
        return bookmark;
    }

    public void setBookmark(Integer bookmark) {
        this.bookmark = bookmark;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}