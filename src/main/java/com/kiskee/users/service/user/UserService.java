package com.kiskee.users.service.user;

import com.kiskee.users.model.dto.MultipleResponseDto;
import com.kiskee.users.model.dto.ResponseDto;
import com.kiskee.users.model.dto.user.UserCreateRequest;
import com.kiskee.users.model.dto.user.UserUpdateRequest;

import java.time.LocalDate;
import java.util.UUID;

public interface UserService {

    ResponseDto createUser(UserCreateRequest createRequest);

    ResponseDto getUser(UUID id);

    ResponseDto updateUser(UUID userId, UserUpdateRequest updateRequest);

    void deleteUser(UUID id);

    MultipleResponseDto findUsersByBirthDateRange(LocalDate from, LocalDate to);
}
