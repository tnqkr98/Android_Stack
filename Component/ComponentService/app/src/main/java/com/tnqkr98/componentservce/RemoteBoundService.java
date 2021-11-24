package com.tnqkr98.componentservce;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RemoteBoundService extends Service {
    public RemoteBoundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}