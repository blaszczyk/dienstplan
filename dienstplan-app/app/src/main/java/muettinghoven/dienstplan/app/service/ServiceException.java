package muettinghoven.dienstplan.app.service;

public class ServiceException extends Exception {
    public ServiceException() {
        super();
    }

    public ServiceException(String detailMessage) {
        super(detailMessage);
    }

    public ServiceException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ServiceException(Throwable throwable) {
        super(throwable);
    }
}
