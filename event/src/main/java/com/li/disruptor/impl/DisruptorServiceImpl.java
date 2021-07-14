package com.li.disruptor.impl;

import com.li.disruptor.*;
import com.li.disruptor.event.DisruptorEvent;
import com.li.disruptor.event.DisruptorEventFactory;
import com.li.disruptor.event.DisruptorEventType;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import com.li.thread.NamedThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author li-yuanwen
 */
@Slf4j
@Service
public class DisruptorServiceImpl implements DisruptorService {

    /** disruptor **/
    private Disruptor<DisruptorEvent<?>> disruptor;
    private DisruptorEventProducer disruptorEventProducer;

    @Autowired
    private ApplicationContext applicationContext;

    private Map<DisruptorEventType, DisruptorEventHandler<?>> handlers;

    /** 开启计数 **/
    private final AtomicInteger count = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        this.handlers = new HashMap<>(DisruptorEventType.values().length);
        for (DisruptorEventHandler<?> handler : applicationContext.getBeansOfType(DisruptorEventHandler.class).values()) {
            DisruptorEventHandler<?> old = handlers.putIfAbsent(handler.getHandlerEventType(), handler);
            if (old != null) {
                throw new BeanInitializationException("DisruptorEventType:" + handler.getHandlerEventType().name() + "事件类型存在多个处理器");
            }
        }
    }

    @Override
    public void start() {

        // 由0到1开启Disruptor队列
        if (this.count.incrementAndGet() == 1) {
            NamedThreadFactory namedThreadFactory = new NamedThreadFactory("Disruptor事件处理线程池");
            // 必须为2的几次方
            int ringBufferSize = 1024 * 8;

            this.disruptor = new Disruptor<>(new DisruptorEventFactory()
                    , ringBufferSize, namedThreadFactory, ProducerType.MULTI, new SleepingWaitStrategy());


            // 消费数量
            int num = Runtime.getRuntime().availableProcessors() / 2;
            DisruptorEventDispatcher[] dispatchers = new DisruptorEventDispatcher[num];
            for (int i = 0; i < num; i++) {
                dispatchers[i] = new DisruptorEventDispatcher(handlers);
            }

            // 连接消费端方法
            disruptor.handleEventsWithWorkerPool(dispatchers);
            // 全局异常处理
            disruptor.setDefaultExceptionHandler(new DisruptorExceptionHandler());

            // 启动
            RingBuffer<DisruptorEvent<?>> ringBuffer = disruptor.start();
            // 创建生产者
            this.disruptorEventProducer = new DisruptorEventProducer(ringBuffer);

            log.warn("Disruptor队列服务已启动");
        }

    }

    @Override
    public void tryShutDown() {
        // 计数值为0,shutdown Disruptor队列服务
        if (this.count.addAndGet(-1) == 0) {
            shutdown();
        }
    }

    @PreDestroy
    private void shutdown() {
        this.count.set(0);

        if (this.disruptor != null) {
            this.disruptor.shutdown();
            log.warn("停止Disruptor队列服务");
            this.disruptor = null;
        }

        if (this.disruptorEventProducer != null) {
            this.disruptorEventProducer = null;
        }
    }


    @Override
    public <T> void produce(DisruptorEventType type, T body) {
        // 队列服务未开启
        if (!isStarted()) {
            log.warn("Disruptor队列服务 未开启");
            return;
        }
        this.disruptorEventProducer.produce(type, body);
    }

    @Override
    public boolean isStarted() {
        return this.count.get() > 0;
    }
}
