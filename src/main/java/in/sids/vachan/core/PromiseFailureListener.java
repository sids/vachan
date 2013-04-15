package in.sids.vachan.core;

public interface PromiseFailureListener<T> {
    void onFailure(Promise<T> promise) throws InterruptedException;
}
