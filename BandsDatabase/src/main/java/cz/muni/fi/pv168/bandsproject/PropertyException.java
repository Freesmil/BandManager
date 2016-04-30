package cz.muni.fi.pv168.bandsproject;

/**
 * Created by Lenka on 30.4.2016.
 */
public class PropertyException extends RuntimeException {
    public PropertyException(String msg) {
        super(msg);
    }

    public PropertyException(Throwable cause) {
        super(cause);
    }

    public PropertyException(String message, Throwable cause) {
        super(message, cause);
    }
}
