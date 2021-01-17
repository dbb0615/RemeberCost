package com.ding.java.remembercost.Activity;

//import java.sql.Date;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;

import com.ding.java.remembercost.R;

public class Activity_DatePicker extends BaseActivity {

	Button m_buttonOk = null;
	Button m_buttonCancel = null;
	DatePicker m_datePicker = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mydatepick);
		
		Log.i("ding_log", "Activity_DatePicker onCreate!");
		m_buttonOk = (Button) findViewById(R.id.my_datepicker_ok);
		m_buttonCancel = (Button) findViewById(R.id.my_datepicker_cancel);
		m_datePicker = (DatePicker) findViewById(R.id.my_datepicker_picker);

		int tmp_year = getIntent().getExtras().getInt("year");
		int tmp_month = getIntent().getExtras().getInt("month");
		int tmp_day = getIntent().getExtras().getInt("day");
		if ( 0 != tmp_year && 0 != tmp_month && 0 != tmp_day ) {
			m_datePicker.updateDate(tmp_year,tmp_month-1,tmp_day);
		}

		m_buttonOk.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 设置返回信息
				Intent ret_intent = new Intent();
				Bundle ret_bundle = new Bundle();
				//ret_bundle.putString( "date", selected_date_str );
				ret_bundle.putInt("year",m_datePicker.getYear());
				ret_bundle.putInt("month",m_datePicker.getMonth()+1);
				ret_bundle.putInt("day",m_datePicker.getDayOfMonth());
				ret_intent.putExtras(ret_bundle);
				ret_intent.setClass( Activity_DatePicker.this, Activity_Main.class );
				
				Log.i("ding_log", "setResult ok running!");
				setResult( RESULT_OK, ret_intent );
				finish();
			}
		});
		
		m_buttonCancel.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 设置返回信息
				Intent ret_intent = new Intent();
				ret_intent.setClass( Activity_DatePicker.this, Activity_Main.class );
				Log.i("ding_log", "setResult cancel running!");
				setResult( RESULT_CANCELED, ret_intent );
				finish();
			}
		});
		
	}
}
