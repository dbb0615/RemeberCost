package com.ding.java.remembercost.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import com.ding.java.remembercost.Activity.Activity_CostRecordList;
import com.ding.java.remembercost.Activity.BaseActivity_EdtiCostRecord;
import com.ding.java.remembercost.Adapter.Adapter_TypeList;
import com.ding.java.remembercost.R;

import java.util.ArrayList;

public class Dialog_TypeList {

	private Context m_Context = null;
	private AlertDialog.Builder m_Buider = null;
	private AlertDialog m_Dialog = null;
	private Adapter_TypeList m_Adapter_type = null;
	
	public Dialog_TypeList(Context context) {
		m_Context = context;
		m_Buider = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
	}

	public void show() {
		// 生成type
		View typepicker_view = LayoutInflater.from(m_Context).inflate(
				R.layout.typepicker, null, false );
		// 设置按钮响应
		Button ok_button = (Button) typepicker_view.findViewById(R.id.typepicker_ok);
		Button cancel_button = (Button) typepicker_view.findViewById(R.id.typepicker_reset);
		ok_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("ding_log","dialog_typelist ok clicked!");
				ArrayList<String> selected_str = m_Adapter_type.getSelectedType();
				String show_str = null;
				if ( 0 == selected_str.size() ) {
					Log.d("ding_log","dialog_typelist not select a type!");
					show_str = "";
				}
				else {
					Log.i("ding_log","dialog_typelist type:"+selected_str.toString());
					show_str = selected_str.get(0);
				}

				if (m_Context instanceof BaseActivity_EdtiCostRecord){
					((BaseActivity_EdtiCostRecord) m_Context).onDataChanged("type",show_str);
				} else if (m_Context instanceof Activity_CostRecordList) {
					((Activity_CostRecordList) m_Context).setType(show_str);
				}
				m_Dialog.dismiss();
			}
		});
		cancel_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("ding_log","dialog_typelist cancel clicked!");
			if (m_Context instanceof BaseActivity_EdtiCostRecord){
				((BaseActivity_EdtiCostRecord) m_Context).onDataChanged("type","");
			} else if (m_Context instanceof Activity_CostRecordList) {
				((Activity_CostRecordList) m_Context).setType("");
			}
				m_Dialog.dismiss();
			}
		});

		// 设置typelist信息
		ListView typelist_view = (ListView) typepicker_view.findViewById(R.id.typepicker_listview);
		typelist_view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		m_Adapter_type = new Adapter_TypeList(m_Context);
		typelist_view.setAdapter(m_Adapter_type);

		// 显示对话框
		m_Buider.setView(typepicker_view);
		m_Dialog = m_Buider.show();

		// 取得屏幕像素
		WindowManager wm = (WindowManager) m_Context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);

		// 设置大小
		WindowManager.LayoutParams params = m_Dialog.getWindow().getAttributes();
		//params.width = metrics.widthPixels * 4 / 5;
		params.height = metrics.heightPixels / 3;
		m_Dialog.getWindow().setAttributes(params);
	}


}
