package ar.edu.iua.iw3.business.exception;

import lombok.Builder;
import lombok.NoArgsConstructor;
@NoArgsConstructor
public class BusinessException extends Exception{

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Throwable ex) {
        super(ex);
    }

    @Builder
    public BusinessException(String message, Throwable ex) {
        super(message, ex);
    }
}
