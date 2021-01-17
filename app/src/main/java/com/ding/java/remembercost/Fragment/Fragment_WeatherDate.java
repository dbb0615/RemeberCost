package com.ding.java.remembercost.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ding.java.remembercost.Activity.Activity_Weather;
import com.ding.java.remembercost.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/22.
 */
public class Fragment_WeatherDate extends Fragment implements View.OnClickListener {

    private View m_SelfView = null;
    private Handler m_Handler = null;
    private Button m_OkButton = null;
    private EditText m_CityEditText = null;
    private ArrayList<WeatherCityInfo> m_CityList = null;
    private WeatherCurrent m_CurrentWeather = new WeatherCurrent();

    // 获取天百度天气匹配城市列表
    private final Runnable m_Runable_GetCity =  new Runnable() {
        @Override
        public void run() {
            HttpURLConnection url_conn = null;
            InputStream in_stream = null;
            sendMessage(R.string.Thread_MsgForShowTextOnView, "\ninput city name:"+m_CityEditText.getText().toString());
            try {
                URL url = null;
                if ( null == (url = getWeatherURL("city",m_CityEditText.getText().toString())) ) {
                    return;
                }
                url_conn = (HttpURLConnection)url.openConnection();
                // 设置连接属性
                if ( 0 != setURLConnection(url_conn) ) {
                    return;
                }
                // 在调用getInputStream时会隐式调用connect
                url_conn.connect();
                in_stream = url_conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in_stream, "UTF-8"));
                String strRead = new String();
                String tmp_str = null;
                while ((tmp_str = reader.readLine()) != null) {
                    strRead = strRead + tmp_str;
                }
                JSONObject json_read = new JSONObject(strRead);
                showWeatherInfo("city", json_read);

                in_stream.close();
            }
            catch (MalformedURLException e) {
                Log.e("ding_log",e.getStackTrace().toString()+"|"+e.getMessage());
            } catch (JSONException e) {
                Log.e("ding_log",e.getStackTrace().toString()+"|"+e.getMessage());
            } catch (IOException e) {
                Log.e("ding_log",e.getStackTrace().toString()+"|"+e.getMessage());
            }

            if ( null != url_conn ) url_conn.disconnect();

            sendMessage(R.string.Thread_MsgForUpCityListOver,null);
        }
    };

    // 获取天百度天气匹配城市列表
    private final Runnable m_Runable_GetCurrentWeather =  new Runnable() {
        @Override
        public void run() {
            HttpURLConnection url_conn = null;
            InputStream in_stream = null;
            String cityname_en = getCityNameEn();
            if ( null == cityname_en ) {
                sendMessage(R.string.Thread_MsgForToast,"没有找到对应的城市！");
                return;
            }
            sendMessage(R.string.Thread_MsgForShowTextOnView,"\ninput city name en:"+cityname_en);
            try {
                URL url = null;
                if ( null == (url = getWeatherURL("cur_weather",cityname_en)) ) {
                    return;
                }
                url_conn = (HttpURLConnection)url.openConnection();
                // 设置连接属性
                if ( 0 != setURLConnection(url_conn) ) {
                    return;
                }
                // 在调用getInputStream时会隐式调用connect
                url_conn.connect();
                in_stream = url_conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in_stream, "UTF-8"));
                String strRead = new String();
                String tmp_str = null;
                while ((tmp_str = reader.readLine()) != null) {
                    strRead = strRead + tmp_str;
                }
                JSONObject json_read = new JSONObject(strRead);
                showWeatherInfo("cur_weather", json_read);

                in_stream.close();
            }
            catch (MalformedURLException e) {
                Log.e("ding_log",e.getStackTrace().toString()+"|"+e.getMessage());
            } catch (JSONException e) {
                Log.e("ding_log",e.getStackTrace().toString()+"|"+e.getMessage());
            } catch (IOException e) {
                Log.e("ding_log",e.getStackTrace().toString()+"|"+e.getMessage());
            }

            if ( null != url_conn ) url_conn.disconnect();
        }
    };

    public int setURLConnection(HttpURLConnection url_conn) {
        if ( null == url_conn ) {
            Log.e("ding_log","setURLConnection input is null!");
            return -2;
        }
        try {
            // 设置输入输出流
            url_conn.setDoInput(true);
            url_conn.setDoOutput(true);
            // 设置请求方式为post
            //url_conn.setRequestMethod("POST");
            url_conn.setRequestMethod("GET");
            // Post请求不能使用缓存
            url_conn.setUseCaches(false);
            url_conn.setInstanceFollowRedirects(true);
            // 设置超时时间
            url_conn.setConnectTimeout(5000);
            url_conn.setReadTimeout(5000);
            // 设置content-type
            url_conn.setRequestProperty("apikey","3c724dfc58f927549c26d9abb49fd3a1");
        } catch (ProtocolException e) {
            Log.e("ding_log","setRequestMethod GET error!:"+e.getMessage());
            return -1;
        }
        return 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_SelfView = inflater.inflate(R.layout.fragment_weatherdate,container);
        m_OkButton = (Button) m_SelfView.findViewById(R.id.fragment_weatherdate_button);
        m_CityEditText = (EditText) m_SelfView.findViewById(R.id.fragment_weatherdate_edittext);
        m_CityList = new ArrayList<WeatherCityInfo>();

        m_OkButton.setOnClickListener(this);

        createHandler();

        return m_SelfView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_weatherdate_button:
                Log.d("ding_log","fragment_weatherdate_button clicked!");
                getDayDetailFragment().setDetailText("###########################");
                if ( null != m_CityEditText && 0 != m_CityEditText.getText().toString().length() ) {
                    // 自动隐藏输入法

                    new Thread(m_Runable_GetCity).start();
                }
                break;
            default:
                Log.i("ding_log","unknown button clicked!|");
                break;
        }
    }

    // 捕获线程消息
    void createHandler() {
        m_Handler = new Handler(){
            public void handleMessage(Message msg) {
                switch (msg.arg1) {
                    case R.string.Thread_MsgForUpCityList:
                        for ( int i = 0; i < m_CityList.size(); i++ ) {
                            getDayDetailFragment().addDetailText(
                                    m_CityList.get(i).province_cn+"\t"+
                                            m_CityList.get(i).name_cn+"\t"+
                                            m_CityList.get(i).name_en+"\t"+
                                            m_CityList.get(i).district_cn+"\t"+
                                            m_CityList.get(i).area_id
                            );
                        }
                        break;
                    case R.string.Thread_MsgForToast:
                        Toast.makeText(getWeatherActivity(),msg.getData().getString("msg"),Toast.LENGTH_LONG).show();
                        break;
                    case R.string.Thread_MsgForUpCityListOver:
                        new Thread(m_Runable_GetCurrentWeather).start();
                        break;
                    case R.string.Thread_MsgForUpCurrentWeather:
                        getDayDetailFragment().addDetailText(
                                "城市:"+m_CurrentWeather.city+"\n"+
                                        "城市拼音:"+m_CurrentWeather.pinyin+"\n"+
                                        "城市编码:"+m_CurrentWeather.citycode+"\n"+
                                        "日期"+m_CurrentWeather.date+"\n"+
                                        "发布时间:"+m_CurrentWeather.time+"\n"+
                                        "邮编:"+m_CurrentWeather.postCode+"\n"+
                                        "经度:"+m_CurrentWeather.longitude+"\n"+
                                        "维度:"+m_CurrentWeather.latitude+"\n"+
                                        "海拔:"+m_CurrentWeather.altitude+"\n"+
                                        "天气情况:"+m_CurrentWeather.weather+"\n"+
                                        "气温:"+m_CurrentWeather.temp+"\n"+
                                        "最低气温:"+m_CurrentWeather.l_tmp+"\n"+
                                        "最高气温:"+m_CurrentWeather.h_tmp+"\n"+
                                        "风向:"+m_CurrentWeather.WD+"\n"+
                                        "风力:"+m_CurrentWeather.WS+"\n"+
                                        "日出时间:"+m_CurrentWeather.sunrise+"\n"+
                                        "日落时间:"+m_CurrentWeather.sunset
                        );
                        break;
                    case R.string.Thread_MsgForShowTextOnView:
                        getDayDetailFragment().addDetailText(msg.getData().getString("msg")+"\n");
                        break;
                    default:
                        break;
                }
            }
        };
    }

    // 从城市列表中找到匹配度最高的城市
    public String getCityNameEn() {
        if ( null == m_CityList ) return null;

        for (int i = 0; i < m_CityList.size(); i++ ) {
            if ( 0 == m_CityList.get(i).name_cn.compareTo(m_CityEditText.getText().toString()) ) {
                return m_CityList.get(i).name_en;
            }
        }

        return null;
    }

    public Activity_Weather getWeatherActivity() {
        return (Activity_Weather)getActivity();
    }

    public Fragment_WeatherDayDetail getDayDetailFragment() {
        return getWeatherActivity().getDayDetailFragment();
    }

    // 向消息中心发送任务
    public void sendMessage( int msg_type, String msg_text ) {
        Message msg = m_Handler.obtainMessage();
        msg.arg1 = msg_type;
        if ( null != msg_text ) {
            Bundle bundle = new Bundle();
            bundle.putString("msg", msg_text);
            msg.setData(bundle);
        }
        m_Handler.sendMessage(msg);
    }

    public class WeatherCityInfo {
        public String province_cn = null;
        public String district_cn = null;
        public String name_cn = null;
        public String name_en = null;
        public String area_id = null;
    }

    public class WeatherCurrent {
        public String city = null; //城市
        public String pinyin = null; //城市拼音
        public String citycode = null;  //城市编码
        public String date = null; //日期
        public String time = null; //发布时间
        public String postCode = null; //邮编
        public String longitude = null; //经度
        public double latitude = 0; //维度
        public double altitude = 0; //海拔
        public String weather = null;  //天气情况
        public String temp = null; //气温
        public String l_tmp = null; //最低气温
        public String h_tmp = null; //最高气温
        public String WD = null;	 //风向
        public String WS = null; //风力
        public String sunrise = null; //日出时间
        public String sunset = null; //日落时间

    }

    public class WeatherForecast {

    }

    // 获取查询天气的URL
    // type :
    // @city 获取城市列表  city_name为中文城市名称
    // @cur_weather 获取当前天气  city_name为从城市列表获取的城市拼音
    // @forecast_weather 获取未来天气  city_name为从城市列表获取的城市拼音
    private URL getWeatherURL( String type, String city_name ) {
        URL ret_url = null;
        String city_encode = null;

        try {
            if ( 0 == type.compareTo("city") ) {
                try {
                    city_encode = URLEncoder.encode(city_name,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    sendMessage(R.string.Thread_MsgForToast,e.getMessage()+"|"+city_name);
                }
                ret_url = new URL("http://apis.baidu.com/apistore/weatherservice/citylist?cityname="+city_encode);
            } else if ( 0 == type.compareTo("cur_weather") ) {
                ret_url = new URL("http://apis.baidu.com/apistore/weatherservice/weather?citypinyin="+city_name);
            }
        } catch (MalformedURLException e) {
            sendMessage(R.string.Thread_MsgForToast,e.getMessage()+"|"+city_encode);
            ret_url = null;
        }

        return ret_url;
    }

    // 显示天气信息
    // type :
    // @city 获取城市列表
    // @cur_weather 获取当前天气
    // @forecast_weather 获取未来天气
    private void showWeatherInfo(String type, JSONObject json_input ) {
        if (null == type || null == json_input) {
            sendMessage(R.string.Thread_MsgForToast,"showWeatherInfo input is null" + type + "|" + json_input);
            return;
        }

        try {
            int errnum = json_input.getInt("errNum");
            String errmsg = json_input.getString("errMsg");
            // 获取天气相关信息出错
            if (0 != errnum) {
                sendMessage(R.string.Thread_MsgForShowTextOnView, "get weather info error:" + errnum + "|" + errmsg);
                sendMessage(R.string.Thread_MsgForToast,"get weather info error:" + errnum + "|" + errmsg);
            }

            if (0 == type.compareTo("city")) {
                // 解析城市信息
                m_CityList.clear();
                analyzeCityInfoFromJSON(json_input.getJSONArray("retData"));
                sendMessage(R.string.Thread_MsgForUpCityList,null);
            } else if (0 == type.compareTo("cur_weather")) {
                // 解析当前天气
                analyzeCurrentWeatherFromJSON(json_input.getJSONObject("retData"));
                sendMessage(R.string.Thread_MsgForUpCurrentWeather,null);
            } else if (0 == type.compareTo("forecast_weather")) {
                // 解析未来天气
                sendMessage(R.string.Thread_MsgForShowTextOnView,"forecast_weather type will support!:" + type);
            } else {
                sendMessage(R.string.Thread_MsgForToast,"showWeatherInfo unknown type!:" + type);
                sendMessage(R.string.Thread_MsgForShowTextOnView,"showWeatherInfo unknown type!:" + type);
                return;
            }
        } catch (JSONException e) {
            sendMessage(R.string.Thread_MsgForToast,"showWeatherInfo JSONException!:" + e.getMessage());
        }
    }

    // 解析当前天气信息
    private void analyzeCurrentWeatherFromJSON(JSONObject cur_weather) throws JSONException {
        m_CurrentWeather.city = cur_weather.getString("city");
        m_CurrentWeather.pinyin = cur_weather.getString("pinyin");
        m_CurrentWeather.citycode = cur_weather.getString("citycode");
        m_CurrentWeather.date = cur_weather.getString("date");
        m_CurrentWeather.time = cur_weather.getString("time");
        m_CurrentWeather.postCode = cur_weather.getString("postCode");
        m_CurrentWeather.latitude = cur_weather.getDouble("latitude");
        m_CurrentWeather.altitude = cur_weather.getDouble("altitude");
        m_CurrentWeather.weather = cur_weather.getString("weather");
        m_CurrentWeather.temp = cur_weather.getString("temp");
        m_CurrentWeather.l_tmp = cur_weather.getString("l_tmp");
        m_CurrentWeather.h_tmp = cur_weather.getString("h_tmp");
        m_CurrentWeather.WD = cur_weather.getString("WD");
        m_CurrentWeather.WS = cur_weather.getString("WS");
        m_CurrentWeather.sunrise = cur_weather.getString("sunrise");
        m_CurrentWeather.sunset = cur_weather.getString("sunset");
    }

    private void analyzeCityInfoFromJSON(JSONArray json_array) throws JSONException {
        JSONObject tmp_json = null;
        for (int i = 0; i < json_array.length(); i++) {
            WeatherCityInfo tmp_cityinfo = new WeatherCityInfo();
            tmp_json = json_array.getJSONObject(i);
            tmp_cityinfo.province_cn = tmp_json.getString("province_cn");
            tmp_cityinfo.district_cn = tmp_json.getString("district_cn");
            tmp_cityinfo.name_cn = tmp_json.getString("name_cn");
            tmp_cityinfo.name_en = tmp_json.getString("name_en");
            tmp_cityinfo.area_id = tmp_json.getString("area_id");
            m_CityList.add(tmp_cityinfo);
        }
    }
}
