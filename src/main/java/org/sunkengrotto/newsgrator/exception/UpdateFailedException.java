package org.sunkengrotto.newsgrator.exception;

public class UpdateFailedException extends RuntimeException {

    public UpdateFailedException(Exception ex) {
        super(ex);
    }
}