package com.li.session;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @Description MessageDispatcherFactoryBean
 * @Author li-yuanwen
 * @Date 2021/3/26 15:48
 */
public class MessageDispatcherFactoryBean implements FactoryBean<MessageDispatcher>, InitializingBean, BeanPostProcessor {

    private MessageDispatcher dispatcher;

    @Override
    public MessageDispatcher getObject() throws Exception {
        return dispatcher;
    }

    @Override
    public Class<?> getObjectType() {
        return MessageDispatcher.class;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.dispatcher = new MessageDispatcher();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        dispatcher.commandRegister(bean);
        return bean;
    }
}
