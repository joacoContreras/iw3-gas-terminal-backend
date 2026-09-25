package ar.edu.iua.iw3.business.exception;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotFoundException extends Exception {

    @Builder
    public NotFoundException(String message, Throwable ex) {
        super(message, ex);
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Throwable ex) {
        super(ex);
    }

}