package com.kiskee.users.mapper;

import com.kiskee.users.model.dto.user.UserCreateRequest;
import com.kiskee.users.model.dto.user.UserDto;
import com.kiskee.users.model.dto.user.UserUpdateRequest;
import com.kiskee.users.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Optional;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    User toEntity(UserCreateRequest createRequest);

    User updateEntity(@MappingTarget User user, UserUpdateRequest updateRequest);

    UserDto toDto(User user);

    default Optional<User> toEntityOpt(UserCreateRequest createRequest) {
        return Optional.of(toEntity(createRequest));
    }
}
