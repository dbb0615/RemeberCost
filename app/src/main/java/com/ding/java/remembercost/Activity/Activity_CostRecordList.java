package com.ding.java.remembercost.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ding.java.remembercost.Adapter.Adapter_CostRecordList;
import com.ding.java.remembercost.Other.RC_Config;
import com.ding.java.remembercost.data.CostRecord;
import com.ding.java.remembercost.Dialog.Dialog_DatePicker;
import com.ding.java.remembercost.Dialog.Dialog_SearchText;
import com.ding.java.remembercost.Dialog.Dialog_SendData;
import com.ding.java.remembercost.Dialog.Dialog_TypeList;
import com.ding.java.remembercost.data.My_SqlLite;
import com.ding.java.remembercost.PopupWindow.PopupWin_Add;
import com.ding.java.remembercost.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Activity_CostRecordList extends BaseActivity {

	public static final int RECORD_VIEW_NORMAL = 0;
	public static final int RECORD_VIEW_MONTH = 1;
	public static final int RECORD_VIEW_DAY = 2;
	private TextView m_TextView_Loading = null;
	private Button m_Button_FilterDate = null;
	private Button m_Button_FilterType = null;
	private Button m_Button_FilterText = null;
	private Button m_Button_Export = null;
	private int m_BeginDate = 0;
	private int m_EndDate = 0;
	private String m_Type = null;
	private Button m_Button_OK = null;
	private Button m_Button_Normal_View = null;
	private Button m_Button_Month_View = null;
	private Button m_Button_Day_View = null;
	private int m_RecordViewType = RECORD_VIEW_NORMAL;
	private ListView m_ListView_Record = null;
	private List<CostRecord> m_CostRecordList = null;
	private Adapter_CostRecordList m_CostRecordListAdapter = null;
	private float m_TotalMoney = 0;
	private My_SqlLite m_DataBase = null;
	private PopupWindow m_EditPopupWin = null;
	private String m_SearchText = null;
	PopupWin_Add m_PopupWin_Add = null;  // 编辑记录弹出窗口
	PopupWindow m_PopupWindow_TypePicker = null; // 类型选择弹出窗口

	// 为了实现activity出现后出现弹出窗口
	private Handler m_Handler = new Handler();
	private Runnable m_Runnable_LoadData = new Runnable() {
		@Override
		public void run() {
			m_CostRecordList.clear();
			List<CostRecord> tmp_recordlist = getCostRecord();
			// 没有记录
			if ( null == tmp_recordlist ) {
				Log.i("ding_log","cost record is null!");
			} else {
				m_CostRecordList.addAll(tmp_recordlist);
			}
			m_CostRecordListAdapter.notifyDataSetChanged();

			if ( null == m_PopupWin_Add ) {
				m_PopupWin_Add = new PopupWin_Add( Activity_CostRecordList.this, m_Button_OK );
			}
			if ( !m_PopupWin_Add.isShowing() ) {
				m_PopupWin_Add.show();
			}

			// 数据加载完毕，隐藏提示正在加载数据
			//m_TextView_Loading.setVisibility(View.GONE);
			m_TextView_Loading.setText(getResources().getString(R.string.TotalMoney)+
					String.format("%.2f", m_TotalMoney));
		}
	};

	private Runnable m_Runnable_ExportData = new Runnable() {
		@Override
		public void run() {
			File export_file = new File(RC_Config.getExportedFile());
			if (true == export_file.exists()) export_file.delete();
			try {
				export_file.createNewFile();
				FileOutputStream out_stream = new FileOutputStream(export_file);
				String tmp_str = "";
				for (CostRecord tmp_rec:m_CostRecordList) {
					tmp_str = tmp_rec.m_Date + "^" +
							tmp_rec.m_Type + "^" +
							tmp_rec.m_SubType + "^" +
							tmp_rec.m_Fee + "^" +
							tmp_rec.m_Remarks + "\n";
					out_stream.write(tmp_str.getBytes());
				}
				out_stream.close();
			} catch (IOException e) {
				Toast.makeText(Activity_CostRecordList.this,"write file---"+e.toString(),Toast.LENGTH_SHORT).show();
			}
			Toast.makeText(Activity_CostRecordList.this,"文件导出成功！",Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_costrecord);

		m_TextView_Loading = (TextView) findViewById(R.id.my_costrecord_textview_loading);
		m_Button_OK = (Button) findViewById(R.id.my_costrecord_ok);
		m_Button_Normal_View = (Button) findViewById(R.id.my_costrecord_normal_view);
		m_Button_Month_View = (Button) findViewById(R.id.my_costrecord_month_view);
		m_Button_Day_View = (Button) findViewById(R.id.my_costrecord_day_view);
		m_ListView_Record = (ListView) findViewById(R.id.my_costrecord_listview);
		m_Button_FilterDate = (Button) findViewById(R.id.filter_head_select_date);
		m_Button_FilterType = (Button) findViewById(R.id.filter_head_select_type);
		m_Button_FilterText = (Button) findViewById(R.id.filter_head_select_text);
		m_Button_Export = (Button) findViewById(R.id.my_costrecord_export);

		m_Button_OK.setOnClickListener(this);
		m_Button_Normal_View.setOnClickListener(this);
		m_Button_Month_View.setOnClickListener(this);
		m_Button_Day_View.setOnClickListener(this);
		m_Button_FilterDate.setOnClickListener(this);
		m_Button_FilterType.setOnClickListener(this);
		m_Button_FilterText.setOnClickListener(this);
		m_Button_Export.setOnClickListener(this);

		// 获取要显示的记录
		m_DataBase = new My_SqlLite();
		String search_date = getIntent().getExtras().getString("date");
		m_SearchText = getIntent().getExtras().getString("remarks");
		
		int tmp_date = 0;
		try {
			tmp_date = Integer.valueOf(search_date).intValue();
		} catch (NumberFormatException e) {
			Toast.makeText(Activity_CostRecordList.this,"date invalid!---"+e.toString(),Toast.LENGTH_SHORT).show();
		}
		m_BeginDate = tmp_date;
		m_EndDate = tmp_date;

		// 生成list
		createRecordList();

	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.my_costrecord_ok :
				// 设置返回信息
				Intent ret_intent = new Intent();
				ret_intent.setClass( Activity_CostRecordList.this, Activity_Main.class );
				setResult( RESULT_OK, ret_intent );
				finish();
				break;
			case R.id.my_costrecord_normal_view :
				if ( RECORD_VIEW_MONTH == m_RecordViewType ||
						RECORD_VIEW_DAY == m_RecordViewType ) {
					m_RecordViewType = RECORD_VIEW_NORMAL;
					CostRecord tmp_record = m_CostRecordListAdapter.getSelectedItem();
					if ( null == tmp_record ) {
						reloadRecord();
					} else {
						setDate( tmp_record.m_Date, tmp_record.m_Date );
					}
				}
				break;
			case R.id.my_costrecord_month_view :
				m_RecordViewType = RECORD_VIEW_MONTH;
				reloadRecord();
				break;
			case R.id.my_costrecord_day_view :
				if ( RECORD_VIEW_MONTH == m_RecordViewType ) {
					m_RecordViewType = RECORD_VIEW_DAY;
					Calendar calendar = Calendar.getInstance();
					CostRecord tmp_record = m_CostRecordListAdapter.getSelectedItem();
					if (null != tmp_record) {
						calendar.set(Calendar.YEAR, tmp_record.m_Date / 100);
						calendar.set(Calendar.MONTH, tmp_record.m_Date % 100 - 1);
						int first_day = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
						int last_day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
						setDate(tmp_record.m_Date * 100 + first_day,
								tmp_record.m_Date * 100 + last_day);
					} else {
						reloadRecord();
					}
				} else {
					m_RecordViewType = RECORD_VIEW_DAY;
					reloadRecord();
				}
				break;
			case R.id.filter_head_select_date :
				// 设置返回信息
				Dialog_DatePicker tmp_picker = new Dialog_DatePicker(this);
				tmp_picker.show();
				break;
			case R.id.filter_head_select_type :
				Dialog_TypeList type_dialog = new Dialog_TypeList(this);
				type_dialog.show();
//				showTypePicker();
				break;
			case R.id.filter_head_select_text :
				Dialog_SearchText searchtext = new Dialog_SearchText(this);
				searchtext.show();
				break;
			case R.id.typepicker_ok :
				m_PopupWindow_TypePicker.dismiss();
				break;
			case R.id.typepicker_reset:
				m_PopupWindow_TypePicker.dismiss();
				break;
			case R.id.my_costrecord_export:
				exportCostRecord();
				break;
			default:
				Log.i("ding_log",getClass().toString()+"unknown view is clicked!"+v.toString());
				super.onClick(v);
				break;
		}
	}
	// 生成list
	private void createRecordList() {
		m_ListView_Record.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		m_CostRecordList = new ArrayList<CostRecord>();
		m_CostRecordListAdapter = new Adapter_CostRecordList(Activity_CostRecordList.this, m_CostRecordList);
		m_ListView_Record.setAdapter(m_CostRecordListAdapter);

		m_ListView_Record.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				m_CostRecordListAdapter.setSelectedPosition(position);
				m_CostRecordListAdapter.notifyDataSetChanged();

				int tmp_height = m_Button_OK.getMeasuredHeight()+ ((int) getResources().getDimension(R.dimen.doubleblank_size));
				popupEditWindow(parent, tmp_height );
				return true;
			}
		});

		m_ListView_Record.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				m_CostRecordListAdapter.setSelectedPosition(position);
				m_CostRecordListAdapter.notifyDataSetChanged();
			}
		});

		m_ListView_Record.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.d("ding_log",view.toString()+"|"+scrollState);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				Log.d("ding_log",view.toString()+"|"+firstVisibleItem+"|"+visibleItemCount+"|"+totalItemCount );
			}
		});

	}


	// 弹出编辑记录窗口
	private boolean popupEditWindow(View p_view, int height ) {

		Context tmp_context = getBaseContext();
		m_EditPopupWin = new PopupWindow();

		// 取得屏幕像素
		WindowManager wm = (WindowManager) tmp_context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);

		// 创建弹出窗口视图
		View pop_view = LayoutInflater.from(tmp_context).inflate(
				R.layout.popupwin_editrecord, null, false );
		Button delete_button = (Button) pop_view.findViewById(R.id.PopupWin_EditRecord_Delete);
		Button edit_button = (Button) pop_view.findViewById(R.id.PopupWin_EditRecord_Edit);
		// 设置button的响应事件
		delete_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("ding_log","delete button clicked!" );
				deleteRecord();
				m_EditPopupWin.dismiss();
				m_EditPopupWin = null;
			}
		});
		edit_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editRecord();
			}
		});

		m_EditPopupWin.setContentView(pop_view);

		m_EditPopupWin.setFocusable(true);
		m_EditPopupWin.setOutsideTouchable(true);
		// popupwindow设置背景后才可以点击外部消失
		m_EditPopupWin.setBackgroundDrawable(new BitmapDrawable());

		// 宽度设置为屏幕的2/3
		m_EditPopupWin.setWidth(metrics.widthPixels);
		// 高度设置为文本的高度
		m_EditPopupWin.setHeight(height);

		m_EditPopupWin.setAnimationStyle(R.style.popdown_anim_style);

		// 设置好参数之后再show
		m_EditPopupWin.showAtLocation(p_view,
				Gravity.NO_GRAVITY,
				0,
				metrics.heightPixels - height );
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i("ding_log", "onActivityResult "+requestCode+"|"+resultCode);

		if ( requestCode == R.integer.requestcode_editdata ) {
			if (RESULT_OK == resultCode) {
				Log.i("ding_log", "ok "+resultCode);
			} else {
				Log.i("ding_log", "cancel "+resultCode);
			}
		} else {
			Log.w("ding_log", "unknown requestcode "+requestCode);
		}
		// 使编辑框消失
		if ( null != m_EditPopupWin && m_EditPopupWin.isShowing() ) {
			m_EditPopupWin.dismiss();
		}
	}

	// 删除记录
	public boolean deleteRecord() {
		// 开始加载数据，提示正在加载数据
		//m_TextView_Loading.setVisibility(View.VISIBLE);

		CostRecord tmp_record = m_CostRecordListAdapter.getSelectedItem();
		if ( null == tmp_record ) {
			Toast.makeText(Activity_CostRecordList.this,"please select an item!",
					Toast.LENGTH_LONG).show();
			return true;
		}
		if ( false == m_DataBase.deleteCostRecord(tmp_record.m_Sequence) ) {
			Log.i("ding_log","delete record failed!:"+m_DataBase.getError());
			Toast.makeText(Activity_CostRecordList.this,
					"delete record failed!:"+m_DataBase.getError(),Toast.LENGTH_LONG).show();
			return false;
		}

		m_Handler.post(m_Runnable_LoadData);
		return true;
	}
	// 编辑记录
	public boolean editRecord() {
		CostRecord tmp_record = m_CostRecordListAdapter.getSelectedItem();
		if ( null == tmp_record ) {
			Toast.makeText(Activity_CostRecordList.this,"please select an item!",
					Toast.LENGTH_SHORT).show();
			return true;
		}
		Log.i("ding_log","edit button clicked!"+tmp_record.m_Sequence );

		Intent tmp_intent = new Intent();
		Bundle tmp_bundle = new Bundle();
		tmp_bundle.putLong(getResources().getString(R.string.cost_sequence),tmp_record.m_Sequence);
		tmp_bundle.putInt(getResources().getString(R.string.cost_date),tmp_record.m_Date);
		tmp_bundle.putString(getResources().getString(R.string.cost_type),tmp_record.m_Type);
		tmp_bundle.putString(getResources().getString(R.string.cost_subtype),tmp_record.m_SubType);
		tmp_bundle.putFloat(getResources().getString(R.string.cost_fee),tmp_record.m_Fee);
		tmp_bundle.putString(getResources().getString(R.string.cost_remarks),tmp_record.m_Remarks);
		tmp_intent.putExtras(tmp_bundle);
		tmp_intent.setClass( Activity_CostRecordList.this, Activity_EditData.class );
		startActivityForResult(tmp_intent, R.integer.requestcode_editdata);
		return true;
	}
	// 添加记录
	public boolean addRecord() {
		Intent tmp_intent = new Intent();
		tmp_intent.setClass( Activity_CostRecordList.this, Activity_EditData.class );
		startActivityForResult(tmp_intent, R.integer.requestcode_editdata);
		return true;
	}
	// 发送同步信息
	public boolean sendRecord() {
		Dialog_SendData tmp_senddata = new Dialog_SendData(Activity_CostRecordList.this);
		tmp_senddata.show();
		return true;
	}
	// 重新加载记录
	public boolean reloadRecord() {
		// 开始加载数据，提示正在加载数据
		/* if ( !m_TextView_Loading.isShown() )
			m_TextView_Loading.setVisibility(View.VISIBLE);*/
		m_TextView_Loading.setText(getResources().getString(R.string.loading));
		m_Handler.postDelayed(m_Runnable_LoadData, 100);
		return true;
	}

	protected void onRestart() {
		super.onRestart();
		Log.i("ding_log", "onRestart running!");
	}

	protected void onResume() {
		super.onResume();
		Log.i("ding_log", "onResume running!");
		reloadRecord();
	}

	public List<CostRecord> getCostRecord() {
		String[] types = null;
		m_TotalMoney = 0;
		if ( null == m_Type || 0 == m_Type.length() ) {
			types = new String[]{"交通","衣服","网购","吃喝","话费","饭菜","水果果","生病病","杂乱","随礼","水电煤"};
		} else {
			types = new String[]{m_Type};
		}
//		m_SearchText = m_Button_FilterText.getText().toString();
		List<CostRecord> ret_list = m_DataBase.getCostRecord_test(m_BeginDate,m_EndDate,types, m_SearchText, null);
		List<CostRecord> month_list = null;
		CostRecord tmp_cost_record = null;
		// 月视图，整理数据
		if ( RECORD_VIEW_MONTH == m_RecordViewType ) {
			month_list = new ArrayList<CostRecord>();
			for (int i = 0; i < ret_list.size(); i++) {
				int tmp_month = (ret_list.get(i).m_Date / 100);
				float tmp_fee = ret_list.get(i).m_Fee;
				// 第一个或者日期变化
				if ( null == tmp_cost_record || tmp_cost_record.m_Date != tmp_month ) {
					if ( null != tmp_cost_record )  month_list.add(tmp_cost_record);
					tmp_cost_record = new CostRecord();
					tmp_cost_record.m_Date = tmp_month;
					tmp_cost_record.m_Fee = tmp_fee;
				}
				else tmp_cost_record.m_Fee += tmp_fee;
				m_TotalMoney += tmp_fee;
			}
			if ( null != tmp_cost_record )  month_list.add(tmp_cost_record);
			ret_list = month_list;
		} else if ( RECORD_VIEW_DAY == m_RecordViewType ) {
			month_list = new ArrayList<CostRecord>();
			for (int i = 0; i < ret_list.size(); i++) {
				int tmp_month = ret_list.get(i).m_Date;
				float tmp_fee = ret_list.get(i).m_Fee;
				// 第一个或者日期变化
				if (null == tmp_cost_record || tmp_cost_record.m_Date != tmp_month) {
					if (null != tmp_cost_record) month_list.add(tmp_cost_record);
					tmp_cost_record = new CostRecord();
					tmp_cost_record.m_Date = tmp_month;
					tmp_cost_record.m_Fee = tmp_fee;
				} else tmp_cost_record.m_Fee += tmp_fee;
				m_TotalMoney += tmp_fee;
			}
				if ( null != tmp_cost_record )  month_list.add(tmp_cost_record);
				ret_list = month_list;
		} else {
			for (CostRecord tmp_rec : ret_list) {
				m_TotalMoney += tmp_rec.m_Fee;
			}
		}

		return ret_list;
	}

	public void setDate(int begin_date, int end_date) {
		if ( begin_date < 10000000 || begin_date > 99999999 ) {
			m_BeginDate = 0;
		} else {
			m_BeginDate = begin_date;
		}
		if ( end_date < 10000000 || end_date > 99999999 ) {
			m_EndDate = 0;
		} else {
			m_EndDate = end_date;
		}
		m_Button_FilterDate.setText(m_BeginDate+"\n-\n"+m_EndDate);

		reloadRecord();
	}

	public void setType(String type) {
		if ( null != type ) {
			m_Button_FilterType.setText(type);
		} else {
			m_Button_FilterType.setText("");
		}
		m_Type = type;

		reloadRecord();
	}

	public void setSearchText(String search_text) {
		if ( null != search_text ) {
			m_Button_FilterText.setText(search_text);
		} else {
			m_Button_FilterText.setText("");
		}
		m_SearchText = search_text;

		reloadRecord();
	}

	public String getSearchText() {
        if ( null == m_SearchText ) return  "";
        return m_SearchText;
    }

    private void exportCostRecord() {
		Toast.makeText(Activity_CostRecordList.this,"开出导出",Toast.LENGTH_SHORT).show();
		m_Handler.post(m_Runnable_ExportData);
	}
}
