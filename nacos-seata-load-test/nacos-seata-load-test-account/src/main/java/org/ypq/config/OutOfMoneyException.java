package org.ypq.config;

public class OutOfMoneyException extends RuntimeException {

    public OutOfMoneyException(String message) {
        super(message);
    }
}
