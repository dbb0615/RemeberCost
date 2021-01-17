package com.ding.java.remembercost.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ding.java.remembercost.data.CostRecord;
import com.ding.java.remembercost.R;
import com.ding.java.remembercost.Other.RemarksButton;

import java.util.List;


public class Adapter_CostRecordList extends BaseAdapter {

	private LayoutInflater m_Inflater = null;
	private List<CostRecord> m_CostRecordList;
	private Context m_Context = null;
	private int m_SelectedPos;

	public Adapter_CostRecordList(Context context, List<CostRecord> costrecordlist) {
		// TODO Auto-generated constructor stub
		m_Context = context;
		m_Inflater = LayoutInflater.from(context);
		m_CostRecordList = costrecordlist;
		m_SelectedPos = -1;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return m_CostRecordList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	class ViewHolder {
		TextView m_TextView_Date;
		TextView m_TextView_Type;
		TextView m_TextView_SubType;
		TextView m_TextView_Fee;
		RemarksButton m_Button_Remarks;
	}

	/* 通过日期获取当日总费用 */
	private float getCostByDate( String i_date ) {
		int list_size = m_CostRecordList.size();
		float ret_fee = 0;
		CostRecord tmp_record = null;
		for ( int i = 0; i < list_size; i++ ) {
			tmp_record = m_CostRecordList.get(i);
			if ( 0 == i_date.compareTo(Integer.toString(tmp_record.m_Date)) ) {
				ret_fee += tmp_record.m_Fee;
			}
		}
		return ret_fee;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder tmp_viewholder = null;
		if ( null == convertView ) {
			try {
				convertView = m_Inflater.inflate(R.layout.costrecordcell, parent, false);
			} catch (InflateException e) {
				Log.i("ding_log",e.getCause()+"||"+e.getMessage());
				return null;
			} catch (Exception e) {
				Log.i("ding_log",e.getCause()+"||"+e.getMessage());
				return null;
			}
			tmp_viewholder = new ViewHolder();
			tmp_viewholder.m_TextView_Date = (TextView) convertView.findViewById(R.id.CostRecordCell_Date);
			/* 点击日期输出当日总费用 */
			tmp_viewholder.m_TextView_Date.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String tmp_date = ((TextView)(v)).getText().toString();
					float tmp_fee = getCostByDate(tmp_date);
					Toast.makeText( m_Context, tmp_date + "总费用:" + String.format("%.2f",tmp_fee),
							Toast.LENGTH_LONG ).show();
				}
			});
			tmp_viewholder.m_TextView_Type = (TextView) convertView.findViewById(R.id.CostRecordCell_Type);
			tmp_viewholder.m_TextView_SubType = (TextView) convertView.findViewById(R.id.CostRecordCell_SubType);
			tmp_viewholder.m_TextView_Fee = (TextView) convertView.findViewById(R.id.CostRecordCell_Fee);
			tmp_viewholder.m_Button_Remarks = (RemarksButton) convertView.findViewById(R.id.CostRecordCell_Remarks);
			convertView.setTag(tmp_viewholder);
		}
		else {
			tmp_viewholder = (ViewHolder) convertView.getTag();
		}

		// 设置item选中状态
		if ( m_SelectedPos == position ) {
			convertView.setBackgroundResource(R.drawable.item_bg_selected);
		}
		else
		{
			convertView.setBackgroundResource(R.drawable.item_bg_normal);
		}

		CostRecord tmp_record = m_CostRecordList.get(position);
		if ( null != tmp_record ) {
			tmp_viewholder.m_TextView_Date.setText(String.format("%d", tmp_record.m_Date));
			tmp_viewholder.m_TextView_Type.setText(tmp_record.m_Type);
			tmp_viewholder.m_TextView_SubType.setText(tmp_record.m_SubType);
			tmp_viewholder.m_TextView_Fee.setText( String.format("%.2f", tmp_record.m_Fee));
			tmp_viewholder.m_Button_Remarks.setRemarks(tmp_record.m_Remarks);

			// 设置显示方式(与前一个不同时，设置为可见)
			if ( 0 == position ) {
				tmp_viewholder.m_TextView_Date.setVisibility(View.VISIBLE);
				tmp_viewholder.m_TextView_Type.setVisibility(View.VISIBLE);
			} else {
				CostRecord pre_record = m_CostRecordList.get(position-1);
				if ( tmp_record.m_Date == pre_record.m_Date ) {
					tmp_viewholder.m_TextView_Date.setVisibility(View.INVISIBLE);

					String type1 = tmp_record.m_Type.trim();
					String type2 = pre_record.m_Type.trim();
					if ( true == type1.equals(type2) ) {
						tmp_viewholder.m_TextView_Type.setVisibility(View.INVISIBLE);
					} else {
						tmp_viewholder.m_TextView_Type.setVisibility(View.VISIBLE);
					}
				} else {
					tmp_viewholder.m_TextView_Date.setVisibility(View.VISIBLE);
					tmp_viewholder.m_TextView_Type.setVisibility(View.VISIBLE);
				}
			}

			// 有备注的显示备注图标
			if ( 0 == tmp_record.m_Remarks.length() ) {
				tmp_viewholder.m_Button_Remarks.setVisibility(View.INVISIBLE);
			}
			else {
				tmp_viewholder.m_Button_Remarks.setVisibility(View.VISIBLE);
				tmp_viewholder.m_Button_Remarks.setFocusable(false);
			}
		}

		return convertView;
	}

	public void setSelectedPosition(int pos) {
		m_SelectedPos = pos;
	}

	public int getSelectedPosition() {
		return  m_SelectedPos;
	}

	public CostRecord getSelectedItem() {
		if ( 0 > m_SelectedPos ) return null;
		else return m_CostRecordList.get(m_SelectedPos);
	}
}
