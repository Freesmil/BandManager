package cz.muni.fi.pv168.bandsproject;

/**
 * Created by Lenka on 30.4.2016.
 */
public class CustomerException extends RuntimeException{
    public CustomerException(String msg) {
        super(msg);
    }

    public CustomerException(Throwable cause) {
        super(cause);
    }

    public CustomerException(String message, Throwable cause) {
        super(message, cause);
    }
}
