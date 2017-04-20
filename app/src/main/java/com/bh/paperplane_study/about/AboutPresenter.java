
package com.bh.paperplane_study.about;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;

import com.bh.paperplane_study.R;
import com.bh.paperplane_study.license.OpenSourceLicenseActivity;

import static android.content.Context.MODE_PRIVATE;

public class AboutPresenter implements AboutContract.Presenter {

    private AboutContract.View view;
    private AppCompatActivity activity;
    private SharedPreferences sp;
    private CustomTabsIntent.Builder customTabsIntent;

    public AboutPresenter(AppCompatActivity activity, AboutContract.View view) {
        this.activity = activity;
        this.view = view;
        this.view.setPresenter(this);
        sp = activity.getSharedPreferences("user_settings",MODE_PRIVATE);

        customTabsIntent = new CustomTabsIntent.Builder();
        customTabsIntent.setToolbarColor(activity.getResources().getColor(R.color.colorPrimary));
        customTabsIntent.setShowTitle(true);
    }

    @Override
    public void start() {

    }


    @Override
    public void openLicense() {
        activity.startActivity(new Intent(activity,OpenSourceLicenseActivity.class));
    }

    // use CustomTabsIntent open the url first
    // if it get failed, use the inner browser
    // code can be simplified due to follow on github and follow on
    // zhihu code
    @Override
    public void followOnGithub() {
        try{
            activity.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse( activity.getString(R.string.github_url))));
        } catch (android.content.ActivityNotFoundException ex){
            view.showBrowserNotFoundError();
        }
    }


    @Override
    public void feedback() {
        try{
            Uri uri = Uri.parse(activity.getString(R.string.sendto));
            Intent intent = new Intent(Intent.ACTION_SENDTO,uri);
            intent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.mail_topic));
            intent.putExtra(Intent.EXTRA_TEXT,
                    activity.getString(R.string.device_model) + Build.MODEL + "\n"
                            + activity.getString(R.string.sdk_version) + Build.VERSION.RELEASE + "\n"
                            + activity.getString(R.string.version));
            activity.startActivity(intent);
        }catch (android.content.ActivityNotFoundException ex){
            view.showFeedbackError();
        }
    }


}
