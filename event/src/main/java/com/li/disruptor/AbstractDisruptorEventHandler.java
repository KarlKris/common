package com.li.disruptor;


import com.li.disruptor.event.DisruptorEvent;

/**
 * @author li-yuanwen
 */
public abstract class AbstractDisruptorEventHandler<B> implements DisruptorEventHandler<B> {


    @Override
    public void handleEvent(DisruptorEvent<B> event) {
        doHandlerEvent(event.getBody());
    }



    /**
     * 实际内容处理
     * @param body 事件内容
     */
    protected abstract void doHandlerEvent(B body);
}
