package com.kiskee.users.exception;

import com.kiskee.users.util.ExceptionMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String email) {
        super(String.format(ExceptionMessage.USER_ALREADY_EXISTS, email));
    }
}
