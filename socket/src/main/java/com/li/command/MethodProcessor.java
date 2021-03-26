package com.li.command;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @Description 方法执行器
 * @Author li-yuanwen
 * @Date 2021/3/26 10:40
 */
public class MethodProcessor {

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

    public MethodProcessor(Command command, Method method, Object target, ParameterInfo[] params) {
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

    public boolean isIdentity() {
        return identity;
    }

    /** 方法调用 **/
    public Object invoke(byte[] body, long identityId) throws InvocationTargetException, IllegalAccessException {
        int length = params.length;
        Object[] args = new Object[length];

        JSONObject object = JSON.parseObject(Arrays.toString(body));
        for (int i = 0; i < length; i++ ){
            ParameterInfo parameterInfo = params[i];
            if (parameterInfo.isIdentity()) {
                args[i] = identityId;
            }else {
                String name = parameterInfo.getName();
                Type type = parameterInfo.getType();

                JSONObject obj = object.getJSONObject(name);

                args[i] = JSONObject.parseObject(obj.toJSONString(), type);
            }
        }

        return method.invoke(target, args);
    }
}
