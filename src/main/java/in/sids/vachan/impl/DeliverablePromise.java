package in.sids.vachan.impl;

import in.sids.vachan.core.Promise;

/**
 * A promise that can be "delivered" once and exactly once -- delivery will cause the
 * promise to be realized.
 *
 * Successful delivery is accomplished using the deliver() method and unsuccessful
 * delivery is accomplished using the fail() method.
 *
 * Trying to deliver to an already realized promise will cause an IllegalStateException
 * to be throw.
 *
 * @param <T>
 */
public class DeliverablePromise<T> extends BasePromise<T> {
    public DeliverablePromise() {
        super();
    }

    public DeliverablePromise(Promise promise) {
        super(promise);
    }

    public void deliver(T value) throws IllegalStateException {
        if (isRealized()) {
            throw new IllegalStateException("Cannot deliver to an already realized promise");
        }
        setValue(value);
    }

    public void fail(Exception throwable) throws IllegalStateException {
        if (isRealized()) {
            throw new IllegalStateException("Cannot deliver to an already realized promise");
        }
        setException(throwable);
    }
}
