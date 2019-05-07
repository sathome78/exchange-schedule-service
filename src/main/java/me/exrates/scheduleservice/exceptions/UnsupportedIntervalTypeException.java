package me.exrates.scheduleservice.exceptions;

public class UnsupportedIntervalTypeException extends RuntimeException {

    public UnsupportedIntervalTypeException(String intervalType) {
        super("No such interval type " + intervalType);
    }
}