package com.ding.java.remembercost.PopupWindow;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import com.ding.java.remembercost.Activity.Activity_CostRecordList;
import com.ding.java.remembercost.R;

/**
 * Created by Administrator on 2016/5/5.
 */
public class PopupWin_Add extends PopupWindow implements View.OnClickListener {

    private Activity_CostRecordList m_ParentActivity = null;
    private View m_ParentView = null;
    private Button m_Button_EditSettings = null;
    private Drawable m_Drawable_left = null;
    private Drawable m_Drawable_right = null;
    private Button m_Button_Add = null;
    private Button m_Button_Edit = null;
    private Button m_Button_Delete = null;
    private Button m_Button_Send = null;
    private boolean m_IsExtend = false;
    private Drawable m_Drawable_Add = null;
    private Drawable m_Drawable_Delete = null;
    private Drawable m_Drawable_Edit = null;
    private Drawable m_Drawable_Send = null;

    public PopupWin_Add(Context parent, View view) {
        m_ParentActivity = (Activity_CostRecordList)parent;
        m_ParentView = view;

        //  取得drawable
        m_Drawable_left = m_ParentActivity.getResources().getDrawable(R.drawable.arrow_left);
        m_Drawable_right = m_ParentActivity.getResources().getDrawable(R.drawable.arrow_right);
        m_Drawable_Add = m_ParentActivity.getResources().getDrawable(R.drawable.add);
        m_Drawable_Delete = m_ParentActivity.getResources().getDrawable(R.drawable.delete);
        m_Drawable_Edit = m_ParentActivity.getResources().getDrawable(R.drawable.edit);
        m_Drawable_Send = m_ParentActivity.getResources().getDrawable(R.drawable.send0);
    }

    public boolean show() {
        if ( null == m_ParentActivity || null == m_ParentView) {
            String log_str = String.format( "%s parent activity or view is null![%0x|%0x]",
                    getClass().toString() + m_ParentActivity, m_ParentView );
            Log.e("ding_log", log_str);
            return  false;
        }

        // 创建弹出窗口视图
        View pop_view = LayoutInflater.from(m_ParentActivity).inflate(
                R.layout.popupwin_editsettings, null, false );
        pop_view.setVisibility(View.VISIBLE);
        m_Button_EditSettings = (Button)pop_view.findViewById(R.id.PopupWin_Button_EditSettings);
        m_Button_Add = (Button)pop_view.findViewById(R.id.PopupWin_Button_Add);
        m_Button_Delete = (Button)pop_view.findViewById(R.id.PopupWin_Button_Delete);
        m_Button_Edit = (Button)pop_view.findViewById(R.id.PopupWin_Button_Edit);
        m_Button_Send = (Button)pop_view.findViewById(R.id.PopupWin_Button_Send);

        m_Button_EditSettings.setOnClickListener(this);
        m_Button_Add.setOnClickListener(this);
        m_Button_Delete.setOnClickListener(this);
        m_Button_Edit.setOnClickListener(this);
        m_Button_Send.setOnClickListener(this);

        // 设置宽度
        setWidth(m_Drawable_left.getIntrinsicWidth() * 4);
        // 设置高度
        setHeight(m_Drawable_left.getIntrinsicHeight());

        setContentView(pop_view);
        setFocusable(false);
        setOutsideTouchable(true);

        int[] parent_view_loc = new int[2];
        m_ParentView.getLocationOnScreen(parent_view_loc);

        // 显示在父窗口上方
        showAtLocation(m_ParentView,Gravity.NO_GRAVITY,
                parent_view_loc[0] + 10,
                parent_view_loc[1] - 20 - m_Drawable_left.getIntrinsicHeight() );

        return true;
    }

    public void onClick(View v) {
        switch ( v.getId() ) {
            case R.id.PopupWin_Button_EditSettings :
                m_IsExtend = !m_IsExtend;

                int show_flag = View.VISIBLE;
                Drawable show_drawable = m_Drawable_left;
                if ( !m_IsExtend ) {
                    show_flag = View.INVISIBLE;
                    show_drawable = m_Drawable_right;
                }
                m_Button_Add.setVisibility(show_flag);
                m_Button_Delete.setVisibility(show_flag);
                m_Button_Edit.setVisibility(show_flag);
                m_Button_Send.setVisibility(show_flag);
                m_Button_EditSettings.setBackground(show_drawable);

                break;
            case R.id.PopupWin_Button_Add :
                m_ParentActivity.addRecord();
                break;
            case R.id.PopupWin_Button_Delete :
                m_ParentActivity.deleteRecord();
                break;
            case R.id.PopupWin_Button_Edit :
                m_ParentActivity.editRecord();
                break;
            case R.id.PopupWin_Button_Send :
                m_ParentActivity.sendRecord();
                break;
        }
    }
}
