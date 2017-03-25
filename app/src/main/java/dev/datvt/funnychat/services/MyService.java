package dev.datvt.funnychat.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import dev.datvt.funnychat.models.MyPlayer;

/**
 * Created by datvt on 4/24/2016.
 */
public class MyService extends Service {
    private MyPlayer myPlayer;
    private IBinder binder;

    // phương thức khởi tạo
    @Override
    public void onCreate() {
        Log.d("ServiceDemo", "Đã gọi onCreate()");

        myPlayer = new MyPlayer(this);
        binder = new MyBinder(); // do MyBinder được extends Binder
        super.onCreate();
    }

    // Bắt đầu một Service
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("ServiceDemo", "Đã gọi onBind()");
        myPlayer.play();
        // trả về đối tượng binder cho ActivityMain
        return binder;
    }

    // Kết thúc một Service
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("ServiceDemo", "Đã gọi onBind()");
        myPlayer.stop();
        return super.onUnbind(intent);
    }

    public void fastForward() {
        myPlayer.fastForward(60000);
    }

    public class MyBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }
}
