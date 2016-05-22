package cz.muni.fi.pv168.bandsproject;

/**
 * Created by Lenka on 6.5.2016.
 */
public class LeaseException extends RuntimeException {
    public LeaseException(String msg) {
        super(msg);
    }

    public LeaseException(Throwable cause) {
        super(cause);
    }

    public LeaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
