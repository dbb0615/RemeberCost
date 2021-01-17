package com.ding.java.remembercost.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.ding.java.remembercost.Activity.Activity_CostRecordList;
import com.ding.java.remembercost.Adapter.Adapter_TypeList;
import com.ding.java.remembercost.R;

public class Dialog_SearchText {

	private Context m_Context = null;
	private AlertDialog.Builder m_Buider = null;
	private AlertDialog m_Dialog = null;
	private Adapter_TypeList m_Adapter_type = null;

    Button m_ButtonOK = null;
    Button m_ButtonCancel = null;
	AutoCompleteTextView m_EditText_SearchText = null;

	public Dialog_SearchText(Context context) {
		m_Context = context;
		m_Buider = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
	}

	public void show() {
		// 生成type
		View typepicker_view = LayoutInflater.from(m_Context).inflate(
				R.layout.dialog_searchtext, null, false );
		// 设置按钮响应
		m_ButtonOK = (Button) typepicker_view.findViewById(R.id.dialog_searchtext_button_ok);
		m_ButtonCancel = (Button) typepicker_view.findViewById(R.id.dialog_searchtext_button_cancel);
        m_EditText_SearchText = (AutoCompleteTextView) typepicker_view.findViewById(R.id.dialog_searchtext_edittext_searchtext);
		if (m_Context instanceof Activity_CostRecordList){
			m_EditText_SearchText.setText(((Activity_CostRecordList) m_Context).getSearchText());
		}
		m_ButtonOK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("ding_log","Dialog_SearchText ok clicked!");
				if (m_Context instanceof Activity_CostRecordList){
					((Activity_CostRecordList) m_Context).setSearchText(
							m_EditText_SearchText.getText().toString());
				}
				m_Dialog.dismiss();
			}
		});
		m_ButtonCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("ding_log","Dialog_SearchText cancel clicked!");
				m_Dialog.dismiss();
			}
		});

		// 显示对话框
		m_Buider.setView(typepicker_view);
		m_Dialog = m_Buider.show();

		// 取得屏幕像素
//		WindowManager wm = (WindowManager) m_Context.getSystemService(Context.WINDOW_SERVICE);
//		DisplayMetrics metrics = new DisplayMetrics();
//		wm.getDefaultDisplay().getMetrics(metrics);

		// 设置大小
//		WindowManager.LayoutParams params = m_Dialog.getWindow().getAttributes();
//		//params.width = metrics.widthPixels * 4 / 5;
//		params.height = metrics.heightPixels / 3;
//		m_Dialog.getWindow().setAttributes(params);
	}

}
