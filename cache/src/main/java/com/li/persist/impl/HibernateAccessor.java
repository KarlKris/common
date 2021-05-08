package com.li.persist.impl;

import com.li.persist.Accessor;
import com.li.persist.IEntity;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import java.io.Serializable;

/**
 * @author li-yuanwen
 * hibernate访问数据库
 */
public class HibernateAccessor extends HibernateDaoSupport implements Accessor {

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> T load(PK id, Class<T> tClass) {
        return getHibernateTemplate().get(tClass, id);
    }

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> void remove(T t) {
        getHibernateTemplate().delete(t);
    }

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> void update(T t) {
        getHibernateTemplate().update(t);
    }

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> void create(T t) {
        getHibernateTemplate().save(t);
    }

}
