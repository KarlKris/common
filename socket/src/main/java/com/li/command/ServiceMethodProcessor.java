package com.li.command;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.li.session.Session;
import jdk.nashorn.internal.ir.annotations.Ignore;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @Description 业务方法执行器
 * @Author li-yuanwen
 * @Date 2021/3/26 10:40
 */
public class ServiceMethodProcessor implements MethodInvokeProcessor {

    /** 命令 **/
    private final Command command;

    /** 执行方法 **/
    private final Method method;

    /** 执行目标 **/
    private final Object target;

    /** 方法参数 **/
    private final ParameterInfo[] params;

    /** 是否需要验证身份 **/
    private boolean identity;

    public ServiceMethodProcessor(Command command, Method method, Object target, ParameterInfo[] params) {
        this.command = command;
        this.method = method;
        this.target = target;
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
    public boolean isIdentity() {
        return identity;
    }

    /** 方法调用 **/
    @Override
    public Object invoke(Session session, byte[] body, long identityId) throws InvocationTargetException, IllegalAccessException {
        int length = params.length;
        Object[] args = new Object[length];

        String typeName = session.getClass().getTypeName();

        // jsoniter 序列化
        Any any = JsonIterator.deserialize(body);
        for (int i = 0; i < length; i++ ){
            ParameterInfo parameterInfo = params[i];
            if (parameterInfo.isIdentity()) {
                args[i] = identityId;
            }else {
                String name = parameterInfo.getName();
                Type type = parameterInfo.getType();

                // session 注入
                if (typeName.equals(type.getTypeName())) {
                    args[i] = session;
                    continue;
                }
                args[i] = any.get(name).bindTo(type);
            }
        }

        return method.invoke(target, args);
    }
}
