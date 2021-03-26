package com.li.session;

import com.li.id.SnowFlakeId;
import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 连接Session管理中心
 * @Author li-yuanwen
 * @Date 2021/3/16 15:16
 */
public class SessionManager {

    /** id生成 **/
    private SnowFlakeId snowFlakeId = new SnowFlakeId(0 , 0);

    /** 所有未授权Session **/
    private ConcurrentHashMap<Long, Session> anonymous = new ConcurrentHashMap<>();

    /** 所有已授权的Session **/
    private ConcurrentHashMap<Long, Session> identities = new ConcurrentHashMap<>();

    /** 构建连接Session数据 **/
    public Session createSession(Channel channel) {
        long nextId = snowFlakeId.nextId();
        Session session = new Session(nextId, channel);
        anonymous.put(nextId, session);

        channel.attr(Session.SESSION_ID).set(nextId);

        return session;
    }

    /** 移除连接数据 **/
    public Session deleteSession(Channel channel) {
        long id = channel.attr(Session.SESSION_ID).get();
        Session session;
        if ((session = anonymous.remove(id) )== null) {
            session = identities.remove(id);
        }

        return session;
    }

    /** 获取连接Session **/
    public Session getSession(Channel channel) {
        long id = channel.attr(Session.SESSION_ID).get();
        Session session;
        if ((session = anonymous.get(id)) == null) {
            session = identities.get(id);
        }
        return session;
    }




}
