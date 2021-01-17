package com.ding.java.remembercost.Other;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2016/6/29.
 */
public class RC_Config {

    static private String m_DataPath_Str = null;
    static private String m_DBFile_Str = null;
    static private String m_ExportedFile_Str = null; /* exported file of cost data */

    // 主页面启动时设置
    static public int init(String db_filename, String exported_data_filename ) {
        // 设置过以后不允许改变
        if ( null != m_DBFile_Str && null != m_DataPath_Str ) return 0;

        // 没有SD卡时存储位置
        File tmp_dbfile = new File("./"+db_filename);
        String sd_path = RC_Config.getSDPath();
        // 存在sd卡
        if ( null != sd_path ) {
            m_DataPath_Str = sd_path + "/RememberCost/";
            File db_path = new File(m_DataPath_Str);

            // 检查目录是否存在
            if ( false == db_path.mkdir() && false == db_path.exists() ) {
                Log.e("ding_log","create app path failed!:"+m_DataPath_Str);
                m_DataPath_Str = "./";
                m_DBFile_Str = m_DataPath_Str + db_filename;
                return 1;
            }
            File db_file = new File(m_DataPath_Str + db_filename);
            // 当前目录数据文件不存在
            if ( false == db_file.exists() ) {
                try {
                    db_file.createNewFile();
                } catch (IOException e) {
                    Log.e("ding_log","create db file failed!:"+db_file.getName()+"|||"+e.getMessage());
                    m_DataPath_Str = "./";
                    m_DBFile_Str = m_DataPath_Str + db_filename;
                    return 1;
                }

                // sd卡中不存在数据，当前目录存在数据，将当前目录的数据移动到sd卡
                if ( true == tmp_dbfile.exists() ) {
                    tmp_dbfile.renameTo( new File(m_DataPath_Str) );
                }
            }
        } else {
            m_DataPath_Str = "./";
        }
        m_DBFile_Str = m_DataPath_Str + db_filename;
        m_ExportedFile_Str = m_DataPath_Str + exported_data_filename;
        return 0;
    }

    static public String getDBFile() {
        return m_DBFile_Str;
    }
    static public String getExportedFile() {
        return m_ExportedFile_Str;
    }
    static public String getDataPath() {
        return m_DataPath_Str;
    }

    static private String getSDPath(){
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if(sdCardExist) {
            File sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            return sdDir.toString();
        } else {
            return null;
        }
    }
}
