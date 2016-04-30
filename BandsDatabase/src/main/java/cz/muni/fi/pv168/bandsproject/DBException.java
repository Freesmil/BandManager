package cz.muni.fi.pv168.bandsproject;

/**
 * Created by Lenka on 30.4.2016.
 */
public class DBException extends RuntimeException {
    public DBException(String msg) {
        super(msg);
    }

    public DBException(Throwable cause) {
        super(cause);
    }

    public DBException(String message, Throwable cause) {
        super(message, cause);
    }
}
