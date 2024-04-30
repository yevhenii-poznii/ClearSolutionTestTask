package com.kiskee.users.service.user;

import com.kiskee.users.config.properties.UserProperties;
import com.kiskee.users.exception.RegistrationRestrictionException;
import com.kiskee.users.model.dto.user.UserCreateRequest;
import com.kiskee.users.util.ExceptionMessage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@AllArgsConstructor
public class UserValidator {

    private final UserProperties userProperties;

    public void validateRegistrationRequest(UserCreateRequest request) {
        int userAge = LocalDate.now().getYear() - request.getBirthDate().getYear();
        boolean registrationIsAllowed = userAge >= userProperties.getMinimumAgeConstraint();

        if (!registrationIsAllowed) {
            throw new RegistrationRestrictionException(ExceptionMessage.USER_REGISTRATION_RESTRICTION);
        }
    }

    public void validateBirthDateRange(LocalDate from, LocalDate to) {
        boolean isRangeValid = from.isBefore(to) || from.isEqual(to);

        if (!isRangeValid) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_DATE_RANGE);
        }
    }
}
