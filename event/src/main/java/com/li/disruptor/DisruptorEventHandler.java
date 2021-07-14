package com.li.disruptor;


import com.li.disruptor.event.DisruptorEvent;
import com.li.disruptor.event.DisruptorEventType;

/**
 * @author li-yuanwen
 * DisruptorEvent 事件处理
 */
public interface DisruptorEventHandler<B> {

    /**
     * 负责的事件
     * @return /
     */
    DisruptorEventType getHandlerEventType();

    /**
     * 事件处理
     * @param event 事件
     */
    void handleEvent(DisruptorEvent<B> event);

}
