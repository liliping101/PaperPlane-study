package com.bh.paperplane_study.application;

/**
 * Created by Administrator on 2017/4/11.
 */

import android.app.Application;

import com.bh.paperplane_study.gen.DaoMaster;
import com.bh.paperplane_study.gen.DaoSession;

import org.greenrobot.greendao.database.Database;

public class App extends Application {
    /**
     * A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher.
     */
    public static final boolean ENCRYPTED = false;

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,ENCRYPTED ? "historys-db-encrypted" : "historys-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}

