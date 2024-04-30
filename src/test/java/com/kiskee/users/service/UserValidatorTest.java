package com.kiskee.users.service;

import com.kiskee.users.config.properties.UserProperties;
import com.kiskee.users.exception.RegistrationRestrictionException;
import com.kiskee.users.model.dto.user.UserCreateRequest;
import com.kiskee.users.service.user.UserValidator;
import com.kiskee.users.util.ExceptionMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @InjectMocks
    private UserValidator userValidator;
    @Mock
    private UserProperties userProperties;

    @Test
    void testValidateRegistrationRequest_WhenGivenAllowedAge_ThenGoodPass() {
        UserCreateRequest createRequest = mock(UserCreateRequest.class);
        when(createRequest.getBirthDate()).thenReturn(LocalDate.of(1995, 10,2));

        when(userProperties.getMinimumAgeConstraint()).thenReturn(18);

        userValidator.validateRegistrationRequest(createRequest);
    }

    @Test
    void testValidateRegistrationRequest_WhenGivenNotAllowedAge_ThenThrowRegistrationRestrictionException() {
        UserCreateRequest createRequest = mock(UserCreateRequest.class);
        when(createRequest.getBirthDate()).thenReturn(LocalDate.of(2015, 10,2));

        when(userProperties.getMinimumAgeConstraint()).thenReturn(18);

        assertThatExceptionOfType(RegistrationRestrictionException.class)
                .isThrownBy(() -> userValidator.validateRegistrationRequest(createRequest))
                        .withMessage(ExceptionMessage.USER_REGISTRATION_RESTRICTION);
    }

    @Test
    void testValidateBirthDateRange_WhenGivenValidRange_ThanGoodPass() {
        LocalDate from = LocalDate.of(1990, 1, 1);
        LocalDate to = LocalDate.of(1995, 12, 31);

        userValidator.validateBirthDateRange(from, to);
    }

    @Test
    void testValidateBirthDateRange_WhenGivenInvalidRange_ThenThrowIllegalArgumentException() {
        LocalDate from = LocalDate.of(1995, 12, 31);
        LocalDate to = LocalDate.of(1990, 1, 1);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> userValidator.validateBirthDateRange(from, to))
                .withMessage(ExceptionMessage.INVALID_DATE_RANGE);
    }
}
