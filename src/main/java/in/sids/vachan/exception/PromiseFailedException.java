package in.sids.vachan.exception;

public class PromiseFailedException extends PromiseException {
    public PromiseFailedException(Throwable throwable) {
        super(throwable);
    }
}
