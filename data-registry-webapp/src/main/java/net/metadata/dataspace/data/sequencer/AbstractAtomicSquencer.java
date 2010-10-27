package net.metadata.dataspace.data.sequencer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 1:17:50 PM
 */
public abstract class AbstractAtomicSquencer {
    protected AtomicInteger atomicInterger;

    protected AbstractAtomicSquencer() {
        atomicInterger = new AtomicInteger(0);
    }

    public int next() {
        return atomicInterger.incrementAndGet();
    }

    public int current() {
        return atomicInterger.get();
    }
}
