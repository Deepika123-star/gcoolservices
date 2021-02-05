package com.smartwebarts.acrepair;

import android.app.Application;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MyApplication extends Application {

    private AppEventsLogger logger;

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.setAutoLogAppEventsEnabled(true);
        FacebookSdk.setAdvertiserIDCollectionEnabled(true);
        FacebookSdk.fullyInitialize();

        logger = AppEventsLogger.newLogger(getApplicationContext());
    }

    public void logLeonEvent (String eventName, String leon, double valToSum) {
        Bundle params = new Bundle();
        params.putString(eventName, leon);
        logger.logEvent(eventName, valToSum, params);
    }

    public void logBattleTheMonsterEvent () {
        logger.logEvent("BattleTheMonster");
    }

}
