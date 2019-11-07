package yzw.ahaqth.accountbag;

import android.app.Application;
import android.content.res.Resources;

import org.litepal.LitePal;

import yzw.ahaqth.accountbag.tools.CrashHandler;

public class MyApApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        CrashHandler.getInstance().init(this);
    }

    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        if (resources != null && resources.getConfiguration().fontScale != 1.0f) {
            android.content.res.Configuration configuration = resources.getConfiguration();
            configuration.fontScale = 1.0f;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return resources;
    }
}
