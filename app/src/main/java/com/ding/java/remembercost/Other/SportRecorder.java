package com.ding.java.remembercost.Other;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Administrator on 2016/9/14.
 */
public class SportRecorder implements LocationListener {

    private LocationManager m_LocationManager = null;
    private float m_Speed = 0;
    public float GetSpeed() {
        return m_Speed;
    }
    private boolean m_IsActive = false;
    public boolean IsActive() {
        return m_IsActive;
    }
    private double m_Longitude = 0;// 经度
    public double GetLongitude() {
        return m_Longitude;
    }
    private double m_Latitude = 0;// 经度
    public double GetLatitude() {
        return m_Latitude;
    }

    SportRecorder( LocationManager lm ) {
        m_LocationManager = lm;
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch ( status ) {
            case LocationProvider.AVAILABLE :
                Log.i("ding_log", "onStatusChanged: GPS可见@"+provider);
                break;
            case LocationProvider.OUT_OF_SERVICE :
                Log.i("ding_log", "onStatusChanged: GPS不在服务区@"+provider);
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE :
                Log.i("ding_log", "onStatusChanged: GPS暂停服务@"+provider);
                break;
        }
    }
    // GPS开启
    public void onProviderEnabled(String provider) {
        m_IsActive = true;
    }
    // GPS关闭
    public void onProviderDisabled(String provider) {
        m_IsActive = false;
    }
    // 位置变化时
    public void onLocationChanged(Location location) {
        m_Speed = location.getSpeed();
        m_Longitude = location.getLongitude();
        m_Latitude = location.getLatitude();
    }
}
