package no.kantega.openaksess.rest.domain;

/**
 * @author Kristian Myrhaug
 * @since 2015-06-24
 */
public class Fault extends RuntimeException {

    private int code;

    public Fault(int code) {
        this.code = code;
    }

    public Fault(int code, String message) {
        super(message);
        this.code = code;
    }

    public Fault(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public Fault(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public Fault(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
