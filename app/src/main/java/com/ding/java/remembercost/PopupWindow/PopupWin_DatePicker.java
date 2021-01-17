package com.ding.java.remembercost.PopupWindow;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupWindow;

import com.ding.java.remembercost.Activity.Activity_Main;
import com.ding.java.remembercost.R;

/**
 * Created by Administrator on 2016/5/5.
 */
public class PopupWin_DatePicker extends PopupWindow {

    private Context m_Parent = null;
    private View m_TopView = null;
    private Button m_Ok = null;
    private Button m_Cancel = null;
    private DatePicker m_DatePicker = null;

    public PopupWin_DatePicker(Context parent,View top_view) {
        m_Parent = parent;
        m_TopView = top_view;
    }

    public boolean InitStatus() {

        if ( null == m_Parent ) {
            return  false;
        }

        View pop_view = LayoutInflater.from(m_Parent).inflate(
                R.layout.activity_mydatepick, null, false );
        m_Ok = (Button) pop_view.findViewById(R.id.my_datepicker_ok);
        m_Cancel = (Button) pop_view.findViewById(R.id.my_datepicker_cancel);
        m_DatePicker = (DatePicker)  pop_view.findViewById(R.id.my_datepicker_picker);
        CreateButtonListener();
        setContentView(pop_view);

        setFocusable(true);
        setOutsideTouchable(true);
        // popupwindow设置背景后才可以点击外部消失
        setBackgroundDrawable(new BitmapDrawable());

        // 获取顶端窗口位置
        Rect top_rect = new Rect();
        m_TopView.getGlobalVisibleRect(top_rect);
        // 取得屏幕像素
        WindowManager wm = (WindowManager) m_Parent.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        setHeight(metrics.heightPixels-top_rect.bottom);
        setWidth(metrics.widthPixels);

        setAnimationStyle(R.style.popdown_anim_style);

        // 设置好参数之后再show
        showAtLocation(getContentView(),
                Gravity.CENTER_HORIZONTAL|Gravity.TOP, top_rect.left, top_rect.bottom );
        return true;
    }

    private void CreateButtonListener() {

        m_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selected_date_str = String.format( "%04d%02d%02d",
                        m_DatePicker.getYear(),
                        (m_DatePicker.getMonth()+1),
                        m_DatePicker.getDayOfMonth() );
                ((Activity_Main) m_Parent).onDataChanged("date",selected_date_str);
                dismiss();
            }
        });

        m_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity_Main) m_Parent).onDataChanged("date","");
                dismiss();
            }
        });
    }

}
