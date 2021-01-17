package com.ding.java.remembercost.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ding.java.remembercost.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class Adapter_TypeList extends BaseAdapter implements View.OnClickListener {

	public static int SELECT_MODE_SINGLE = 0;
	public static int SELECT_MODE_MULTI = 1;
	private LayoutInflater m_Inflater = null;
	private ArrayList<String> m_TypeList = new ArrayList<String>();
	private ArrayList<View> m_SelectedView = new ArrayList<View>();
	private ArrayList<String> m_SelectedType = new ArrayList<String>();
	private int m_SelectMode = SELECT_MODE_SINGLE;
	
	public Adapter_TypeList(Context context) {
		// TODO Auto-generated constructor stub
		m_Inflater = LayoutInflater.from(context);

		m_TypeList.add("交通");
		m_TypeList.add("衣服");
		m_TypeList.add("网购");
		m_TypeList.add("吃喝");
		m_TypeList.add("话费");
		m_TypeList.add("饭菜");
		m_TypeList.add("水果果");
		m_TypeList.add("生病病");
		m_TypeList.add("杂乱");
		m_TypeList.add("随礼");
		m_TypeList.add("水电煤");
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return (m_TypeList.size()+1) / 3;
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
		TextView m_TextView_Type1;
		TextView m_TextView_Type2;
		TextView m_TextView_Type3;
		//ImageView icon;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if ( null == convertView ) {
			try {
				convertView = m_Inflater.inflate(R.layout.typecell, parent, false);
			} catch (InflateException e) {
				// TODO: handle exception
				Log.i("ding_log",e.getCause()+"||"+e.getMessage());
				return null;
			}
			ViewHolder view_holder = new ViewHolder();
			view_holder.m_TextView_Type1 = (TextView) convertView.findViewById(R.id.TypeCell_Type1);
			view_holder.m_TextView_Type2 = (TextView) convertView.findViewById(R.id.TypeCell_Type2);
			view_holder.m_TextView_Type3 = (TextView) convertView.findViewById(R.id.TypeCell_Type3);
			view_holder.m_TextView_Type1.setOnClickListener(this);
			view_holder.m_TextView_Type2.setOnClickListener(this);
			view_holder.m_TextView_Type3.setOnClickListener(this);

			// 设置显示的文本
			int type_pos = position*3;
			if ( type_pos < m_TypeList.size() ) {
				view_holder.m_TextView_Type1.setText(m_TypeList.get(type_pos));
				view_holder.m_TextView_Type1.setBackgroundColor(Color.WHITE);
			} else {
				view_holder.m_TextView_Type1.setVisibility(View.INVISIBLE);
			}
			type_pos++;
			if ( type_pos < m_TypeList.size() ) {
				view_holder.m_TextView_Type2.setText(m_TypeList.get(type_pos));
				view_holder.m_TextView_Type2.setBackgroundColor(Color.WHITE);
			} else {
				view_holder.m_TextView_Type2.setVisibility(View.INVISIBLE);
			}
			type_pos++;
			if ( type_pos < m_TypeList.size() ) {
				view_holder.m_TextView_Type3.setText(m_TypeList.get(type_pos));
				view_holder.m_TextView_Type3.setBackgroundColor(Color.WHITE);
			} else {
				view_holder.m_TextView_Type3.setVisibility(View.INVISIBLE);
			}

			convertView.setTag(view_holder);
		}
		
		return convertView;
	}

	public void onClick( View v ) {
		switch (v.getId()) {
			case R.id.TypeCell_Type1 :
			case R.id.TypeCell_Type2 :
			case R.id.TypeCell_Type3 :
				addSelectedView(v);
				break;
			default:
				Log.i("ding_log",this.toString()+" other view clicked! " + v.toString() );
				break;
		}
	}

	// 查找View是否在已选择的View中
	private boolean isSelectedView( View v ) {
		for ( int i = 0; i < m_SelectedView.size(); i++ ) {
			if ( v == m_SelectedView.get(i) ) {
				return true;
			}
		}
		return false;
	}

	// 将指定视图添加到已选择的视图中
	private void addSelectedView( View v ) {
		if ( true == isSelectedView(v) ) {
			removeSelectedView(v);
		} else {
			if ( SELECT_MODE_SINGLE == m_SelectMode ) reset();
			v.setBackgroundColor(Color.GRAY);
			m_SelectedView.add(v);
		}
	}

	// 从已选择的View中去除指定View
	private void removeSelectedView( View v ) {
		for ( int i = 0; i < m_SelectedView.size(); i++ ) {
			if ( v == m_SelectedView.get(i) ) {
				m_SelectedView.remove(i);
				v.setBackgroundColor(Color.WHITE);
				return;
			}
		}
	}

	// 重置已选择的所有View
	public void reset() {
		for ( int i = 0; i < m_SelectedView.size(); i++ ) {
			m_SelectedView.get(i).setBackgroundColor(Color.WHITE);
			m_SelectedView.remove(i);
		}
	}

	// set select mode SELECT_MODE_SINGLE SELECT_MODE_MULTI
	public void setSelectMode( int mode ) {
		if ( SELECT_MODE_SINGLE != mode &&
				SELECT_MODE_MULTI != mode ) return;
		m_SelectMode = mode;
	}

	public int getSelectMode() {
		return m_SelectMode;
	}

	public ArrayList<View> getSelectedView() {
		return m_SelectedView;
	}

	public ArrayList<String> getSelectedType() {
		m_SelectedType.clear();
		for ( int i = 0; i < m_SelectedView.size(); i++ ) {
			TextView v = (TextView)m_SelectedView.get(i);
			String str = v.getText().toString();
			m_SelectedType.add(str);
		}
		return m_SelectedType;
	}
}
