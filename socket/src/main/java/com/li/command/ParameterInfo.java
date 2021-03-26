package com.li.command;

import lombok.Getter;

import java.lang.reflect.Type;

/**
 * @Description 参数信息
 * @Author li-yuanwen
 * @Date 2021/3/26 11:07
 */
@Getter
public class ParameterInfo {

    /** 参数名称 **/
    private final String name;

    /** 参数类型 **/
    private final Type type;

    /** 是否是标识身份的参数 **/
    private final boolean identity;

    public ParameterInfo(String name, Type type, boolean identity) {
        this.name = name;
        this.type = type;
        this.identity = identity;
    }
}
