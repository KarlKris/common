package com.li.session;

import com.li.consumer.ZkServiceConsumer;
import com.li.proto.MessageProto;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description 转发分发器
 * @Author li-yuanwen
 * @Date 2021/4/13 10:31
 */
@Slf4j
public class ForwardDispatcher {

    /**
     * 线程池
     **/
    private ExecutorService executorService;

    /**
     * zookeeper 消费
     **/
    private ZkServiceConsumer zkServiceConsumer;

    public ForwardDispatcher(ZkServiceConsumer zkServiceConsumer) {
        this.zkServiceConsumer = zkServiceConsumer;
    }

    private void checkAndInitExecutors() {
        if (executorService != null) {
            return;
        }

        synchronized (this) {
            if (executorService != null) {
                return;
            }
            int num = Runtime.getRuntime().availableProcessors() + 1;
            executorService = new ThreadPoolExecutor(num, num * 2,
                    10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), new DefaultThreadFactory("forward-thread-pool"));
        }
    }

    public void forward(MessageProto.Message message, Session session, String serverId) {

    }

    private void process(MessageProto.Message message, Session session) {

    }

    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

}
