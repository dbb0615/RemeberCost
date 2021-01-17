package com.ding.java.remembercost.Activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Window;

import com.ding.java.remembercost.Fragment.Fragment_WeatherDate;
import com.ding.java.remembercost.Fragment.Fragment_WeatherDayDetail;
import com.ding.java.remembercost.R;

public class Activity_Weather extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather);

    }

    public Fragment_WeatherDayDetail getDayDetailFragment() {
        // 取得fragment
        FragmentManager frag_manager = getFragmentManager();
        return (Fragment_WeatherDayDetail)frag_manager.findFragmentById(R.id.fragment_weatherdaydetail);
    }

    public Fragment_WeatherDate getDateFragment() {
        // 取得fragment
        FragmentManager frag_manager = getFragmentManager();
        return (Fragment_WeatherDate)frag_manager.findFragmentById(R.id.fragment_weatherdate);
    }

}
