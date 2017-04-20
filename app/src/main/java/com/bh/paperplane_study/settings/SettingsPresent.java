package com.bh.paperplane_study.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.preference.Preference;

import com.bh.paperplane_study.R;
import com.bumptech.glide.Glide;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2017/3/10.
 */

public class SettingsPresent implements SettingsContract.Presenter{
    private Context context;
    private SettingsContract.View view;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public static final int CLEAR_GLIDE_CACHE_DONE = 1;

    public SettingsPresent(Context context, SettingsContract.View view) {
        this.context = context;
        this.view = view;
        this.view.setPresenter(this);
        sp = context.getSharedPreferences("user_setting", MODE_PRIVATE);
        editor = sp.edit();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CLEAR_GLIDE_CACHE_DONE:
                    view.showcleanGlideCache();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void setNoPictureMode(Preference preference) {
        editor.putBoolean("no_picture_mode",
                preference.getSharedPreferences().getBoolean("no_picture_mode", false));
        editor.apply();
    }

    @Override
    public void setInAppBrowser(Preference preference) {
        editor.putBoolean("in_app_browser",
                preference.getSharedPreferences().getBoolean("in_app_browser", false));
        editor.apply();
    }

    @Override
    public void cleanGlideCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
                Message msg = new Message();
                msg.what = CLEAR_GLIDE_CACHE_DONE;
                handler.sendMessage(msg);
            }
        }).start();
        Glide.get(context).clearMemory();
    }

    @Override
    public void setTimeOfSavingArticles(Preference preference, Object newValue) {
        editor.putString("time_of_saving_articles", (String)newValue);
        editor.apply();
    }

    @Override
    public String getTimeSummary() {
        String[] options = context.getResources().getStringArray(R.array.time_to_save_opts);
        String str = sp.getString("time_of_saving_articles", "7");
        switch (str) {
            case "1":
                return options[0];
            case "3":
                return options[1];
            case "15":
                return options[3];
            default:
                return options[2];

        }
    }

    @Override
    public void start() {

    }
}
