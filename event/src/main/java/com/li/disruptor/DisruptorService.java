package com.li.disruptor;


import com.li.disruptor.event.DisruptorEventType;

/**
 * @author li-yuanwen
 */
public interface DisruptorService {


    /** 开启Disruptor队列 与#{tryShutDown()配合，防止出现Disruptor线程无效运行} **/
    void start();

    /**
     * 生产事件
     * @param type 事件类型
     * @param body 事件内容
     * @param <T> 事件类
     */
    <T> void produce(DisruptorEventType type, T body);


    /** 尝试关闭Disruptor队列 **/
    void tryShutDown();

    /** 查询Disruptor队列的开启状态 **/
    boolean isStarted();

}
