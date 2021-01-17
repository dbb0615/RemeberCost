package com.ding.java.remembercost.Other;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ding.java.remembercost.PopupWindow.PopupWin_CostRemarks;

/**
 * Created by Administrator on 2016/5/17.
 */
public class RemarksButton extends Button {

    private Context m_Context = null;
    private String m_Remarks = null;

    public RemarksButton(Context context) {
        this(context, null);
        m_Context = context;
    }

    public RemarksButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyle);
        m_Context = context;
    }

    public RemarksButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        m_Context = context;
    }

    public void setRemarks(String remarks) {
        m_Remarks = remarks;

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(m_Context,m_Remarks,Toast.LENGTH_LONG).show();
                Log.i("ding_log",m_Remarks);
                PopupWin_CostRemarks tmp_popwin = new PopupWin_CostRemarks(m_Context,v);
                tmp_popwin.showText(m_Remarks);
            }
        });
    }

    public String getRemarks() {
        return  m_Remarks;
    }
}
