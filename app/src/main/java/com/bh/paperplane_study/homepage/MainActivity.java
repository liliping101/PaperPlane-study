package com.bh.paperplane_study.homepage;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.bh.paperplane_study.Bookmarks.BookmarksFragment;
import com.bh.paperplane_study.Bookmarks.BookmarksPresenter;
import com.bh.paperplane_study.R;
import com.bh.paperplane_study.about.AboutPreferenceActivity;
import com.bh.paperplane_study.service.CacheService;
import com.bh.paperplane_study.settings.SettingsPreferenceActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private MainFragment mainFragment;
    private BookmarksFragment bookmarksFragment;

    public static final String ACTION_BOOKMARKS = "com.marktony.zhihudaily.bookmarks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initFragment(savedInstanceState);

        String action = getIntent().getAction();
        if (action.equals(ACTION_BOOKMARKS)) {
            showBookmarksFragment();
            navView.setCheckedItem(R.id.nav_bookmarks);
        } else {
            showMainFragment();
            navView.setCheckedItem(R.id.nav_home);
        }

        startService(new Intent(this, CacheService.class));
    }

    private void showMainFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.show(mainFragment);
        transaction.hide(bookmarksFragment);
        transaction.commit();

        toolbar.setTitle(R.string.app_name);
    }

    private void showBookmarksFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.show(bookmarksFragment);
        transaction.hide(mainFragment);
        transaction.commit();

        toolbar.setTitle(R.string.nav_bookmarks);

        if (bookmarksFragment.isAdded()) {
            bookmarksFragment.notifyDataChanged();
        }
    }

    private void initFragment(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mainFragment = (MainFragment) getSupportFragmentManager().getFragment(savedInstanceState, "MainFragment");
            bookmarksFragment = (BookmarksFragment) getSupportFragmentManager().getFragment(savedInstanceState, "BookmarksFragment");
        } else {
            mainFragment = MainFragment.newInstance();
            bookmarksFragment = BookmarksFragment.newInstance();
        }

        if (!mainFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.layout_fragment, mainFragment, "MainFragment")
                    .commit();
        }

        if (!bookmarksFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.layout_fragment, bookmarksFragment, "BookmarksFragment")
                    .commit();
        }

        new BookmarksPresenter(MainActivity.this, bookmarksFragment);
    }

    private void initView() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            showMainFragment();
        } else if (id == R.id.nav_bookmarks) {
            showBookmarksFragment();
        } else if (id == R.id.nav_change_theme) {
            drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(View drawerView) {

                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    SharedPreferences sp = getSharedPreferences("user_settings", MODE_PRIVATE);
                    if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                            == Configuration.UI_MODE_NIGHT_YES) {
                        sp.edit().putInt("theme", 0).apply();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    } else {
                        sp.edit().putInt("theme", 1).apply();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                    getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
                    recreate();
                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsPreferenceActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutPreferenceActivity.class));
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(mainFragment.isAdded())
        {
            getSupportFragmentManager().putFragment(outState, "MainFragment", mainFragment);
        }
        if(bookmarksFragment.isAdded())
        {
            getSupportFragmentManager().putFragment(outState, "BookmarksFragment", bookmarksFragment);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo serviceInfo:activityManager.getRunningServices(Integer.MAX_VALUE))
        {
            if(CacheService.class.getName().equals(serviceInfo.service.getClassName()))
            {
                stopService(new Intent(this, CacheService.class));
            }
        }
        super.onDestroy();
    }
}
