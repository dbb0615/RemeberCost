package com.ding.java.remembercost.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.ding.java.remembercost.Other.RC_Config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;


/**
 * Created by Administrator on 2016/5/11.
 */
public class My_SqlLite {
    private static SQLiteDatabase m_DB = null;// 数据存储
    private static Cursor m_Cursor = null;// 游标
    private static String m_DBFileName = null;
    private static String m_ErrorStr = null;
    private static File m_DBFile = null;
    private static String m_CostRecordTab = "DCostRecord";
    private static String m_IPPortTab = "DIPPortRecord";

    public My_SqlLite() {
        if (null == m_DBFile) m_DBFile = new File(RC_Config.getDBFile());
        if ( false == m_DBFile.exists() ) {
            Log.w("ding_log","db file not exists! -- "+m_DBFile.getName());
        }
        if ( false == m_DBFile.canRead() ) {
            Log.w("ding_log","db file cat not read! -- "+m_DBFile.getName());
        }
        if ( false == m_DBFile.canWrite() ) {
            Log.w("ding_log","db file cat not write! -- "+m_DBFile.getName());
        }
        m_DBFileName = RC_Config.getDBFile();
    }

    private boolean connectDB() {
        /*if ( null != m_DB ) {
            m_ErrorStr = "data base is busy!";
            return false;
        }*/

        if ( null == m_DB ) {
            try {
                m_DB = SQLiteDatabase.openOrCreateDatabase(m_DBFile, null );
            }
            catch (SQLiteException e) {
                Log.e("ding_log","open or create database failed! "+e.getCause()+"|"+e.getMessage());
                m_ErrorStr = "open or create database failed! "+e.getCause()+"|"+e.getMessage();
                return false;
            }
        }

        Log.i("ding_log","open or create database ok!");

        try {
            // 创建消费记录表
            String create_sql_str = "create table if not exists " + m_CostRecordTab +
                    "(sequence INTEGER primary key,date smallint,type VARCHAR,subtype VARCHAR,fee float,remarks VARCHAR)";
            Log.i("ding_log","create sql:"+create_sql_str);
            m_DB.execSQL(create_sql_str);

            // 创建IP端口记录表
            create_sql_str = "create table if not exists " + m_IPPortTab +
                    "(sequence INTEGER primary key,ip VARCHAR,port INTEGER)";
            Log.i("ding_log","create sql:"+create_sql_str);
            m_DB.execSQL(create_sql_str);
        }
        catch ( SQLException e) {
            Log.e("ding_log","exec sql error:"+e.getMessage()+"|"+e.getCause());
            m_ErrorStr = "exec sql error:"+e.getMessage()+"|"+e.getCause();
            return false;
        }
        return true;
    }

    public void disConnectDB() {
        if ( null != m_DB ) {
            m_DB.close();
            m_DB = null;
        }
        if ( null != m_Cursor ) {
            m_Cursor.close();
            m_Cursor = null;
        }
    }

    public boolean addCostRecord(int i_date, String i_type, String i_subtype, float i_fee, String i_remarks) {
        if ( false == connectDB() ) {
            return false;
        }
        boolean ret = true;
        // 生成序列
        long tmp_sequence = getSequence();
        // 插入数据
        ContentValues tmp_values = new ContentValues();
        tmp_values.put("sequence",tmp_sequence);
        tmp_values.put("date", i_date );
        tmp_values.put("type", i_type);
        tmp_values.put("subtype", i_subtype);
        tmp_values.put("fee", i_fee);
        tmp_values.put("remarks", i_remarks);
        // 插入失败返回
        long insert_ret = m_DB.insert(m_CostRecordTab, null, tmp_values );
        if ( -1 == insert_ret ) {
            Log.i("ding_log","insert failed!");
            m_ErrorStr = "insert failed!";
            ret = false;
        }

        return ret;
    }

    private long getSequence() {
        // 生成序列
        Date tmp_date = new Date();
        return tmp_date.getTime();
    }

    public String getError() {
        return m_ErrorStr;
    }

    /* 获取指定消费记录 */
    public List<CostRecord> getCostRecord(int i_begin_date,
                                          int i_end_date,
                                          String[] i_types,
                                          String i_text,
                                          String i_fee) {
        if ( false == connectDB() ) {
            return null;
        }

        String tmp_columns[] = {"sequence","date","type","subtype","fee","remarks"};
        String tmp_order_by = "date desc";
        /* 由于sqllite的like不能模糊匹配，需要在fetch的时候过滤数据 */
        m_Cursor = m_DB.query( m_CostRecordTab, tmp_columns, null, null,
                null, null,tmp_order_by );

        /* 过滤 */
        return fetchRecordList( i_begin_date, i_end_date, i_types, i_text, i_fee );
    }

    /* 获取指定消费记录 */
    public List<CostRecord> getCostRecord_test(int i_begin_date,
                                          int i_end_date,
                                          String[] i_types,
                                          String i_text,
                                          String i_fee) {
        if ( false == connectDB() ) {
            return null;
        }

        String where_str = null;
        if ( 0 != i_begin_date ) {
            if ( where_str == null ) where_str = " date >= " + i_begin_date;
            else where_str += "and date >= " + i_begin_date;
        }
        if ( 0 != i_end_date && m_Cursor.getInt(1) > i_end_date ) {
            if ( where_str == null ) where_str = " date <= " + i_end_date + " ";
            else where_str += " and date <= " + i_end_date + " ";
        }
        if (    null != i_types  ) {
            String where_types = null;
            for (String tmp_type : i_types
                 ) {
                if ( where_types == null ) where_types = "( type = \"" + tmp_type + "\" ";
                else where_types += "or type = \"" + tmp_type + "\" ";
            }

            if ( where_types != null ) {
                if ( where_str == null ) where_str = where_types + ")";
                else where_str += "and " + where_types + ")";
            }
        }
        // 过滤的文本在子类型和备注中均不存在
        if (    null != i_text ) {
            if ( where_str == null ) where_str = " ( subtype like \"%" + i_text + "%\" or remarks like \"%" + i_text + "%\" ) ";
            else where_str += " and ( subtype like \'%" + i_text + "%\' or remarks like \'%" + i_text + "%\' )";
        }

        String order_str = "order by date,type,subtype";
        String select_str = "select sequence,date,type,subtype,fee,remarks from DCostRecord ";

        String tmp_columns[] = {"sequence","date","type","subtype","fee","remarks"};
        String tmp_selection = where_str;
        String tmp_order_by = "date desc";
        /* 由于sqllite的like不能模糊匹配，需要在fetch的时候过滤数据 */
        m_Cursor = m_DB.query( m_CostRecordTab, tmp_columns, tmp_selection, null,
                null, null,tmp_order_by );

        /* 过滤 */
        return fetchRecordList();
    }

    public boolean addIpPortRecord(IpPortRecord rec) {
        if ( false == connectDB() ) {
            return false;
        }
        boolean ret = true;
        long tmp_sequence = getSequence();
        String i_ip = rec.m_Ip;
        int i_port = rec.m_Port;

        String tmp_columns[] = {"count(*)"};
        String tmp_selections = "ip = ? and port = ?";
        String tmp_selections_args[] = {i_ip,String.format("%d",i_port)};
        m_Cursor = m_DB.query( m_IPPortTab, tmp_columns, tmp_selections, tmp_selections_args,
                null, null,null );
        if (true == m_Cursor.moveToNext() ) {
            long count_num = m_Cursor.getLong(0);
            if (0 < count_num)  Log.i("ding_log","ip port record is exists["+count_num+"]!");
            return true;
        }
        // 插入数据
        ContentValues tmp_values = new ContentValues();
        tmp_values.put("sequence",tmp_sequence);
        tmp_values.put("ip", i_ip );
        tmp_values.put("port", i_port);
        // 插入失败返回
        long insert_ret = m_DB.insert(m_IPPortTab, null, tmp_values );
        if ( -1 == insert_ret ) {
            Log.e("ding_log","insert failed!");
            m_ErrorStr = "insert failed!";
            ret = false;
        }

        return ret;
    }

    /* 获取IP和PORT记录 */
    public List<IpPortRecord> getIpPortRecord(IpPortRecord rec) {
        if ( false == connectDB() ) {
            return null;
        }

        String tmp_columns[] = {"sequence","ip","port"};
        String tmp_order_by = "sequence";

        m_Cursor = m_DB.query( m_IPPortTab, tmp_columns, null, null,
                null, null,tmp_order_by );

        // fetch 数据
        List<IpPortRecord> ret_list = null;
        if ( 0 < m_Cursor.getCount() ) {
            IpPortRecord tmp_data = new IpPortRecord();
            while (true == m_Cursor.moveToNext()) {
                if ( null == ret_list ) {
                    ret_list = new ArrayList<IpPortRecord>();
                }
                tmp_data.m_Sequence = m_Cursor.getLong(0);
                tmp_data.m_Ip = m_Cursor.getString(1);
                tmp_data.m_Port = m_Cursor.getInt(2);
                ret_list.add(tmp_data);
            }
        }

        return ret_list;
    }

    private boolean FindTextInArray(String[] str_array, String target_str) {
        for ( int i = 0; i < str_array.length; i++ ) {
            if ( true == str_array[i].equals(target_str)) {
                return true;
            }
        }
        return false;
    }

    /* columns
     * "sequence","date","type","subtype","fee","remarks"
     * 没有数据返回null
     */
    private List<CostRecord> fetchRecordList(int i_begin_date,
                                             int i_end_date,
                                             String[] i_types,
                                             String i_text,
                                             String i_fee) {
        if ( null == m_Cursor ) return null;
        CostRecord tmp_cost_record = null;
        List<CostRecord> ret_list = null;
        if ( 0 < m_Cursor.getCount() ) {
            while ( true == m_Cursor.moveToNext() ) {
                /* 过滤无效数据 */
                if ( 0 != i_begin_date && m_Cursor.getInt(1) < i_begin_date ) {
                    continue;
                }
                if ( 0 != i_end_date && m_Cursor.getInt(1) > i_end_date ) {
                    continue;
                }
                if (    null != i_types &&
                        false == FindTextInArray(i_types,m_Cursor.getString(2)) ) {
                    continue;
                }
                // 过滤的文本在子类型和备注中均不存在
                if (    null != i_text &&
                        false == m_Cursor.getString(3).contains(i_text) &&
                         false == m_Cursor.getString(5).contains(i_text) ) {
                    continue;
                }
//                if (    null != i_fee &&
//                        false == m_Cursor.getString(4).contains(i_fee) ) {
//                    continue;
//                }

                if ( null == tmp_cost_record ) {
                    tmp_cost_record = new CostRecord();
                }
                if (null==ret_list) {
                    ret_list = new ArrayList<CostRecord>();
                }
                tmp_cost_record.m_Sequence = m_Cursor.getLong(0);
                tmp_cost_record.m_Date = m_Cursor.getInt(1);
                tmp_cost_record.m_Type = m_Cursor.getString(2);
                tmp_cost_record.m_Type_py = getHanyuHeaders(tmp_cost_record.m_Type);
                tmp_cost_record.m_SubType = m_Cursor.getString(3);
                tmp_cost_record.m_SubType_py = getHanyuHeaders(tmp_cost_record.m_SubType);
                tmp_cost_record.m_Fee = m_Cursor.getFloat(4);
                tmp_cost_record.m_Remarks = m_Cursor.getString(5);
                tmp_cost_record.m_Remarks_py = getHanyuHeaders(tmp_cost_record.m_Remarks);
                ret_list.add(tmp_cost_record);
                tmp_cost_record = null;
            }

            m_Cursor.close();
            m_Cursor = null;
            /*排序*/
            sortCostRecord(ret_list);
        }

        return ret_list;
    }

    /* columns
     * "sequence","date","type","subtype","fee","remarks"
     * 没有数据返回null
     */
    private List<CostRecord> fetchRecordList() {
        if ( null == m_Cursor ) return null;
        CostRecord tmp_cost_record = null;
        List<CostRecord> ret_list = null;
        if ( 0 < m_Cursor.getCount() ) {
            while ( true == m_Cursor.moveToNext() ) {

                if ( null == tmp_cost_record ) {
                    tmp_cost_record = new CostRecord();
                }
                if (null==ret_list) {
                    ret_list = new ArrayList<CostRecord>();
                }
                tmp_cost_record.m_Sequence = m_Cursor.getLong(0);
                tmp_cost_record.m_Date = m_Cursor.getInt(1);
                tmp_cost_record.m_Type = m_Cursor.getString(2);
                tmp_cost_record.m_Type_py = getHanyuHeaders(tmp_cost_record.m_Type);
                tmp_cost_record.m_SubType = m_Cursor.getString(3);
                tmp_cost_record.m_SubType_py = getHanyuHeaders(tmp_cost_record.m_SubType);
                tmp_cost_record.m_Fee = m_Cursor.getFloat(4);
                tmp_cost_record.m_Remarks = m_Cursor.getString(5);
                tmp_cost_record.m_Remarks_py = getHanyuHeaders(tmp_cost_record.m_Remarks);
                ret_list.add(tmp_cost_record);
                tmp_cost_record = null;
            }

            m_Cursor.close();
            m_Cursor = null;
        }

        return ret_list;
    }

    // 获取指定日期以后的所有记录
    public List<CostRecord> getAllCostRecordByDate(String i_date ) {
        if ( false == connectDB() ) {
            return null;
        }

        String tmp_columns[] = {"sequence","date","type","subtype","fee","remarks"};
        String tmp_selection = "date >= ?";
        String tmp_selection_args[] = {i_date};
        String tmp_group_by = "date";
        if ( null == i_date || 0 == i_date.length() ) {
            m_ErrorStr = "date is null!";
            return null;
        }
        else {
            m_Cursor = m_DB.query( m_CostRecordTab, tmp_columns, tmp_selection, tmp_selection_args,
                    null, null,tmp_group_by );
        }

        return fetchRecordList(0,0,null,null,null);
    }

    // 删除记录
    public boolean deleteCostRecord(long sequence ) {
        if ( false == connectDB() ) {
            return false;
        }

        boolean ret = true;
        String tmp_sql_str;
        tmp_sql_str = "delete from " + m_CostRecordTab +
                " where sequence = " + sequence;

        try {
            m_DB.execSQL(tmp_sql_str);
        }
        catch (SQLException e) {
            m_ErrorStr = "exec delete cost record sql error! [" + sequence + "]#["+
                    e.getCause() + "][" + e.getMessage() + "]";
            ret = false;
        }

        return ret;
    }

    // 根据序号更新记录
    public boolean updateCostRecord(long sequence, int i_date, String i_type, String i_subtype, float i_fee, String i_remarks) {
        if (false == connectDB()) {
            return false;
        }

        boolean ret = true;
        String tmp_sql_str;
        tmp_sql_str = "update " + m_CostRecordTab +
                " set date = " + i_date +
                ", type = '" + i_type +
                "', subtype = '" + i_subtype +
                "', fee = " + i_fee +
                ", remarks = '" + i_remarks +
                "' where sequence = " + sequence;

        try {
            m_DB.execSQL(tmp_sql_str);
        }
        catch (SQLException e) {
            m_ErrorStr = "exec update cost record sql error! [" + sequence + "]#["+
                    e.getCause() + "][" + e.getMessage() + "]";
            ret = false;
        }

        return  ret;
    }

    /* 通过制定文本查找记录，
     * 在详细类型和备注中搜索 */
//    public List<CostRecord> GetCostRecordByText( String i_text ) {
//        if ( false == connectDB() ) {
//            return null;
//        }
//        if ( null == i_text ) {
//            i_text = "";
//        }
//
//        List<CostRecord> ret_list = new ArrayList<CostRecord>();
//
//        String tmp_columns[] = {"sequence","date","type","subtype","fee","remarks"};
//        String tmp_selection = "type like ? or subtype like ? or remarks like ?";
//        String tmp_selection_args[] = {i_text,i_text,i_text};
//        String tmp_group_by = "date desc";
//        m_Cursor = m_DB.query( m_CostRecordTab, tmp_columns, null, null,
//                null, null, tmp_group_by );
//
//        Log.i("ding_log","query selection:"+tmp_selection);
//        Log.i("ding_log","query selection_args:"+tmp_selection_args[0]+"|"+tmp_selection_args[1]+"|"+tmp_selection_args[2]);
//
//        return FetchRecordListByText(i_text);
//    }

    private String getHanyuHeaders( String hanyu_str ) {
        if ( 0 == hanyu_str.length() ) return hanyu_str;
        String ret_str = new String();
        /*为了提高效率只转化前两个汉字*/
        for (int i = 0; i < hanyu_str.length() && i < 2; i++) {
            char word = hanyu_str.charAt(i);
            String[] pinyinarray = PinyinHelper.toHanyuPinyinStringArray(word);
            if ( null != pinyinarray ) {
                Log.d("ding_log",pinyinarray[0]);
                ret_str += pinyinarray[0].charAt(0);
            }
            else {
                ret_str += word;
            }
        }

        return ret_str;
    }
    /* 将链表中的数据排序 */
    private boolean sortCostRecord(List<CostRecord> cost_record) {
        if ( null == cost_record ) return false;
        /*日期逆序排列，其他正序排列*/
        Comparator<CostRecord> cost_rec_cmp = new Comparator<CostRecord>() {
            @Override
            public int compare(CostRecord lhs, CostRecord rhs) {
                /*日期*/
                if ( lhs.m_Date > rhs.m_Date ) return -1;
                if ( lhs.m_Date < rhs.m_Date ) return 1;
                /*类型*/
                int cmp_ret = lhs.m_Type_py.compareTo(rhs.m_Type_py);
                if ( 0 != cmp_ret ) return cmp_ret;
                /*子类型*/
                cmp_ret = lhs.m_SubType_py.compareTo(rhs.m_SubType_py);
                if ( 0 != cmp_ret ) return cmp_ret;
                /*费用*/
                if ( lhs.m_Fee > rhs.m_Fee ) return -1;
                if ( lhs.m_Fee < rhs.m_Fee ) return 1;
                /*备注*/
                cmp_ret = lhs.m_Remarks_py.compareTo(rhs.m_Remarks_py);
                if ( 0 != cmp_ret ) return cmp_ret;

                /*所有数据都相同*/
                return 0;
            }
        };
        try {
            Collections.sort( cost_record, cost_rec_cmp);
        } catch ( ClassCastException e ) {
            Log.e( "ding_log", "CostRecord sort exception! "+e.getMessage() );
            return false;
        }
        return true;
    }
}
