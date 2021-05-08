package com.li.persist;

/**
 * @author li-yuanwen
 * 数据库回写管理
 */
public interface WriteBackManager {

    /**
     * 加入回写队列
     * @param element 回写对象载体
     */
    void put(WriteBackElement element);

}
