package com.li.persist;

import lombok.Getter;

/**
 * @author li-yuanwen
 * 回写数据库对象信息
 */
@Getter
public class WriteBackElement {

    /** 回写类型 **/
    private WriteBackType type;

    /** 回写对象 **/
    private IEntity target;

    public WriteBackElement(WriteBackType type, IEntity target) {
        this.type = type;
        this.target = target;
    }

}
