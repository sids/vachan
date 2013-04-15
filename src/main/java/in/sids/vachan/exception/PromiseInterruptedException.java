package in.sids.vachan.exception;

public class PromiseInterruptedException extends PromiseException {
    public PromiseInterruptedException(InterruptedException throwable) {
        super(throwable);
    }
}
