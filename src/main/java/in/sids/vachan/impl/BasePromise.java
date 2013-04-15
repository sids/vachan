package in.sids.vachan.impl;

import in.sids.vachan.core.*;
import in.sids.vachan.exception.PromiseFailedException;
import in.sids.vachan.exception.PromiseInterruptedException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * A base implementation of the idea of a promise. This still does not
 * provide a way to realize the promise -- that is something that concrete
 * implementations are expected to provide.
 *
 * @param <T>
 */
public abstract class BasePromise<T> implements Promise<T> {
    private final CountDownLatch countDownLatch;

    private boolean realized = false;
    private T value = null;
    private Exception exception = null;

    private final List<PromiseSuccessListener<T>> successHandlers = new ArrayList<PromiseSuccessListener<T>>();
    private final List<PromiseFailureListener<T>> failureHandlers = new ArrayList<PromiseFailureListener<T>>();

    public BasePromise() {
        countDownLatch = new CountDownLatch(1);
    }

    public BasePromise(Promise<T> promise) {
        this();

        promise.addSuccessListener(new PromiseSuccessListener<T>() {
            @Override
            public void onSuccess(Promise<T> promise) throws InterruptedException {
                setValue(promise.get());
            }
        });

        promise.addFailureListener(new PromiseFailureListener<T>() {
            @Override
            public void onFailure(Promise<T> promise) throws InterruptedException {
                try {
                    promise.get();
                } catch (RuntimeException e) {
                    setException(new Exception(e.getCause()));
                }
            }
        });
    }

    @Override
    public boolean isRealized() {
        return countDownLatch.getCount() == 0;
    }

    @Override
    public boolean isSuccess() {
        if (!isRealized()) {
            throw new IllegalStateException("Promise isn't ready yet");
        }

        return value != null;
    }

    @Override
    public boolean isFailure() {
        if (!isRealized()) {
            throw new IllegalStateException("Promise isn't ready yet");
        }

        return value == null;
    }

    @Override
    public void await() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new PromiseInterruptedException(e);
        }
    }

    @Override
    public void await(long timeout, TimeUnit unit) {
        try {
            countDownLatch.await(timeout, unit);
        } catch (InterruptedException e) {
            throw new PromiseInterruptedException(e);
        }
    }

    @Override
    public T get() {
        await();

        if (value != null) {
            return value;
        }
        else {
            throw new PromiseFailedException(exception);
        }
    }

    @Override
    public T get(long timeout, TimeUnit unit) {
        await(timeout, unit);

        if (value != null) {
            return value;
        }
        else {
            throw new PromiseFailedException(exception);
        }
    }

    @Override
    final public void addSuccessListener(PromiseSuccessListener<T> successListener) {
        successHandlers.add(successListener);

        if (isRealized() && isSuccess()) {
            invokeSuccessHandler(successListener);
        }
    }

    @Override
    final public void addFailureListener(PromiseFailureListener<T> failureListener) {
        failureHandlers.add(failureListener);

        if (isRealized() && isFailure()) {
            invokeFailureHandler(failureListener);
        }
    }

    final protected T getValue() {
        return value;
    }

    final protected void setValue(T value) {
        realized = true;
        this.value = value;
        countDownLatch.countDown();

        for (PromiseSuccessListener<T> successHandler : successHandlers) {
            invokeSuccessHandler(successHandler);
        }
    }

    final protected Exception getException() {
        return exception;
    }

    final protected void setException(Exception t) {
        realized = true;
        this.exception = t;
        countDownLatch.countDown();

        for (PromiseFailureListener<T> failureHandler : failureHandlers) {
            invokeFailureHandler(failureHandler);
        }
    }

    private void invokeSuccessHandler(PromiseSuccessListener<T> successHandler) {
        try {
            successHandler.onSuccess(this);
        } catch (Throwable t) {
            t.printStackTrace();  // TODO: convert to logging
        }
    }

    private void invokeFailureHandler(PromiseFailureListener<T> failureHandler) {
        try {
            failureHandler.onFailure(this);
        } catch (Throwable t) {
            t.printStackTrace();  // TODO: convert to logging
        }
    }
}
