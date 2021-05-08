package com.li.persist;

/**
 * 回写数据库类型
 * @author li-yuanwen
 */
public enum WriteBackType {

    /** 新建 **/
    SAVE,

    /** 更新 **/
    UPDATE,

    /** 移除 **/
    REMOVE,

    ;

}
