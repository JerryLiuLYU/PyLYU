package com.example.compus;

import android.app.Application;

import androidx.multidex.MultiDex;

import cn.leancloud.AVOSCloud;
import cn.leancloud.AVObject;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //MultiDex.install(this);
        AVOSCloud.initialize(this, "0kcJVchn5JVCOueqnzIJE2L4-gzGzoHsz",
                "q6rGVySOV1pR5e2juUOyVj78", "https://0kcjvchn.lc-cn-n1-shared.com");
    }
}
