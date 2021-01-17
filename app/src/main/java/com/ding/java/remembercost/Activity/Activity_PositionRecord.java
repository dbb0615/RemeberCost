package com.ding.java.remembercost.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ding.java.remembercost.R;

/**
 * Created by xiaobei on 2017/11/24.
 *
 */

public class Activity_PositionRecord extends BaseActivity
        implements View.OnClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        android.location.LocationListener{
    public static final int SPEED_MS = 0;  // 米每秒
    public static final int SPEED_KMH = 1; // 千米每秒
    public static final int GPS_UPDATE_TIME = 500;
    public static final float GPS_UPDATE_DISTANCE = 1;

    private TextView m_TextView_speed = null;
    private Button m_Button_SpeedUnit = null;
    private TextView m_TextView_longitude = null;
    private TextView m_TextView_latitude = null;
    private Button m_Button_OK = null;
    private Button m_Button_Test = null;
    private ListView m_ListView_Record = null;
    private LocationManager m_LocationManager = null;
    private int m_SpeedUnit = SPEED_MS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positionrecord);

        m_LocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

//        m_MockLocationProvider = new MockLocationProvider(LocationManager.GPS_PROVIDER, this);
//        m_LocationManager.addTestProvider(LocationManager.GPS_PROVIDER,false, false,
//                false, false, true, true, true, 0, 5);

        m_TextView_speed = (TextView) findViewById(R.id.activity_positionrecord_speed);
        m_Button_SpeedUnit = (Button) findViewById(R.id.activity_positionrecord_speedunit);
        m_TextView_longitude = (TextView) findViewById(R.id.activity_positionrecord_longitude);
        m_TextView_latitude = (TextView) findViewById(R.id.activity_positionrecord_latitude);
        m_Button_OK = (Button) findViewById(R.id.activity_positionrecord_ok);
        m_Button_Test = (Button) findViewById(R.id.activity_positionrecord_test);
        m_ListView_Record = (ListView) findViewById(R.id.activity_positionrecord_listview);

        m_Button_OK.setOnClickListener(this);
        m_Button_Test.setOnClickListener(this);
        m_Button_SpeedUnit.setOnClickListener(this);

        refreshButtonSpeedUnit();

        if ( 1 == checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) ) {
            /* 拥有GPS权限，设置GPS回调 */
            m_LocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    GPS_UPDATE_TIME, GPS_UPDATE_DISTANCE, this);
        }
    }

    @Override
    public void onClick(View v) {
        Log.i("ding_log", "onClick -- " + v.toString());
        switch (v.getId()) {
            case R.id.activity_positionrecord_ok:
                finish();
                break;
            case R.id.activity_positionrecord_test:
                if (PackageManager.PERMISSION_DENIED
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.i("ding_log", "无模拟位置权限");
                    Toast.makeText(this, "无模拟位置权限", Toast.LENGTH_LONG).show();
                } else {
                    Location loc = new Location(LocationManager.GPS_PROVIDER);
                    loc.setLatitude(37.422);
                    loc.setLongitude(-122.084);
                    loc.setSpeed(10.0f);
                    loc.setAltitude(0);
                    loc.setTime(System.currentTimeMillis());
                    loc.setAccuracy(10);
                    m_LocationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, loc);
                }
                break;
            case R.id.activity_positionrecord_speedunit:
                if (m_SpeedUnit == SPEED_KMH) m_SpeedUnit = SPEED_MS;
                else m_SpeedUnit = SPEED_KMH;
                refreshButtonSpeedUnit();
                break;
            default:
                Log.i("ding_log", "未知按钮点击：" + v.toString());
                break;
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        for ( int i = 0; i < grantResults.length; i++ ) {
            if ( permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION) ) {
                // WIFI和基站粗略位置信息
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("ding_log","获取粗略位置权限成功");
                } else {
                    Log.i("ding_log","获取粗略位置权限失败");
                    Toast.makeText(this,"获取粗略位置权限失败",Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                // GPS精确位置信息
                if ( grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                    Log.i("ding_log","获取精确位置权限成功");
                    if ( ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED ) {
                        Log.i("ding_log","requestLocationUpdates "+LocationManager.GPS_PROVIDER);
                        m_LocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                GPS_UPDATE_TIME, GPS_UPDATE_DISTANCE, this);
                    }
                } else {
                    Log.i("ding_log","获取精确位置权限失败");
                    Toast.makeText(this,"获取GPS精确位置权限失败",Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else if (0==1) {
                // 模拟位置信息
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("ding_log","获取模拟位置权限成功");
                    /* 拥有模拟位置权限 */
                    m_LocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER,true);
                    finish();
                } else {
                    Log.i("ding_log","获取模拟位置权限失败");
                    Toast.makeText(this,"获取模拟位置权限失败",Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.i("ding_log","其他请求 "+permissions[i].toString());
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("ding_log","onLocationChanged -- Latitude:"+location.getLatitude() +
                "|Longitude:" + location.getLongitude() );
        float now_speed = location.getSpeed();
        if ( m_SpeedUnit == SPEED_MS ) {
            m_TextView_speed.setText( String.format("速度:%.3f M/S",now_speed));
        } else if ( m_SpeedUnit == SPEED_KMH ) {
            m_TextView_speed.setText( String.format("速度:%.3f KM/H",now_speed*3.6));
        } else {
            m_TextView_speed.setText( String.format("速度:%.3f M/S",now_speed));
        }

        m_TextView_longitude.setText(String.format("经度:%.6f 度",location.getLongitude()));
        m_TextView_latitude.setText(String.format("纬度:%.6f 度",location.getLatitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i("ding_log","onStatusChanged -- provider "+provider+
                "| status -- " + status +
                "| extras -- " + extras.toString());
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i("ding_log","onProviderEnabled -- "+provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i("ding_log","onProviderDisabled -- "+provider);
    }

    public void refreshButtonSpeedUnit() {
        if ( m_SpeedUnit == SPEED_KMH ) {
            m_Button_SpeedUnit.setText(getResources().getString(R.string.speed_kmh));
        } else if ( m_SpeedUnit == SPEED_MS ) {
            m_Button_SpeedUnit.setText(getResources().getString(R.string.speed_ms));
        } else {
            m_Button_SpeedUnit.setText(getResources().getString(R.string.speed_ms));
        }
    }

    @Override
    protected void onDestroy() {
        m_LocationManager.removeUpdates(this);
        super.onDestroy();
    }
}
