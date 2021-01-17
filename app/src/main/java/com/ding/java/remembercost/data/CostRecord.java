package com.ding.java.remembercost.data;

public class CostRecord {
    public long m_Sequence;
    public int m_Date;
    public String m_Type;
    public String m_Type_py;
    public String m_SubType;
    public String m_SubType_py;
    public float m_Fee;
    public String m_Remarks;
    public String m_Remarks_py;
    public CostRecord() {
        m_Sequence = 0;
        m_Date = 0;
        m_Type = "";
        m_Type_py = "";
        m_Type_py = "";
        m_SubType = "";
        m_SubType_py = "";
        m_Fee = 0;
        m_Remarks = "";
        m_Remarks_py = "";
    }
}
