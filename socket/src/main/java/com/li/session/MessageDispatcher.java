package com.li.session;

import com.alibaba.fastjson.JSON;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.li.codec.Response;
import com.li.codec.protocol.impl.InnerMessage;
import com.li.codec.protocol.impl.InnerMessageHeader;
import com.li.command.Command;
import com.li.command.CommandManager;
import com.li.command.MethodInvokeProcessor;
import com.li.exception.ManagedException;
import com.li.proto.MessageProto;
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
    }

    private void checkAndInitServiceExecutors() {
        if (serviceExecutors == null) {
            synchronized (this) {
                if (serviceExecutors != null) {
                    return;
                }
                int num = Runtime.getRuntime().availableProcessors() + 1;
                serviceExecutors = new ExecutorService[num];

                for (int i = 0; i < num; i++) {
                    serviceExecutors[i] = new ThreadPoolExecutor(1, 1,
                            0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1000), new DefaultThreadFactory("service-thread-pool "));
                }
            }
        }
    }

    public void dispatcher(InnerMessage message, Session session) {
        Command c = getCommandFromMessage(message);
        MethodInvokeProcessor methodProcessor = commandManager.getMethodProcessor(c);
        if (methodProcessor.isIdentity() && !session.hasIdentity()) {
            return;
        }

        byte[] body = message.getBody();
        if (log.isInfoEnabled()) {
            log.info("收到请求[{},{}]]", c, body.length);
        }

        int hash;
        if (session.hasIdentity()) {
            hash = hash(session.getIdentity());
        }else {
            hash = hash(session.getId());
        }

        // 检查并初始化线程池
        checkAndInitServiceExecutors();
        serviceExecutors[getExecutorsIndex(hash)]
                .submit(() -> processMessage(methodProcessor, body,  session));
    }

    public void commandRegister(Object instance) {
        commandManager.commandRegister(instance);
    }

    private Command getCommandFromMessage(InnerMessage message) {
        InnerMessageHeader header = message.getHeader();
        int module = header.getModule();
        int command = header.getCommand();

        return new Command(module, command);
    }

    private void processMessage(MethodInvokeProcessor processor, byte[] body, Session session) {

        Response response = null;
        try {
            Object content = processor.invoke(session, body, session.getIdentity());
            if (content instanceof Void) {
                response = Response.emptyResponce();
            }else {
                response = Response.success(content);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.error("消息处理出现异常", e);
            response = Response.unknowErrorResponce();
        }catch (ManagedException e) {
            response = Response.fail(e.getCode());
        }catch (Exception e) {
            log.error("消息处理出现未知异常", e);
            response = Response.unknowErrorResponce();
        }


        session.getChannel().writeAndFlush(JsonStream.serialize(response));
    }

    /**
     * 根据hash找到对应的线程池下标,仿HashMap
     **/
    private int getExecutorsIndex(int hash) {
        int length = serviceExecutors.length;
        return (length - 1) & hash;
    }

    static int hash(Long key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }


    public void shutdown() {
        for (ExecutorService executorService : serviceExecutors) {
            executorService.shutdown();
        }
    }

}
