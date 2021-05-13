package com.li.command;

import com.li.consumer.ZkServiceConsumer;
import com.li.session.Session;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;

/**
 * @author li-yuanwen
 */
public class ForwardMethodProcessor implements MethodInvokeProcessor {

    @Autowired
    private ZkServiceConsumer consumer;

    /** 转发请求服务名称 **/
    private String serviceName;

    /** 命令 **/
    private Command command;

    /** 方法参数 **/
    private final ParameterInfo[] params;

    /** 是否需要验证身份 **/
    private boolean identity;

    public ForwardMethodProcessor(String serviceName, Command command, ParameterInfo[] params) {
        this.serviceName = serviceName;
        this.command = command;
        this.params = params;
        for (ParameterInfo info : params) {
            if (!info.isIdentity()) {
                continue;
            }
            this.identity = true;
            break;
        }
    }

    @Override
    public boolean isForward() {
        return true;
    }

    @Override
    public boolean isIdentity() {
        return identity;
    }

    @Override
    public Object invoke(Session session, byte[] body, long identityId)
            throws InvocationTargetException, IllegalAccessException {
        return null;
    }
}
