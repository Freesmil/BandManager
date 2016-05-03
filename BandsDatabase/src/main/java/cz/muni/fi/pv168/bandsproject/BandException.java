package cz.muni.fi.pv168.bandsproject;

/**
 *
 * @author Lenka
 */
public class BandException extends RuntimeException {
    public BandException(String message, Throwable cause) {
        super(message, cause);
    }
    public BandException(String msg) {
        super(msg);
    }
}
