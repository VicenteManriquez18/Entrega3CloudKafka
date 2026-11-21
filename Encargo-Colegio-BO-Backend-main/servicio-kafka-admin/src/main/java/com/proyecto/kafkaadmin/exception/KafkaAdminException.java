package com.proyecto.kafkaadmin.exception;

public class KafkaAdminException extends RuntimeException {

    private final int status;

    public KafkaAdminException(int status, String message) {
        super(message);
        this.status = status;
    }

    public KafkaAdminException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}

