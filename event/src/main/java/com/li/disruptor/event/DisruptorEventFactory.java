package com.li.disruptor.event;

import com.lmax.disruptor.EventFactory;

/**
 * @author li-yuanwen
 */
public class DisruptorEventFactory implements EventFactory<DisruptorEvent<?>> {

    @Override
    public DisruptorEvent<?> newInstance() {
        return new DisruptorEvent<Object>();
    }

}
