package com.bh.paperplane_study.license;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bh.paperplane_study.R;


public class OpenSourceLicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame);

        OpenSourceLicenseFragment fragment = OpenSourceLicenseFragment.newInstance();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment)
                .commit();

    }

}
