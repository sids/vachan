package in.sids.vachan.core;

public interface PromiseSuccessListener<T> {
    void onSuccess(Promise<T> promise) throws InterruptedException;
}
