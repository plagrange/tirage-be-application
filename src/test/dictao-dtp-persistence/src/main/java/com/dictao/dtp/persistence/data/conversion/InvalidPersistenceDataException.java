package com.dictao.dtp.persistence.data.conversion;

/**
 * <p>
 * This exception has to be used for persistence structure marshalling
 * and unmarshalling errors
  * </p>
 */
public class InvalidPersistenceDataException extends RuntimeException {

    private static final long serialVersionUID = -2441485678099884573L;

    /**
     * @param cause
     *            cause of the currentException
     * @param format
     *            a <a href="../util/Formatter.html#syntax">format string</a>
     * @param args
     *            arguments referenced by the format specifiers in the
     *            <code>format</code> String
     * @see java.util.Formatter
     */
    public InvalidPersistenceDataException(Throwable cause, String format, Object... args) {
        super(String.format(format, args), cause);
    }
    
    /**
     * @param format
     *            a <a href="../util/Formatter.html#syntax">format string</a>
     * @param args
     *            arguments referenced by the format specifiers in the
     *            <code>format</code> String
     * @see java.util.Formatter
     */
    public InvalidPersistenceDataException(String format, Object... args) {
        super(String.format(format, args));
    }
}
