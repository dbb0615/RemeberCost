package com.ding.java.remembercost.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ding.java.remembercost.R;

/**
 * Created by Administrator on 2016/6/22.
 */
public class Fragment_WeatherDayDetail extends Fragment {
    private View m_FragmentView = null;
    private TextView m_DetailView = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_FragmentView = inflater.inflate(R.layout.fragment_weatherdaydetail,container);
        m_DetailView = (TextView) m_FragmentView.findViewById(R.id.fragment_weatherdaydetail_text);
        m_DetailView.setMovementMethod(ScrollingMovementMethod.getInstance());
        return m_FragmentView;
    }

    public void setDetailText(String str) {
        m_DetailView.setText(str);
    }

    public void addDetailText(String str) {
        m_DetailView.setText(m_DetailView.getText()+"\n"+str);
    }

}
