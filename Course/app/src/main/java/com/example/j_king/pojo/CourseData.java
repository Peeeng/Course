package com.example.j_king.pojo;

/**
 * Created by King on 2018/5/28.
 */

public class CourseData {

    private Integer cNo;
    private String cName;
    private String cTeacher;
    private String cWeeks;
    private Integer cWeekday;
    private Integer cTime;
    private String cAddr;

    public Integer getcNo() {
        return cNo;
    }

    public void setcNo(Integer cNo) {
        this.cNo = cNo;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getcTeacher() {
        return cTeacher;
    }

    public void setcTeacher(String cTeacher) {
        this.cTeacher = cTeacher;
    }

    public String getcWeeks() {
        return cWeeks;
    }

    public void setcWeeks(String cWeeks) {
        this.cWeeks = cWeeks;
    }

    public Integer getcWeekday() {
        return cWeekday;
    }

    public void setcWeekday(Integer cWeekday) {
        this.cWeekday = cWeekday;
    }

    public Integer getcTime() {
        return cTime;
    }

    public void setcTime(Integer cTime) {
        this.cTime = cTime;
    }

    public String getcAddr() {
        return cAddr;
    }

    public void setcAddr(String cAddr) {
        this.cAddr = cAddr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CourseData that = (CourseData) o;

        if (cNo != null ? !cNo.equals(that.cNo) : that.cNo != null) return false;
        if (cName != null ? !cName.equals(that.cName) : that.cName != null) return false;
        if (cTeacher != null ? !cTeacher.equals(that.cTeacher) : that.cTeacher != null)
            return false;
        if (cWeeks != null ? !cWeeks.equals(that.cWeeks) : that.cWeeks != null) return false;
        if (cWeekday != null ? !cWeekday.equals(that.cWeekday) : that.cWeekday != null)
            return false;
        if (cTime != null ? !cTime.equals(that.cTime) : that.cTime != null) return false;
        return cAddr != null ? cAddr.equals(that.cAddr) : that.cAddr == null;

    }

    @Override
    public int hashCode() {
        int result = cNo != null ? cNo.hashCode() : 0;
        result = 31 * result + (cName != null ? cName.hashCode() : 0);
        result = 31 * result + (cTeacher != null ? cTeacher.hashCode() : 0);
        result = 31 * result + (cWeeks != null ? cWeeks.hashCode() : 0);
        result = 31 * result + (cWeekday != null ? cWeekday.hashCode() : 0);
        result = 31 * result + (cTime != null ? cTime.hashCode() : 0);
        result = 31 * result + (cAddr != null ? cAddr.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CourseData{" +
                "cNo=" + cNo +
                ", cName='" + cName + '\'' +
                ", cTeacher='" + cTeacher + '\'' +
                ", cWeeks='" + cWeeks + '\'' +
                ", cWeekday=" + cWeekday +
                ", cTime=" + cTime +
                ", cAddr='" + cAddr + '\'' +
                '}';
    }
}
