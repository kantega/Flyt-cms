package no.kantega.publishing.common.exception;

import no.kantega.commons.exception.SystemException;



public class TransactionLockException extends SystemException {
    public TransactionLockException(String message, Throwable original) {
        super(message, original);
    }
}
