package com.aero4te.tevogs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiReceiver extends BroadcastReceiver {
    @Override

    public void onReceive(Context context, Intent intent) {

        if (((ReadActivity)context).wifiConfig.SSID == null) { return; }

        final String action = intent.getAction();

        if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){

            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            // detailedState: DISCONNECTED | CONNECTING | AUTHENTICATING | OBTAINING_IPADDR | CONNECTED
            NetworkInfo.DetailedState detailedState = info.getDetailedState();
            // extraInfo: <unknown ssid> | "Jedek_2.4" | ... "SSID"
            String extraInfo = info.getExtraInfo();
            // connected: FALSE | TRUE
            boolean connected = info.isConnected();

            //<editor-fold desc="Log">
            Log.i("conn", "NetworkInfo " + info);
            Log.i("conn", "extraInfo " + extraInfo);
            Log.i("conn", "detailedState " + detailedState);
            Log.i("conn", "connected " + connected);
            Log.i("conn", "-------------------------------------------");
            //</editor-fold>

            boolean isPreferredWifi = extraInfo.equals(((ReadActivity)context).wifiConfig.SSID);
            if (!isPreferredWifi) {
                ((ReadActivity)context).reconectToWifiConfig();
            }
        }
    }
}