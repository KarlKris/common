package com.li.command;

import com.li.anno.SessionId;
import com.li.anno.SocketCommand;
import com.li.anno.SocketModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 请求管理器
 * @Author li-yuanwen
 * @Date 2021/3/26 10:37
 */
@Slf4j
public class CommandManager {

    /**
     * 命令2方法执行器
     **/
    private Map<Command, MethodProcessor> command2Processors = new ConcurrentHashMap<>();

    public MethodProcessor getMethodProcessor(Command command) {
        return command2Processors.get(command);
    }

    public void commandRegister(Object target) {
        Class<?> clz = AopUtils.getTargetClass(target);
        if (clz.isInterface()) {
            log.warn("指令处理器不可注册接口[" + clz.getName() + "]");
            return;
        }

        SocketModule socketModule = AnnotationUtils.findAnnotation(clz, SocketModule.class);
        if (socketModule == null) {
            return;
        }
        int module = socketModule.module();

        for (Method method : clz.getDeclaredMethods()) {
            SocketCommand socketCommand = AnnotationUtils.findAnnotation(method, SocketCommand.class);
            if (socketCommand == null) {
                continue;
            }

            int command = socketCommand.command();

            Parameter[] parameters = method.getParameters();
            int length = parameters.length;
            ParameterInfo[] infos = new ParameterInfo[length];
            for (int i = 0; i < length; i++) {
                Parameter parameter = parameters[i];
                SessionId sessionId = parameter.getAnnotation(SessionId.class);

                infos[i] = new ParameterInfo(parameter.getName()
                        , parameter.getParameterizedType(), sessionId != null);
            }

            Command c = new Command(module, command);
            MethodProcessor pre = command2Processors.putIfAbsent(c, new MethodProcessor(c, method, target, infos));
            if (pre != null) {
                throw new RuntimeException("注册重复命令号：" + module + "," + command);
            }
        }

    }


}
