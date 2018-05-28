package com.example.j_king.pojo;

/**
 * @name Course
 * @class name：com.example.j_king.pojo
 * @class describe
 * @anthor J-King QQ:2354345263
 * @time 2017/12/27 20:44
 */
public class VoiceContent {
    private String cName ;
    private String cAddr ;
    private Integer cTime ;
    private long triggerAtTime ;
    private Integer voicedTime ;
    String message ;

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getcAddr() {
        return cAddr;
    }

    public void setcAddr(String cAddr) {
        this.cAddr = cAddr;
    }

    public Integer getcTime() {
        return cTime;
    }

    public void setcTime(Integer cTime) {
        this.cTime = cTime;
    }

    public long getTriggerAtTime() {
        return triggerAtTime;
    }

    public void setTriggerAtTime(long triggerAtTime) {
        this.triggerAtTime = triggerAtTime;
    }

    public Integer getVoicedTime() {
        return voicedTime;
    }

    public void setVoicedTime(Integer voicedTime) {
        this.voicedTime = voicedTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
