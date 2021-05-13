package com.li.command;

import com.li.session.Session;

import java.lang.reflect.InvocationTargetException;

/**
 * @author li-yuanwen
 * 命令方法调用处理器
 */
public interface MethodInvokeProcessor {

    /**
     * 消息是否是转发
     * @return
     */
    boolean isForward();

    /**
     * @return 是否需要检查Channel身份标识id
     */
    boolean isIdentity();


    /**
     * 方法调用
     * @param session
     * @param body 方法体内容
     * @param identityId channel 身份标识
     * @return 方法调用结果
     */
    Object invoke(Session session, byte[] body, long identityId) throws InvocationTargetException,
            IllegalAccessException;

}
