package com.li.disruptor;

import com.li.disruptor.event.DisruptorEvent;
import com.li.disruptor.event.DisruptorEventType;
import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author li-yuanwen
 * DisruptorEvent事件分发
 */
@Slf4j
public class DisruptorEventDispatcher implements WorkHandler<DisruptorEvent<?>> {

    private java.util.Map<DisruptorEventType, DisruptorEventHandler<?>> handlers;

    public DisruptorEventDispatcher(Map<DisruptorEventType, DisruptorEventHandler<?>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void onEvent(DisruptorEvent<?> event) throws Exception {
        DisruptorEventHandler handler = handlers.get(event.getName());
        if (handler == null) {
            return;
        }

        if (log.isDebugEnabled()) {
            int time = event.calculateHandleTime();
            if (time > 500) {
                log.debug("Disruptor 队列 事件[{}]开始处理前耗时[{}], 超过0.5s", event.getName(), time);
            }
        }

        handler.handleEvent(event);

        if (log.isDebugEnabled()) {
            int time = event.calculateHandleTime();
            if (time > 500) {
                log.debug("Disruptor 队列 事件[{}]处理耗时[{}], 超过0.5s", event.getName(), time);
            }
        }
    }
}
