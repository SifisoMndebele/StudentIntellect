package com.ssmnd.studentintellect.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public final class NetworkObserver {
    private final MutableLiveData<Boolean> isOnline = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsOnline() {
        return isOnline;
    }

    public NetworkObserver(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder networkRequestBuilder = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            networkRequestBuilder.addCapability(NetworkCapabilities.NET_CAPABILITY_FOREGROUND);
        }
        NetworkRequest networkRequest = networkRequestBuilder.build();
        connectivityManager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                isOnline.postValue(true);
            }
            @Override
            public void onUnavailable() {
                super.onUnavailable();
                isOnline.postValue(false);
            }
            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                isOnline.postValue(false);
            }
        });
    }
}
