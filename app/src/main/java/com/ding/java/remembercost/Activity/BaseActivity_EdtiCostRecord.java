package com.ding.java.remembercost.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ding.java.remembercost.Dialog.Dialog_TypeList;
import com.ding.java.remembercost.R;

/**
 * Created by Administrator on 2016/11/11.
 * 编辑消费记录的ACTIVITY基类
 * 包含 选择日期、记录类型、子记录类型、费用、备注的特殊动作
 * 其他操作按钮需要自行实现
 * 如果重载onActivityResult和onClick执行该类的函数
 * onCreate函数需要先设置layout后才可以调用
 * 子类中的layout必须包含这个布局edit_data.xml
 */
public class BaseActivity_EdtiCostRecord extends BaseActivity{

    private Button m_Button_Date = null;
    private Button m_Button_Type = null;
    private EditText m_EditText_SubType = null;
    private EditText m_EditText_Fee = null;
    private EditText m_EditText_Remarks = null;
    private long m_Sequence = 0;
    private int m_Year = 0;
    private int m_Month = 0;
    private int m_Day = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_Button_Date = (Button)findViewById(R.id.Button_cost_date);
        m_Button_Date.setOnClickListener(this);
        m_Button_Type = (Button)findViewById(R.id.Button_cost_type);
        m_Button_Type.setOnClickListener(this);
        m_EditText_SubType = (EditText) findViewById(R.id.edittext_cost_subtype);
        m_EditText_Fee = (EditText) findViewById(R.id.edittext_cost_fee);
        m_EditText_Remarks = (EditText) findViewById(R.id.edittext_cost_remarks);

        // 设置初始数据
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if ( null != bundle ) {
            m_Sequence = bundle.getLong(getResources().getString(R.string.cost_sequence));// 序号
            int tmp_ymd = bundle.getInt(getResources().getString(R.string.cost_date));// 日期(格式必须为yyyymmdd)
            // 解析日期
            m_Year = tmp_ymd / 10000;
            m_Month = tmp_ymd / 100 % 100;
            m_Day = tmp_ymd % 100;
            String tmp_str = getDateStr();
            m_Button_Date.setText( tmp_str );
            tmp_str = bundle.getString(getResources().getString(R.string.cost_type));// 类型
            m_Button_Type.setText( tmp_str );
            tmp_str = bundle.getString(getResources().getString(R.string.cost_subtype));// 子类型
            m_EditText_SubType.setText( tmp_str );
            float tmp_fee  = bundle.getFloat(getResources().getString(R.string.cost_fee));// 费用
            m_EditText_Fee.setText( Float.toString(tmp_fee) );
            tmp_str = bundle.getString(getResources().getString(R.string.cost_remarks));// 备注
            m_EditText_Remarks.setText( tmp_str );
        }

        // 设置子类型的监听事件
        m_EditText_SubType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onDataChanged("subtype",s.toString());
            }
        });
    }

    @Override
    public void onClick( View v ) {
        switch ( v.getId() ) {
            case R.id.Button_cost_date :
                Log.i("ding_log", "m_Button_Date onTouch");
                Intent tmp_intent = new Intent();
                Bundle tmp_bundle = new Bundle();
                tmp_bundle.putInt("year",m_Year);
                tmp_bundle.putInt("month",m_Month);
                tmp_bundle.putInt("day",m_Day);
                tmp_intent.putExtras(tmp_bundle);
                tmp_intent.setClass( this, Activity_DatePicker.class );
                startActivityForResult(tmp_intent, R.integer.requestcode_datepicker);
                break;
            case R.id.Button_cost_type :
                Log.i("ding_log", "m_Button_Type onTouch");
                Dialog_TypeList tmp_dlg = new Dialog_TypeList(this);
                tmp_dlg.show();
                break;
            default:
                Log.i( "ding_log", "unknown view is clicked! " + v.toString() );
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("ding_log", this.getLocalClassName() + "onActivityResult " + requestCode + "|" + resultCode);
        if ( requestCode == R.integer.requestcode_datepicker  ) {
            switch (resultCode) {
                case RESULT_OK:
                    Bundle b=data.getExtras();
                    m_Year = b.getInt("year");
                    m_Month = b.getInt("month");
                    m_Day = b.getInt("day");
                    onDataChanged("date",getDateStr());
                    break;
                default:
                    m_Year = 0;
                    m_Month = 0;
                    m_Day = 0;
                    onDataChanged("date","");
                    break;
            }
        }
        else {
            Log.e( "ding_log", "unknown request code! " + requestCode );
        }
    }

    // 清除已经选择的内容
    public void onDataChanged( String data_type, String data ) {
        if ( true == data_type.equals("date") ) {
            if ( false == m_Button_Date.getText().toString().equals(data) ) {
                m_Button_Date.setText(data);
                m_Button_Type.setText("");
                m_EditText_SubType.setText("");
                m_EditText_Fee.setText("");
                m_EditText_Remarks.setText("");
            }
        } else if ( true == data_type.equals("type") ) {
            if ( false == m_Button_Type.getText().toString().equals(data) ) {
                m_Button_Type.setText(data);
                m_EditText_SubType.setText("");
                m_EditText_Fee.setText("");
                m_EditText_Remarks.setText("");
            }
        } else if ( true == data_type.equals("subtype") ) {
            // 子类型全清空时，才清除费用和备注
            if ( 0 == data.length() ) {
                // 防止和EditText的监听事件形成死循环，不再设置
                //m_EditText_SubType.setText("");
                m_EditText_Fee.setText("");
                m_EditText_Remarks.setText("");
            }
        } else if ( true == data_type.equals("fee") ) {
            if ( false == m_EditText_Fee.getText().toString().equals(data) ) {
                m_EditText_Fee.setText(data);
                //m_EditText_Remarks.setText("");
            }
        }
    }

    public long getSequence(){ return  m_Sequence; }
    public void setDate( String date_str ){ m_Button_Date.setText(date_str); }
    public int getDate(){ return  m_Year*10000+m_Month*100+m_Day; }
    public String getDateStr(){
        if ( 0 == m_Year || 0 == m_Month || 0 == m_Day ) return new String("");
        else return String.format( "%04d-%02d-%02d", m_Year, m_Month, m_Day );
    }
    public int getYear(){ return  m_Year; }
    public int getMonth(){ return  m_Month; }
    public int getDay(){ return  m_Day; }
    public void setType( String type ){ m_Button_Type.setText(type); }
    public String getType(){ return  m_Button_Type.getText().toString(); }
    public void setSubType( String sub_type ){ m_EditText_SubType.setText(sub_type); }
    public String getSubType(){ return  m_EditText_SubType.getText().toString(); }
    public float getFee(){
        float ret_f = 0;
        try {
            ret_f = Float.parseFloat(m_EditText_Fee.getText().toString());
        } catch ( NumberFormatException e ) {
            Log.e( "ding_log", "fee convert to float error! " + e.getMessage() );
            return R.integer.invalid_fee;
        }
        return  ret_f;
    }
    public void setFee( String fee ){ m_EditText_Fee.setText(fee); }
    public String getFeeStr(){ return m_EditText_Fee.getText().toString(); }
    public void setRemarks( String remarks ){ m_EditText_Remarks.setText(remarks); }
    public String getRemarks(){ return  m_EditText_Remarks.getText().toString(); }
}
