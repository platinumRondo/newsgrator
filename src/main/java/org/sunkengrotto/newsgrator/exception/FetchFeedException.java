package org.sunkengrotto.newsgrator.exception;

public class FetchFeedException extends Exception {

    private String url;

    public FetchFeedException(String url, String message) {
        super(message);
        this.url = url;
    }

    public FetchFeedException(String url, Exception e) {
        super(e);
        this.url = url;
    }

    @Override
    public String toString() {
        return String.format("URL: %s%nMessage: %s", this.url, this.getMessage());
    }

}
