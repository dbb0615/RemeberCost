package com.ding.java.remembercost.Activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ding.java.remembercost.data.My_SqlLite;
import com.ding.java.remembercost.PopupWindow.PopupWin_DatePicker;
import com.ding.java.remembercost.R;
import com.ding.java.remembercost.Service.RCServiceBinder;
import com.ding.java.remembercost.Other.RC_Config;

public class Activity_Main extends BaseActivity_EdtiCostRecord {

	private AutoCompleteTextView m_EditText_IP = null;
	private EditText m_EditText_Port = null;
	private Button m_Button_Save = null;
	private Button m_Button_PopWin = null;
	private Button m_Button_Query = null;
	private Button m_Button_Weather = null;
	private Button m_Button_Baidu = null;
    private Button m_Button_Sport = null;
	private Button m_Button_BaiduApi = null;
	private My_SqlLite m_DataBase = null;

	private ServiceConnection m_ServiceConnection = new ServiceConnection() {
		private RCServiceBinder m_Binder = null;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
			m_Binder = (RCServiceBinder)service;
			Log.i("ding_log","onServiceConnected -- " + name.toString() + "\n" + service.toString());
        }

		public RCServiceBinder getBinder() {
			return m_Binder;
		}

        @Override
        public void onServiceDisconnected(ComponentName name) {
			m_Binder = null;
            Log.i("ding_log","onServiceDisconnected -- " + name.toString());
        }
    };
    private Intent m_ServiceIntent = null;

	private boolean SaveCostRecord() {

		if ( 0 != getYear() && 0 != getMonth() && 0 != getDay() &&
				0 != getType().length() && 0 != getSubType().length() &&
				R.integer.invalid_fee != getFee() ) {

			Log.i("ding_log", "input date:"+getDate());

            // 添加记录
            if ( false == m_DataBase.addCostRecord(getDate(),
                    getType(),
                    getSubType(),
                    getFee(),
                    getRemarks())
                    ) {
                Log.i("ding_log","add cost record failed!"+m_DataBase.getError() );
				return false;
            }

			Toast.makeText(getApplicationContext(),"存储信息到本地",Toast.LENGTH_SHORT).show();
        }
		else {
			Toast.makeText(getApplicationContext(),"信息不完整，请填写!",Toast.LENGTH_SHORT).show();
		}

		return true;
	}

	// 按钮的点击事件响应

	@Override
	public void onClick(View v) {
		switch ( v.getId() ) {
			case R.id.button_save :
				SaveCostRecord();
				break;
			case R.id.button_popwin :
				// 启动弹出窗口
				PopupWin_DatePicker tmp_pop = new PopupWin_DatePicker(
						Activity_Main.this,m_EditText_IP);
				tmp_pop.InitStatus();

				break;
			case R.id.button_query :
				Log.i("ding_log", "m_Button_Query onTouch");
				Intent tmp_intent = new Intent();
				Bundle tmp_bundle = new Bundle();
				tmp_bundle.putString("date",Integer.toString(getDate()));
				tmp_bundle.putString("subtype",getSubType());
				tmp_bundle.putString("remarks",getRemarks());
				tmp_intent.putExtras(tmp_bundle);
				tmp_intent.setClass( Activity_Main.this, Activity_CostRecordList.class );
				startActivityForResult(tmp_intent, R.integer.requestcode_query);
				break;
			case R.id.button_weather :
				Log.i("ding_log", "m_Button_Weather clicked");
				Intent weather_intent = new Intent();
				weather_intent.setClass( Activity_Main.this, Activity_Weather.class );
				startActivity(weather_intent);
				break;
			case R.id.button_baidu :
				Intent baidu_intent = new Intent(Intent.ACTION_VIEW);
				baidu_intent.setData(Uri.parse("http://www.baidu.com"));
				startActivity(baidu_intent);
				break;
            case R.id.button_sport :
                Log.i("ding_log", "m_Button_Sport clicked");
                Intent sport_intent = new Intent();
                sport_intent.setClass( Activity_Main.this, Activity_PositionRecord.class );
                startActivity(sport_intent);
                break;
			case R.id.button_baiduapi :
				Log.i("ding_log", "m_Button_BaiduApi clicked");
				Intent baiduapi_intent = new Intent();
				baiduapi_intent.setClass( Activity_Main.this, Activity_BaiduNetDisk.class );
				startActivity(baiduapi_intent);
				break;
			default :
				super.onClick(v);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int repeat_count = event.getRepeatCount();
		Log.d("ding_log","back key down, repeat count:"+repeat_count);
		if ( keyCode == KeyEvent.KEYCODE_BACK && 0 == repeat_count ) {
			exitApp();
		}
		return true;
	}

	private void exitApp() {
		///////////// 退出确认对话框  //////////////////////////////// 测试中。。。
		AlertDialog.Builder exit_builder = new AlertDialog.Builder(Activity_Main.this, AlertDialog.THEME_HOLO_LIGHT );
		exit_builder.setTitle("退出程序");
		exit_builder.setMessage("是否确定退出程序");
		exit_builder.setIcon(R.drawable.ic_launcher);
		exit_builder.setPositiveButton( "确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
//				int pid = android.os.Process.myPid();
//				android.os.Process.killProcess(pid);
				finish();
				Toast.makeText(getBaseContext(),"退出程序!",Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		});
		exit_builder.setNegativeButton( "取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getBaseContext(),"取消退出!",Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		});
		AlertDialog exit_dialog = exit_builder.show();
		exit_dialog.setCancelable(true);
		exit_dialog.setCanceledOnTouchOutside(false);
		///////////// 退出确认对话框  OVER /////////////////////////////

		////////////// 连续按两次退出代码 ////////////////////////
//		if ( System.currentTimeMillis() - exit_time > 2000 ) {
//			Toast.makeText(getBaseContext(),"再按一下退出程序",Toast.LENGTH_SHORT).show();
//			exit_time = System.currentTimeMillis();
//		}
//		else {
//			exit_dialog.dismiss();
//			finish();
//		}
		////////////// 连续按两次退出代码 OVER ////////////////////////
	}

	@Override
	protected void onDestroy() {
        unbindService(m_ServiceConnection);
//        stopService(m_ServiceIntent);
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_rc_main);
		super.onCreate(savedInstanceState);
		
		// 选择日期
		m_EditText_IP = (AutoCompleteTextView) findViewById(R.id.ip_port_ip_domain);
		m_EditText_Port = (EditText) findViewById(R.id.ip_port_port);
		ArrayAdapter<String> m_ListAdapter_IP = new ArrayAdapter<String>(
				Activity_Main.this,
				R.layout.arrayadapter_textview,
				R.id.arrayadapter_textview_t);
		m_ListAdapter_IP.add("192.168.0.100");
		m_ListAdapter_IP.add("193.168.0.100");
		m_ListAdapter_IP.add("194.168.0.100");
		m_EditText_IP.setAdapter(m_ListAdapter_IP);

		m_Button_Save = (Button) findViewById(R.id.button_save);
		m_Button_PopWin = (Button) findViewById(R.id.button_popwin);
		m_Button_Query = (Button) findViewById(R.id.button_query);
		m_Button_Weather = (Button) findViewById(R.id.button_weather);
		m_Button_Baidu = (Button) findViewById(R.id.button_baidu);
        m_Button_Sport = (Button) findViewById(R.id.button_sport);
		m_Button_BaiduApi = (Button) findViewById(R.id.button_baiduapi);

		requestMyPermission();

		// 设置数据文件路径
		RC_Config.init(getResources().getString(R.string.db_name),
				getResources().getString(R.string.ExportedData_name));
		m_DataBase = new My_SqlLite();

		// 保存信息
		m_Button_Save.setOnClickListener(this);
		// 弹出窗口
		m_Button_PopWin.setOnClickListener(this);
		// 查询界面
		m_Button_Query.setOnClickListener(this);
		// 天气预报
		m_Button_Weather.setOnClickListener(this);
		// 百度一下
		m_Button_Baidu.setOnClickListener(this);
        // 运动数据
        m_Button_Sport.setOnClickListener(this);
        // 百度api测试
		m_Button_BaiduApi.setOnClickListener(this);

		//m_ServiceIntent = new Intent(this,RCService.class);
//        startService(m_ServiceIntent);
        //bindService(m_ServiceIntent,m_ServiceConnection, Service.BIND_AUTO_CREATE);
	}

	private void requestMyPermission() {
		if ( Build.VERSION.SDK_INT >= 23 ) {
			String[] my_permissions = {"android.permission.WRITE_EXTERNAL_STORAGE"};
			int ret_permission = ContextCompat.checkSelfPermission(getApplicationContext(),
					my_permissions[0]);
			if ( ret_permission != PackageManager.PERMISSION_GRANTED ) {
				ActivityCompat.requestPermissions(this,my_permissions, 0);
			}
		}
	}

	public void onRequestPermissionsResult(
			int request_code, String[] permissions, int[] grantResults) {
			if ( 0 == request_code ) {
				if ( PackageManager.PERMISSION_GRANTED != grantResults[0] ) {
					Toast.makeText(this,"RequestPermissions failed!",Toast.LENGTH_LONG).show();
				}
				else {
					Toast.makeText(this,"RequestPermissions success!",Toast.LENGTH_LONG).show();
				}
			}
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		Log.i("ding_log", "onActivityResult "+requestCode+"|"+resultCode);
		// 查询列表
		if (R.integer.requestcode_query == requestCode) {
			switch (resultCode) {
				case RESULT_OK:
					break;
				default:
					Toast.makeText(Activity_Main.this,"未找到消费记录!",Toast.LENGTH_LONG).show();
					break;
			}
		}
		else
		{
			super.onActivityResult( requestCode, resultCode, data );
		}
	}

	// 选择日期对话框
	private void selectDateDialog() {
		// 生成日期选择view
		View pop_view = LayoutInflater.from(Activity_Main.this).inflate(
				R.layout.activity_mydatepick, null, false );
		Button ok_bt = (Button) pop_view.findViewById(R.id.my_datepicker_ok);
		Button cancel_bt = (Button) pop_view.findViewById(R.id.my_datepicker_cancel);

		// 生成对话框
		AlertDialog.Builder select_date_buider = new AlertDialog.Builder(Activity_Main.this,AlertDialog.THEME_HOLO_LIGHT);
		select_date_buider.setTitle("选择日期");
		select_date_buider.setView(pop_view);
		final AlertDialog select_date_dlg = select_date_buider.create();
		ok_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				select_date_dlg.dismiss();
			}
		});
		cancel_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				select_date_dlg.dismiss();
			}
		});

		select_date_dlg.show();
	}
}
