package in.sids.vachan.core;

import in.sids.vachan.exception.PromiseFailedException;
import in.sids.vachan.exception.PromiseInterruptedException;

import java.util.concurrent.TimeUnit;

/**
 * A promise represents a value that will be realized at some point in time.
 * The general contract is that trying to retrieve the value of an unrealized
 * promise blocks the current thread. Once a promise is realized its value
 * should be retrievable without blocking.
 *
 * @param <T> The type of value the promise represents.
 */
public interface Promise<T> {
    /**
     * Check if this promise is realized or not.
     *
     * If the promise is realized, we can use isSuccess() and isFailure()
     * to figure out if it was successfully realized or failed.
     *
     * This call *must not* block.
     *
     * @return true if the promise is realized and false otherwise
     */
    boolean isRealized();

    /**
     * Given that the promise is realized, check if it was successfully realized.
     *
     * This is the opposite of isFailure().
     *
     * @return true if the promise was successfully realized, false if it was a failure
     * @throws IllegalStateException if isRealized() == false
     */
    boolean isSuccess() throws IllegalStateException;

    /**
     * Given that the promise is realized, check if it was unsuccessfully realized.
     *
     * This is the opposite of isSuccess().
     *
     * @return false if the promise was successfully realized, true if it was a failure
     * @throws IllegalStateException if isRealized() == false
     */
    boolean isFailure() throws IllegalStateException;

    /**
     * Wait for the promise to be realized. Waits indefinitely or until the
     * thread is interrupted.
     *
     * @throws PromiseInterruptedException if the thread is interrupted while waiting
     */
    void await() throws PromiseInterruptedException;

    /**
     * Wait for the promise to be realized or the given timeout to occur.
     *
     * @throws PromiseInterruptedException if the thread is interrupted while waiting
     */
    void await(long timeout, TimeUnit unit) throws PromiseInterruptedException;

    /**
     * If the promise is realized, retrieve the value; otherwise, block until the promise
     * is realized and then retrieve that value (if promise is successfully realized) or
     * throw exception (if promise if unsuccessfully realized).
     *
     * @return The realized value.
     * @throws PromiseFailedException if the promise is unsuccessfully realized; wraps the underlying exception
     * @throws PromiseInterruptedException if the thread is interrupted while waiting
     */
    T get() throws PromiseFailedException, PromiseInterruptedException;

    /**
     * Same as get() but with a timeout. Returns if promise is realized or timeout occurs.
     *
     * @param timeout
     * @param unit
     * @return
     * @throws PromiseFailedException if the promise is unsuccessfully realized; wraps the underlying exception
     * @throws PromiseInterruptedException if the thread is interrupted while waiting
     */
    T get(long timeout, TimeUnit unit) throws PromiseFailedException, PromiseInterruptedException;

    /**
     * Add a listener that will be called once the promise is successfully realized.
     *
     * Listeners should be called in the order in which they are added.
     *
     * Listeners should be called on the same thread that realizes the promise.
     *
     * @param successListener
     */
    void addSuccessListener(PromiseSuccessListener<T> successListener);

    /**
     * Add a listener that will be called once the promise is unsuccessfully realized.
     *
     * Listeners should be called in the order in which they are added.
     *
     * Listeners should be called on the same thread that realizes the promise.
     *
     * @param failureListener
     */
    void addFailureListener(PromiseFailureListener<T> failureListener);
}
