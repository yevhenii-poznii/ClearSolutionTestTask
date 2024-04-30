package com.kiskee.users.mapper;

import com.kiskee.users.model.dto.user.UserCreateRequest;
import com.kiskee.users.model.dto.user.UserDto;
import com.kiskee.users.model.dto.user.UserUpdateRequestDto;
import com.kiskee.users.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    private UserMapper userMapper;

    private static final UUID USER_ID = UUID.fromString("e85bcd9a-00ed-45d3-87c9-ac0c8ad68203");

    @BeforeEach
    public void setup() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void testToEntity() {
        UserCreateRequest createRequest = new UserCreateRequest("email@google.com", "John", "Doe",
                LocalDate.of(1990, 2, 13), "some address", "380999999999");

        User entity = userMapper.toEntity(createRequest);
        assertThat(entity.getId()).isNull();
        assertThat(entity.getEmail()).isEqualTo(createRequest.getEmail());
        assertThat(entity.getFirstName()).isEqualTo(createRequest.getFirstName());
        assertThat(entity.getLastName()).isEqualTo(createRequest.getLastName());
        assertThat(entity.getBirthDate()).isEqualTo(createRequest.getBirthDate());
        assertThat(entity.getAddress()).isEqualTo(createRequest.getAddress());
        assertThat(entity.getPhoneNumber()).isEqualTo(createRequest.getPhoneNumber());
    }

    @Test
    void testToUpdatedEntity() {
        User userTarget = new User(USER_ID, "email@google.com", "John", "Doe",
                LocalDate.of(1990, 2, 13), "some address", "380999999999");
        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto("newEmail@google.com", null, null,
                null, null, null);

        User updatedUser = userMapper.updateEntity(userTarget, updateRequest);
        assertThat(updatedUser.getId()).isEqualTo(USER_ID);
        assertThat(updatedUser.getEmail()).isEqualTo(updateRequest.getEmail());
        assertThat(updatedUser.getFirstName()).isEqualTo(userTarget.getFirstName());
        assertThat(updatedUser.getLastName()).isEqualTo(userTarget.getLastName());
        assertThat(updatedUser.getBirthDate()).isEqualTo(userTarget.getBirthDate());
        assertThat(updatedUser.getAddress()).isEqualTo(userTarget.getAddress());
        assertThat(updatedUser.getPhoneNumber()).isEqualTo(userTarget.getPhoneNumber());
    }

    @Test
    void testToDto() {
        User user = new User(USER_ID, "email@google.com", "John", "Doe",
                LocalDate.of(1990, 2, 13), "some address", "380999999999");

        UserDto dto = userMapper.toDto(user);
        assertThat(dto.getId()).isEqualTo(USER_ID);
        assertThat(dto.getEmail()).isEqualTo(user.getEmail());
        assertThat(dto.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(dto.getLastName()).isEqualTo(user.getLastName());
        assertThat(dto.getBirthDate()).isEqualTo(user.getBirthDate());
        assertThat(dto.getAddress()).isEqualTo(user.getAddress());
        assertThat(dto.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
    }

    @Test
    void testToEntityOpt() {
        UserCreateRequest createRequest = new UserCreateRequest("email@google.com", "John", "Doe",
                LocalDate.of(1990, 2, 13), "some address", "380999999999");

        Optional<User> entityOpt = userMapper.toEntityOpt(createRequest);
        assertThat(entityOpt).isPresent();
        assertThat(entityOpt.get().getEmail()).isEqualTo(createRequest.getEmail());
        assertThat(entityOpt.get().getFirstName()).isEqualTo(createRequest.getFirstName());
        assertThat(entityOpt.get().getLastName()).isEqualTo(createRequest.getLastName());
        assertThat(entityOpt.get().getBirthDate()).isEqualTo(createRequest.getBirthDate());
        assertThat(entityOpt.get().getAddress()).isEqualTo(createRequest.getAddress());
        assertThat(entityOpt.get().getPhoneNumber()).isEqualTo(createRequest.getPhoneNumber());
    }

}
