package com.li.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * @Description 业务异常
 * @Author li-yuanwen
 * @Date 2021/4/12 11:05
 */
public class ManagedException extends RuntimeException {

    /** 错误代码 */
    private final int code;

    public ManagedException(int code) {
        super(MessageFormatter.format("受管理异常 : [{}]", code).getMessage());
        this.code = code;
    }

    public ManagedException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ManagedException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ManagedException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    /**
     * 获取错误代码
     * @return
     */
    public int getCode() {
        return code;
    }
}
