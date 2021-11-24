package com.tnqkr98.componentservce;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.util.Log;

public class StartedService extends Service {
    private static final long SLEEP_TIME = 5000;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.d("Service","onCreate()");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service","onStartCommand");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final ResultReceiver receiver = intent.getParcelableExtra(Constant.EXTRA_RECEIVER);
                SystemClock.sleep(SLEEP_TIME);
                Log.d("Thread","Threading..");
                receiver.send(Constant.SYNC_PROGRESS,null);
                SystemClock.sleep(SLEEP_TIME);
                Log.d("Thread","Threading..");
                receiver.send(Constant.SYNC_PROGRESS,null);
                SystemClock.sleep(SLEEP_TIME);
                Log.d("Thread","Threading..");
                receiver.send(Constant.SYNC_PROGRESS,null);
                SystemClock.sleep(SLEEP_TIME);
                Log.d("Thread","Threading..");
                receiver.send(Constant.SYNC_PROGRESS,null);
                SystemClock.sleep(SLEEP_TIME);
                Log.d("Thread","Threading..");
                receiver.send(Constant.SYNC_COMPLETED,null);
                stopSelf();
            }
        });

        thread.start();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("Service","onDestroy()");
        super.onDestroy();
    }
}