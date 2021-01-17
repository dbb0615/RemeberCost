package com.ding.java.remembercost.PopupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ding.java.remembercost.R;

/**
 * Created by Administrator on 2016/5/5.
 */
public class PopupWin_CostRemarks extends PopupWindow {

    private Context m_Context = null;
    private View m_ParentView = null;

    public PopupWin_CostRemarks(Context parent, View view) {
        m_Context = parent;
        m_ParentView = view;
    }

    public boolean showText(String remarks) {

        if ( null == m_Context || null == m_ParentView) {
            return  false;
        }

        // 取得屏幕像素
        WindowManager wm = (WindowManager) m_Context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        // 创建弹出窗口视图
        View pop_view = LayoutInflater.from(m_Context).inflate(
                R.layout.popupwin_remarks, null, false );
        setContentView(pop_view);

        setFocusable(true);
        setOutsideTouchable(true);
        // popupwindow设置背景后才可以点击外部消失
        setBackgroundDrawable(new BitmapDrawable());

        // 设置弹出窗口文本
        TextView tmp_textview = (TextView) pop_view.findViewById(R.id.PopupWin_Remarks_Text);
        // 使文本可滚动
        tmp_textview.setMovementMethod(ScrollingMovementMethod.getInstance());
        tmp_textview.setText(remarks);

        // 宽度设置为屏幕的2/3
        setWidth(metrics.widthPixels*2/3);
        // 高度设置为文本的高度
        setHeight(metrics.heightPixels/5);

        setAnimationStyle(R.style.scale_anim_style);

        // 设置好参数之后再show
//        int parent_pos[] = {0,0};
//        m_ParentView.getLocationOnScreen(parent_pos);
        showAtLocation(m_ParentView,
                Gravity.NO_GRAVITY,
                metrics.widthPixels/6,
                metrics.heightPixels/2 );

        return true;
    }
}
