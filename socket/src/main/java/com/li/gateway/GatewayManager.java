package com.li.gateway;

import com.li.client.ClientFactory;
import com.li.codec.MessageManager;
import com.li.codec.protocol.MessageCodecFactory;
import com.li.codec.protocol.MessageType;
import com.li.codec.protocol.impl.GateMessage;
import com.li.codec.protocol.impl.InnerMessage;
import com.li.consumer.ZkServiceConsumer;
import com.li.session.Session;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author li-yuanwen
 *
 * 网关服务
 */
public class GatewayManager {

    @Autowired
    private ZkServiceConsumer zkServiceConsumer;

    /** 转发处理线程池 **/
    private ExecutorService[] executorServices;

    /** 消息管理 **/
    private MessageManager messageManager;

    /** 客户端管理 **/
    private ClientFactory clientFactory;


    private void checkAndInitExecutorServices() {
        if (executorServices == null) {
            synchronized (this) {
                if (executorServices != null) {
                    return;
                }
                int num = Runtime.getRuntime().availableProcessors() + 1;
                executorServices = new ExecutorService[num];

                for (int i = 0; i < num; i++) {
                    executorServices[i] = new ThreadPoolExecutor(1, 1,
                            0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1000), new DefaultThreadFactory("gateway-thread-pool "));
                }
            }
        }
    }


    public void forward(GateMessage gateMessage, Session session) {
        // 消息请求转发
        if (gateMessage.getMessageType() != MessageType.REQUEST) {
            return;
        }

        InnerMessage innerMessage = MessageCodecFactory
                .buildInnerMessageByGateMessageAndSession(gateMessage
                        , session, messageManager.getNextSn());


    }




}
