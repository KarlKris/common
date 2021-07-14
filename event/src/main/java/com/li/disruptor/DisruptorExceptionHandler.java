package com.li.disruptor;

import com.li.disruptor.event.DisruptorEvent;
import com.lmax.disruptor.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author li-yuanwen
 * Disruptor 全局异常
 */
@Slf4j
public class DisruptorExceptionHandler implements ExceptionHandler<DisruptorEvent<?>> {

    @Override
    public void handleEventException(Throwable ex, long sequence, DisruptorEvent<?> event) {
        log.error("Disruptor队列事件[{}]处理发生未知异常", event.getName(), ex);
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        log.error("Start Disruptor队列发生未知异常", ex);
    }

    @Override
    public void handleOnShutdownException(Throwable ex) {
        log.error("Shutdown Disruptor队列发生未知异常", ex);
    }
}
