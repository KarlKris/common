package com.li.codec;

/**
 * @Description 响应消息
 * @Author li-yuanwen
 * @Date 2021/4/12 11:08
 */
public class Response {

    /** 成功响应码 **/
    public static final int SUCCESS = 200;
    /** 未知异常 **/
    public static final int UNKNOW_ERROR = -255;

    private int code;

    /** 响应消息 **/
    private Object content;

    public static Response success(Object content) {
        Response response = new Response();
        response.code = SUCCESS;
        response.content = content;
        return response;
    }

    public static Response emptyResponce() {
        Response response = new Response();
        response.code = SUCCESS;
        return response;
    }

    public static Response unknowErrorResponce() {
        Response response = new Response();
        response.code = UNKNOW_ERROR;
        return response;
    }

    public static Response fail(int code) {
        Response response = new Response();
        response.code = code;
        return response;
    }

}
