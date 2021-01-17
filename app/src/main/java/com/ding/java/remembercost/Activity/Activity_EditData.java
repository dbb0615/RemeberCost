package com.ding.java.remembercost.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ding.java.remembercost.data.My_SqlLite;
import com.ding.java.remembercost.R;

public class Activity_EditData extends BaseActivity_EdtiCostRecord {

    private Button m_ButtonSave = null;
    private Button m_ButtonCancel = null;
    My_SqlLite m_Sqllite = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_editrecord);
        super.onCreate(savedInstanceState);

        m_ButtonSave = (Button)findViewById(R.id.button_save);
        m_ButtonCancel = (Button)findViewById(R.id.button_cancel);
        m_ButtonSave.setOnClickListener(this);
        m_ButtonCancel.setOnClickListener(this);

        m_Sqllite = new My_SqlLite();
    }


    @Override
    public void onClick( View v ) {

        switch ( v.getId() ) {
            case R.id.button_save :
                // 添加记录
                if ( 0 == getSequence() ) {
                    m_Sqllite.addCostRecord(getDate(),
                            getType(),
                            getSubType(),
                            getFee(),
                            getRemarks());
                    Log.i("ding_log", "add db data over!");
                } else { // 编辑记录,保存后自动返回
                    m_Sqllite.updateCostRecord( getSequence(),
                            getDate(),
                            getType(),
                            getSubType(),
                            getFee(),
                            getRemarks());

                    Log.i("ding_log", "update db data over!");
                    setResult( RESULT_OK );
                    finish();
                }
                break;
            case R.id.button_cancel:
                Log.i("ding_log", "cancel edit!");
                setResult( RESULT_CANCELED );
                finish();
            default:
                Log.i( "ding_log", "super view onClick run! " + v.toString() );
                super.onClick(v);
                break;
        }
    }

    // 清除已经选择的内容
    public void onDataChanged( String data_type, String data ) {
        if ( 0 == getSequence() ) {
            super.onDataChanged(data_type,data);
        } else {
            /* 编辑的时候不需要清除已有内容，由用户主动删除 */
            if ( true == data_type.equals("date") ) {
                setDate(data);
            } else if ( true == data_type.equals("type") ) {
                setType(data);
            }
        }
    }
}
