package com.example.hugo.myhandler.handler;
/**
 *
 */
public class Message {
    private int code;
    private String msg;
    Handler target;

    public Message(){}
    public Message(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
