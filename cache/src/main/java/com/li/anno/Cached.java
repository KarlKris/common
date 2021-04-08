package com.li.anno;

/**
 * 缓存注解
 *
 * @author li-yuanwen
 */
public @interface Cached {

    /**
     * 持久化时间节点
     **/
    PersistTime time() default PersistTime.MODIFY;
}
