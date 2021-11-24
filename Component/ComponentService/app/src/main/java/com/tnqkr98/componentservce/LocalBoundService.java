package com.tnqkr98.componentservce;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class LocalBoundService extends Service {

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {   // 클라이언트 측에서 사용할 바인더
        public LocalBoundService getService(){  // 바인더를 통해 서비스의 인스턴스를 전달
            return LocalBoundService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {      // 바인드되면 실행되는 생명주기 메서드
        return mBinder;                         // 클라이언트에 바인더 전달(Connection 콜백에서 수신 처리)
    }

    public int getData(){       // 바인딩한 클라이언트가 사용가능한 메서드
        return 100;
    }
}