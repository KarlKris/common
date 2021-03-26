package com.li.session;

import com.alibaba.fastjson.JSON;
import com.li.command.Command;
import com.li.command.CommandManager;
import com.li.command.MethodProcessor;
import com.li.proto.MessageProto;
import com.li.proto.MessageProtoFactory;
import com.sun.org.apache.regexp.internal.RE;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description 消息分发处理器
 * @Author li-yuanwen
 * @Date 2020/4/6 23:31
 */
@Slf4j
public class MessageDispatcher {

    // 业务处理线程池
    private ExecutorService[] serviceExecutors;

    // 方法处理器
    private final CommandManager commandManager;

    public MessageDispatcher() {
        this.commandManager = new CommandManager();
        int num = Runtime.getRuntime().availableProcessors() + 1;
        serviceExecutors = new ExecutorService[num];

        for (int i = 0; i < num; i++) {
            serviceExecutors[i] = new ThreadPoolExecutor(1, 1,
                    0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1000), new DefaultThreadFactory("service-thread "));
        }
    }

    public void dispatcher(MessageProto.Message message, Session session) {
        int hash;
        if (session.hasIdentity()) {
            hash = hash(session.getIdentity());
        }else {
            hash = hash(session.getId());
        }
        serviceExecutors[getExecutorsIndex(hash)]
                .submit(() -> processMessage(message, session));
    }

    public void commandRegister(Object instance) {
        commandManager.commandRegister(instance);
    }

    private void processMessage(MessageProto.Message message, Session session) {
        MessageProto.Header header = message.getHeader();
        int module = header.getModule();
        int command = header.getCommand();

        Command c = new Command(module, command);

        byte[] bytes = message.getBody().toByteArray();

        if (log.isInfoEnabled()) {
            log.info("收到请求[{},{}]]", c, bytes.length);
        }

        MethodProcessor methodProcessor = commandManager.getMethodProcessor(c);
        if (methodProcessor.isIdentity() && !session.hasIdentity()) {
            return;
        }
        Object result = null;
        try {
            result = methodProcessor.invoke(bytes, session.getIdentity());
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        session.getChannel().writeAndFlush(MessageProtoFactory.createServiceResqMessage(JSON.toJSONString(result)));
    }

    /**
     * 根据hash找到对应的线程池下标,仿HashMap
     **/
    private int getExecutorsIndex(int hash) {
        int length = serviceExecutors.length;
        return (length - 1) & hash;
    }

    static final int hash(Long key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }


    public void shutdown() {
        for (ExecutorService executorService : serviceExecutors) {
            executorService.shutdown();
        }
    }

}
