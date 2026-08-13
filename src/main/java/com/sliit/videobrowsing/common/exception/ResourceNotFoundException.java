package com.sliit.videobrowsing.common.exception;

/** Thrown by any module's service layer when a lookup by id fails. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
