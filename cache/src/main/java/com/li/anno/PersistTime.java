package com.li.anno;

/**
 * 持久化回写类型
 **/
public enum PersistTime {

    /** 修改即回写 **/
    MODIFY,

    /** 统一每1分钟回写 **/
    PER_ONE_MINUTE,

    /** 统一每3分钟回写 **/
    PER_THREE_MINUTE,

    ;

}
