package in.sids.vachan.impl;

import in.sids.vachan.core.Promise;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class LazyPromise<T> extends BasePromise<T> implements Promise<T> {
    private final CallablePromise<T> callablePromise;

    public LazyPromise(CallablePromise callablePromise) {
        super(callablePromise);
        this.callablePromise = callablePromise;
    }

//    public LazyPromise(Callable<T> callable) {
//        this(new CallablePromise<T>(callable));
//    }

    public LazyPromise(Promise<T> promise, Callable callable) {
        this(new CallablePromise(promise, callable));
    }

    @Override
    public void await() {
        if (!isRealized()) {
            try {
                callablePromise.call();
            } catch (Exception e) {
                setException(e);
            }
        }
    }

    @Override
    public void await(long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException("Cannot support timeouts on inline promises");
    }

    @Override
    public T get() {
        if (!isRealized()) {
            try {
                callablePromise.call();
            }
            catch (Exception e) {
                setException(e);
            }
        }

        return super.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException("Cannot support timeouts on inline promises");
    }
}
