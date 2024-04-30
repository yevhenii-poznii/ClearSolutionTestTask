package com.kiskee.users.model.dto.user;

import java.time.LocalDate;

public interface UserUpdateRequest {

    String getEmail();
    String getFirstName();
    String getLastName();
    LocalDate getBirthDate();
    String getAddress();
    String getPhoneNumber();
}
