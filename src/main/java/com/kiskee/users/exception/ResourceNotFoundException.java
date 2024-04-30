package com.kiskee.users.exception;

import com.kiskee.users.util.ExceptionMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(UUID userId) {
        super(String.format(ExceptionMessage.USER_NOT_FOUND, userId));
    }
}
