package com.ding.java.remembercost.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.AppOpsManagerCompat23;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ding.java.remembercost.R;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

/*
 * 实现窗口切换样式和禁用横屏显示
 */
public class BaseActivity extends Activity
        implements
        View.OnClickListener,
        View.OnFocusChangeListener{

    public static final String[] APP_PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WRITE_SETTINGS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置activity切换样式
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
        // 设置竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void onClick( View v ) {
        switch (v.getId()) {
            default:
                Log.i("ding_log", getClass().toString() +
                        " unknown view is clicked! " + v.toString());
                break;
        }
    }

    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            default:
                Log.i("ding_log", getClass().toString() +
                        "other view focus changed " + v.toString() + hasFocus );
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 权限检查
     * @return 0:无权限，且申请失败 1:已有权限 2:待确认
     */
    public int checkPermission( String tmp_permission) {
        if (ContextCompat.checkSelfPermission(this, tmp_permission) != PackageManager.PERMISSION_GRANTED) {
            if ( Build.VERSION.SDK_INT < 23 ) {
                Log.i("ding_log", "android device version " + Build.VERSION.SDK_INT +
                        " lower than 23, do not have this permission " + tmp_permission);
                return 0;
            }
            if ( ActivityCompat.shouldShowRequestPermissionRationale(this, tmp_permission) ) {
                Log.i("ding_log", "request "+tmp_permission+"!");
                return 2;
            } else {
                Toast.makeText(this, "request "+tmp_permission+" failed!", Toast.LENGTH_LONG).show();
                return 0;
            }
        }
        return  1;
    }
}
