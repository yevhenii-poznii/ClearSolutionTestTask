package com.kiskee.users.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessage {

    public final String USER_NOT_FOUND = "User %s not found";
    public final String USER_ALREADY_EXISTS = "User with email %s already exists";
    public final String USER_REGISTRATION_RESTRICTION = "User is not old enough to register";
    public final String INVALID_DATE_RANGE = "Invalid date range. Start date must be before or equal to end date";

}
