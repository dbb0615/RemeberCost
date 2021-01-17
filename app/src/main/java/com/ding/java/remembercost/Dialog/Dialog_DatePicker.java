package com.ding.java.remembercost.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.ding.java.remembercost.Activity.Activity_CostRecordList;
import com.ding.java.remembercost.R;

import java.util.ArrayList;

/**
 * Created by xiaobei on 2017/1/11.
 */

public class Dialog_DatePicker extends Dialog implements View.OnClickListener {

    private Context m_Context = null;
    private Button m_Button_OK = null;
    private Button m_Button_Cancel = null;
    private DatePicker m_DatePicker_begin = null;
    private DatePicker m_DatePicker_end = null;

    public Dialog_DatePicker(Context context) {
        super(context);
        m_Context = context;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_datepicker);

        m_Button_OK = (Button)findViewById(R.id.dialog_datepicker_ok);
        m_Button_OK.setOnClickListener(this);
        m_Button_Cancel = (Button)findViewById(R.id.dialog_datepicker_cancel);
        m_Button_Cancel.setOnClickListener(this);
        m_DatePicker_begin = (DatePicker)findViewById(R.id.dialog_datepicker_begin);
        m_DatePicker_end = (DatePicker)findViewById(R.id.dialog_datepicker_end);

        // 重新设置日期选择器的大小
        resizeDatePicker(m_DatePicker_begin,(float)(0.6));
        resizeDatePicker(m_DatePicker_end,(float)(0.6));

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_datepicker_ok :
                Activity_CostRecordList activity_costrecord =
                        m_Context instanceof Activity_CostRecordList ? ((Activity_CostRecordList) m_Context) : null;
                int begin_date = m_DatePicker_begin.getYear() * 10000 +
                        (m_DatePicker_begin.getMonth()+1) * 100 +
                        m_DatePicker_begin.getDayOfMonth();
                int end_date = m_DatePicker_end.getYear() * 10000 +
                        (m_DatePicker_end.getMonth()+1)  * 100 +
                        m_DatePicker_end.getDayOfMonth();
                activity_costrecord.setDate(begin_date,end_date);
                dismiss();
                break;
            case R.id.dialog_datepicker_cancel :
                dismiss();
                break;
            default:
                Log.i("ding_log",this.toString() + " unknown view clicked!" + v.toString());
                break;
        }
    }

    // 重置datepicker的大小
    // size为占屏幕宽度比例
    private void resizeDatePicker( DatePicker pk, float size) {
        ArrayList<View> array_list = findNumberPickers(pk);

        // 设置LayoutParams
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,0,0,0);
        for ( int i = 0; i < array_list.size(); i++ ) {
            View v = array_list.get(i);
            NumberPicker np = v instanceof NumberPicker ? ((NumberPicker) v) : null;
            if ( null == np ) continue;
            np.setLayoutParams(lp);
            np.setScaleX(size);
        }
    }
    // 查找视图下所有的子视图
    private ArrayList<View> findNumberPickers(ViewGroup vp ) {
        if ( null == vp ) return null;
        NumberPicker np = vp instanceof NumberPicker ? ((NumberPicker) vp) : null;

        ArrayList<View> v_list = new ArrayList<View>();
        if ( null == np ) {
            int child_num = vp.getChildCount();
            for ( int i = 0; i < child_num; i++ ) {
                View v = vp.getChildAt(i);
                ViewGroup tmp_vp = v instanceof ViewGroup ? ((ViewGroup) v) : null;
                if ( null != tmp_vp ) {
                    v_list.addAll(findNumberPickers(tmp_vp));
                }
            }
        } else {
            v_list.add(vp);
        }
        return v_list;
    }
}
