package com.bh.paperplane_study.bean;

import java.util.ArrayList;

public class ZhihuDailyNews {

    private String date;
    private ArrayList<Question> stories;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Question> getStories() {
        return stories;
    }

    public void setStories(ArrayList<Question> stories) {
        this.stories = stories;
    }

    public class Question {

        private ArrayList<String> images;
        private int type;
        private int id;
        private String ga_prefix;
        private String title;

        public ArrayList<String> getImages() {
            return images;
        }

        public void setImages(ArrayList<String> images) {
            this.images = images;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getGa_prefix() {
            return ga_prefix;
        }

        public void setGa_prefix(String ga_prefix) {
            this.ga_prefix = ga_prefix;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }

}
