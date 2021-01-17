package com.ding.java.remembercost.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by xiaobei on 2017/2/22.
 */

public class RCService extends Service {

    private RCServiceThread m_RCServiceThread = new RCServiceThread();
    private RCServiceBinder m_RCServiceBinder = new RCServiceBinder();

    @Override
    public void onCreate() {
        Log.i("ding_log","RCService onCreate");
        m_RCServiceThread.start();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.i("ding_log","RCService onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("ding_log","RCService onBind -- " + intent.toString());
        return m_RCServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("ding_log","RCService onUnbind -- " + intent.toString());
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i("ding_log","RCService onRebind -- " + intent.toString());
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ding_log","RCService onStartCommand -- " + intent.toString() + "#" +
            flags + "#" + startId );
        return super.onStartCommand(intent, flags, startId);
    }
}
