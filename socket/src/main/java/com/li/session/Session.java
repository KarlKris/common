package com.li.session;

import io.netty.util.AttributeKey;

import io.netty.channel.Channel;
import lombok.Getter;

/**
 * @Description 连接信息上下文
 * @Author li-yuanwen
 * @Date 2021/3/16 15:16
 */
@Getter
public class Session {

    public static final AttributeKey<Long> SESSION_ID = AttributeKey.newInstance("Seesion_Id");

    /**
     * sessionId
     **/
    private long id;

    /**
     * 授权id
     **/
    private long identity;

    /**
     * channel
     **/
    private Channel channel;

    /**
     * 最近访问时间
     **/
    private long lastTime;


    public Session(long id, Channel channel) {
        this.id = id;
        this.channel = channel;
        this.lastTime = System.currentTimeMillis();
    }

    public boolean hasIdentity() {
        return this.identity != 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Session)) return false;

        Session session = (Session) o;

        return id == session.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    public Session updateTime() {
        this.lastTime = System.currentTimeMillis();
        return this;
    }
}
