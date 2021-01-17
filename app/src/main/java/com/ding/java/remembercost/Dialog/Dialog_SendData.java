package com.ding.java.remembercost.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ding.java.remembercost.Activity.Activity_CostRecordList;
import com.ding.java.remembercost.data.CostRecord;
import com.ding.java.remembercost.data.IpPortRecord;
import com.ding.java.remembercost.data.My_SqlLite;
import com.ding.java.remembercost.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by xiaobei on 2017/1/11.
 */

public class Dialog_SendData extends Dialog implements View.OnClickListener {

    private Context m_Context = null;
    private AutoCompleteTextView m_ServerIP = null;
    private AutoCompleteTextView m_ServerPort = null;
    private ArrayAdapter<String> m_ListAdapter_IP = null;
    private ArrayAdapter<String> m_ListAdapter_Port = null;
    private My_SqlLite m_My_SqlLite = new My_SqlLite();

    private TextView m_TextView_Progress = null;
    private ImageView m_ImageView_Progress = null;
    private RotateAnimation m_RotateAnimation = null;
    private Button m_Button_Send = null;
    private TextView m_TextView_ErrMsg = null;

    private Thread.UncaughtExceptionHandler m_UncaughtExceptionHandler = new Thread.UncaughtExceptionHandler(){
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            //TODO
            Log.e("ding_log","!!!线程未捕获异常！"+thread.toString()+"|||"+ex.toString());
            errorMsg("!!!线程未捕获异常！"+thread.toString()+"|||"+ex.toString());
        }
    };
    Handler m_Handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case R.string.Thread_MsgForErrorMsg:
                    if ( null != m_TextView_ErrMsg ) {
                        m_TextView_ErrMsg.setText(msg.getData().getString("msg"));
                        m_TextView_ErrMsg.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.string.Thread_MsgForUpdateProgress:
                    if ( null != m_TextView_Progress ) {
                        m_TextView_Progress.setText(msg.getData().getString("msg"));
                    }
                    break;
                case R.string.Thread_MsgForSendDataOver:
                    refreshAutoCompleteText();
                    break;
                default:
                    break;
            }
        }
    };
    // socket线程
    private final Runnable m_SendDataRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            IpPortRecord ip_port =  getIpPort();
            // 检查IP和端口
            if ( 0 == ip_port.m_Ip.length() ) {
                errorMsg("IP地址信息错误");
                clearProgress();
                return;
            }
            if ( 0 == ip_port.m_Port ) {
                errorMsg("端口信息错误");
                clearProgress();
                return;
            }
            List<CostRecord> m_DataList = ((Activity_CostRecordList)m_Context).getCostRecord();
            if ( null == m_DataList ) {
                errorMsg("发送数据为空");
                clearProgress();
                return;
            }
            m_My_SqlLite.addIpPortRecord(ip_port);
            updateProgress(0,m_DataList.size());

            try {
                Socket client_socket = new Socket();
                Log.i("ding_log", "connect!");
                InetSocketAddress inet_addr = new InetSocketAddress(ip_port.m_Ip,ip_port.m_Port);
                client_socket.connect(inet_addr, 5000);
                // 取得socket输出流
                Log.i("ding_log", "getOutputStream!");
                OutputStream out_stream = client_socket.getOutputStream();
                InputStream in_stream = client_socket.getInputStream();

// 需要增加和服务端匹配大端or小端的方法

//                ToastText("发送数据中……!");
                for ( int i = 0; i < m_DataList.size(); i++ ) {
                    String tmp_send_str = m_DataList.get(i).m_Date + "|" +
                            m_DataList.get(i).m_Type + "|" +
                            m_DataList.get(i).m_SubType + ":" +
                            m_DataList.get(i).m_Fee + "|" +
                            m_DataList.get(i).m_Remarks;

                    // 发送消息
                    Log.i("ding_log", "write!");
                    // 由于windows识别的为小端数据所以需要将发送的字节顺序改为小端
                    byte send_byte[] = tmp_send_str.getBytes("UnicodeLittleUnmarked");
                    // Unicode UnicodeBigUnmarked UnicodeLittleUnmarked
                    out_stream.write(send_byte);
                    out_stream.flush();
                    Log.i("ding_log", "send msg:"+tmp_send_str);

                    byte read_byte[] = new byte[1024];
                    in_stream.read(read_byte);
                    String read_str = convertLittleToBig(read_byte);
                    Log.i("ding_log", "read msg:"+read_byte+"|"+read_str);
                    boolean is_ok = read_str.equals("OK");
                    if ( true == is_ok ) {
                        updateProgress(i+1,m_DataList.size());
                        continue;
                    }
                    else {
                        break;
                    }
                }

                out_stream.close();
                in_stream.close();

                client_socket.close();
            } catch (IllegalArgumentException e) {
                Log.e("ding_log", "服务器无法连接！["+ e.toString() + "]");
                errorMsg("服务器无法连接！["+ e.toString() + "]");
                clearProgress();
            }
            catch (SocketTimeoutException e) {
                // TODO Auto-generated catch block
                Log.e("ding_log", "连接服务器失败！"+e.toString());
                errorMsg("连接服务器失败 "+e.toString());
                clearProgress();
            } catch (UnknownHostException e){
                Log.e("ding_log", "未知错误"+e.toString());
                errorMsg("未知错误"+e.toString());
                clearProgress();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("ding_log", "连接错误！"+e.toString());
                errorMsg("连接错误！"+e.toString());
                clearProgress();
            } catch (NullPointerException e) {
                Log.e("ding_log", "空指针错误！"+e.toString());
                errorMsg("空指针错误！"+e.toString());
                clearProgress();
            } catch (Exception e) {
                Log.e("ding_log", "其他错误！"+e.toString());
                errorMsg("其他错误！"+e.toString());
                clearProgress();
            }

            sendDataOver();
        }
    };

    public String convertLittleToBig(byte[] in_byte) {
//        byte out_byte1[] = new byte[in_byte.length];
        int in_len = 0;
        // 计算出输出数组长度
        for ( int i = 0; i < in_byte.length/2; i++ ) {
            if ( 0 == in_byte[i*2] && 0 == in_byte[i*2+1] ) {
                break;
            }
            in_len++;
        }
        in_len = in_len*2;
        byte out_byte[] = new byte[in_len];
        // 将输入数据按照实际长度赋值到输出数组
        for ( int i = 0; i < in_len; i++ ) {
            out_byte[i] = in_byte[i];
        }
        // 由于windows发送过来的数据为小端数据，所以按照小端数据格式生成String
        String tmp_str = null;
        try {
            tmp_str = new String(out_byte,"UnicodeLittleUnmarked");
        } catch (UnsupportedEncodingException e) {
            Log.e("ding_log","convert byte mode error:"+in_byte+"|"+e.getMessage());
            return null;
        }
        return tmp_str;
    }

    // 显示错误信息
    private void errorMsg(String error_msg) {
        Message msg = m_Handler.obtainMessage();
        msg.arg1 = R.string.Thread_MsgForErrorMsg;
        Bundle bundle = new Bundle();
        bundle.putString("msg", error_msg);
        msg.setData(bundle);
        m_Handler.sendMessage(msg);
    }
    // 清除进度信息
    private void clearProgress() {
        Message msg = m_Handler.obtainMessage();
        msg.arg1 = R.string.Thread_MsgForUpdateProgress;
        Bundle bundle = new Bundle();
        bundle.putString("msg", "-/-");
        msg.setData(bundle);
        m_Handler.sendMessage(msg);
    }
    // 更新进度信息
    private void updateProgress(int cur_num,int all_num) {
        Message msg = m_Handler.obtainMessage();
        msg.arg1 = R.string.Thread_MsgForUpdateProgress;
        Bundle bundle = new Bundle();
        bundle.putString("msg", cur_num+"/"+all_num);
        msg.setData(bundle);
        m_Handler.sendMessage(msg);
    }
    // 数据发送完毕
    private void sendDataOver() {
        Message msg = m_Handler.obtainMessage();
        msg.arg1 = R.string.Thread_MsgForSendDataOver;
        Bundle bundle = new Bundle();
        bundle.putString("msg", "over");
        msg.setData(bundle);
        m_Handler.sendMessage(msg);
    }

    public Dialog_SendData(Context context) {
        super(context);
        m_Context = context;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_senddata);

        //  设置IP和PORT的自动补全文本
        m_ServerIP = (AutoCompleteTextView)findViewById(R.id.ip_port_ip_domain);
        m_ServerPort = (AutoCompleteTextView)findViewById(R.id.ip_port_port);
        m_ListAdapter_IP = new ArrayAdapter<String>(
                m_Context,
                R.layout.arrayadapter_textview,
                R.id.arrayadapter_textview_t);
        m_ServerIP.setAdapter(m_ListAdapter_IP);
        m_ListAdapter_Port = new ArrayAdapter<String>(
                m_Context,
                R.layout.arrayadapter_textview,
                R.id.arrayadapter_textview_t);
        m_ServerPort.setAdapter(m_ListAdapter_Port);
        refreshAutoCompleteText();

        // 设置发送进度条
        m_TextView_Progress = (TextView) findViewById(R.id.dialog_senddata_textview_progress);
        m_ImageView_Progress = (ImageView) findViewById(R.id.dialog_senddata_imageview_sending);
        CreateImageViewAnimation();

        m_Button_Send = (Button)findViewById(R.id.dialog_senddata_button_send);
        m_Button_Send.setOnClickListener(this);

        m_TextView_ErrMsg = (TextView) findViewById(R.id.dialog_senddata_textview_errmsg);

        // 取得屏幕像素
        WindowManager wm = (WindowManager) m_Context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = metrics.widthPixels;
        getWindow().setAttributes(params);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

        m_ImageView_Progress.clearAnimation();
    }

    private void CreateImageViewAnimation() {
        // 启动自定义旋转效果
        Drawable processbar = m_Context.getResources().getDrawable(R.drawable.processbar_wait);
        int width = processbar.getIntrinsicWidth();
        int height = processbar.getIntrinsicHeight();
        m_RotateAnimation = new RotateAnimation(0,359,width/2,height/2);
        LinearInterpolator lin = new LinearInterpolator();
        m_RotateAnimation.setInterpolator(lin);
        //  设置旋转一圈用时
        m_RotateAnimation.setDuration(2000);
        // 设置旋转持续圈数
        m_RotateAnimation.setRepeatCount(Animation.INFINITE);
        // 设置重复方式
        m_RotateAnimation.setRepeatMode(Animation.RESTART);
        m_RotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Toast.makeText(m_Context,"旋转开始！",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Toast.makeText(m_Context,"旋转结束！",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Toast.makeText(m_Context,"重复N次！",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // 设置TEXTVIEW的大小和进度图片一致
        m_TextView_Progress.setWidth(width);
        m_TextView_Progress.setHeight(height);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_senddata_button_send :
                Log.i("ding_log",this.toString()+"'s "+m_Button_Send.toString()+" is clicked!");
                // 隐藏错误信息
                m_TextView_ErrMsg.setVisibility(View.GONE);
                // 发送记录
                Thread senddata_thread = new Thread(m_SendDataRunnable);
                senddata_thread.setUncaughtExceptionHandler(m_UncaughtExceptionHandler);
                senddata_thread.start();
                break;
            default:
                Log.i("ding_log",this.toString() + " unknown view clicked!" + v.toString());
                break;
        }
    }

    private IpPortRecord getIpPort() {
        IpPortRecord ip_port_record = new IpPortRecord();
        ip_port_record.m_Ip = m_ServerIP.getText().toString();
        try {
            ip_port_record.m_Port = Integer.valueOf(m_ServerPort.getText().toString());
        } catch ( NumberFormatException e) {
            Log.e("ding_log",e.toString());
            ip_port_record.m_Port = 0;
        }
        if ( 0 == ip_port_record.m_Ip.length() ) {
            Log.e("ding_log","ip is null");
        }
        return ip_port_record;
    }

    // 刷新IP和端口的下拉提示信息
    private void refreshAutoCompleteText() {
        List<IpPortRecord> ip_port_list = m_My_SqlLite.getIpPortRecord(null);
        if ( null == ip_port_list ) return;

        m_ListAdapter_IP.clear();
        m_ListAdapter_Port.clear();
        for ( int i = 0; i < ip_port_list.size() && i < 5; i++ ) {
            m_ListAdapter_IP.add(ip_port_list.get(i).m_Ip);
            m_ListAdapter_Port.add(String.format("%d",ip_port_list.get(i).m_Port));
        }
        m_ListAdapter_IP.notifyDataSetChanged();
        m_ListAdapter_Port.notifyDataSetChanged();
    }
}
