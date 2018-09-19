package com.ourapps.scribefinder;
import android.app.Application;

public class MyConnection extends Application {

    private static MyConnection mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized MyConnection getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(connectivityReceiver.ConnectivityReceiverListener listener) {
        connectivityReceiver.connectivityReceiverListener = listener;
    }
}
