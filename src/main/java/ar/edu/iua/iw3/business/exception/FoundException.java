package ar.edu.iua.iw3.business.exception;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FoundException extends Exception {

    @Builder
    public FoundException(String message, Throwable ex) {
        super(message, ex);
    }

    public FoundException(String message) {
        super(message);
    }

    public FoundException(Throwable ex) {
        super(ex);
    }

}