package in.sids.vachan.impl;

import in.sids.vachan.core.Promise;

import java.util.concurrent.Callable;

/**
 * A promise that accepts a Callable and implements the callable interface itself.
 *
 * When this class's call() method is called, the underlying Callable's call()
 * method is called and the promise is realized with the value returned from it.
 * The value is also returned by the call() method itself.
 *
 * @param <T>
 */
public class CallablePromise<T> extends BasePromise<T> implements Promise<T>, Callable<T> {
    private final Callable<T> callable;

    public CallablePromise(Callable<T> callable) {
        super();
        this.callable = callable;
    }

    public CallablePromise(Promise<T> promise, Callable<T> callable) {
        super(promise);
        this.callable = callable;
    }

    @Override
    final public T call() throws Exception {
        try {
            T value = callable.call();
            setValue(value);
            return value;
        }
        catch (Exception e) {
            setException(e);
            throw e;
        }
    }
}
