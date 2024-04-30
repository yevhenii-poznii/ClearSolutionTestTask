package com.kiskee.users.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegistrationRestrictionException extends RuntimeException {

    public RegistrationRestrictionException(String message) {
        super(message);
    }
}
