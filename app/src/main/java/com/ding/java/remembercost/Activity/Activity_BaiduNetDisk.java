package com.ding.java.remembercost.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.netdisk.sdk.BaiduYunCheckUtil;
import com.baidu.netdisk.sdk.NetDiskSDK;
import com.ding.java.remembercost.R;

public class Activity_BaiduNetDisk extends BaseActivity implements View.OnClickListener{

    private NetDiskSDK m_NetDiskSDK = com.baidu.netdisk.sdk.NetDiskSDK.getInstance();
    private Button m_Button_IsInstall = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidunetdisk);

        m_Button_IsInstall = (Button) findViewById(R.id.activity_baidunetdisk_isinstall);
        m_Button_IsInstall.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Log.i("ding_log","onClick -- "+v.toString());
        switch (v.getId()){
            case R.id.activity_baidunetdisk_isinstall:
                boolean is_install = BaiduYunCheckUtil.isNeedToDownloadBaiduYun(this);
                Toast.makeText(this, is_install ? "百度云需要安装" : "百度云不需要安装", Toast.LENGTH_SHORT).show();
                break;
            default:
                Log.i("ding_log","未知按钮点击："+v.toString());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        m_NetDiskSDK.doDestroy(this);
        super.onDestroy();
    }
}
