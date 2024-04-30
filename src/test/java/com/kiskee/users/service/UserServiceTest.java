package com.kiskee.users.service;

import com.kiskee.users.exception.DuplicateResourceException;
import com.kiskee.users.exception.RegistrationRestrictionException;
import com.kiskee.users.exception.ResourceNotFoundException;
import com.kiskee.users.mapper.UserMapper;
import com.kiskee.users.model.dto.MultipleResponseDto;
import com.kiskee.users.model.dto.ResponseDto;
import com.kiskee.users.model.dto.user.UserCreateRequest;
import com.kiskee.users.model.dto.user.UserDto;
import com.kiskee.users.model.dto.user.UserUpdateRequestDto;
import com.kiskee.users.model.entity.User;
import com.kiskee.users.repository.UserRepository;
import com.kiskee.users.service.user.UserServiceImpl;
import com.kiskee.users.service.user.UserValidator;
import com.kiskee.users.util.ExceptionMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserValidator userValidator;

    private static final UUID USER_ID = UUID.fromString("e85bcd9a-00ed-45d3-87c9-ac0c8ad68203");

    @Test
    void testCreateUser_WhenValidUserCreateRequest_ThenCreateUser() {
        UserCreateRequest createRequest = new UserCreateRequest("email@google.com", "John", "Doe",
                LocalDate.of(1990, 2, 13), "some address", "380999999999");

        when(userRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);

        User user = mock(User.class);
        when(userMapper.toEntityOpt(createRequest)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        UserDto createdUser = new UserDto(USER_ID, createRequest.getEmail(), createRequest.getFirstName(), createRequest.getLastName(),
                createRequest.getBirthDate(), createRequest.getAddress(), createRequest.getPhoneNumber());
        when(userMapper.toDto(user)).thenReturn(createdUser);

        ResponseDto result = userService.createUser(createRequest);

        verify(userValidator).validateRegistrationRequest(createRequest);

        assertThat(result.getData()).isEqualTo(createdUser);
    }

    @Test
    void testCreateUser_WhenUserAlreadyExists_ThenThrowDuplicateResourceException() {
        UserCreateRequest createRequest = new UserCreateRequest("email@google.com", "John", "Doe",
                LocalDate.of(1990, 2, 13), "some address", "380999999999");

        when(userRepository.existsByEmail(createRequest.getEmail())).thenReturn(true);

        assertThatExceptionOfType(DuplicateResourceException.class)
                .isThrownBy(() -> userService.createUser(createRequest))
                .withMessage(String.format(ExceptionMessage.USER_ALREADY_EXISTS, createRequest.getEmail()));

        verify(userValidator).validateRegistrationRequest(createRequest);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    void testCreateUser_WhenUserIsUnderAge_ThenThrowRegistrationRestrictionException() {
        UserCreateRequest createRequest = new UserCreateRequest("email@google.com", "John", "Doe",
                LocalDate.of(2014, 2, 13), "some address", "380999999999");

        doThrow(new RegistrationRestrictionException(ExceptionMessage.USER_REGISTRATION_RESTRICTION))
                .when(userValidator).validateRegistrationRequest(createRequest);

        assertThatExceptionOfType(RegistrationRestrictionException.class)
                .isThrownBy(() -> userService.createUser(createRequest))
                .withMessage(ExceptionMessage.USER_REGISTRATION_RESTRICTION);
    }

    @Test
    void testGetUser_WhenUserExists_ThenReturnUser() {
        User user = mock(User.class);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        UserDto userDto = mock(UserDto.class);
        when(userDto.getId()).thenReturn(USER_ID);
        when(userMapper.toDto(user)).thenReturn(userDto);

        ResponseDto result = userService.getUser(USER_ID);

        assertThat(result.getData().getId()).isEqualTo(USER_ID);
    }

    @Test
    void testGetUser_WhenUserDoesNotExist_ThenThrowResourceNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> userService.getUser(USER_ID))
                .withMessage(String.format(ExceptionMessage.USER_NOT_FOUND, USER_ID));
    }

    @Test
    void testUpdateUser_WhenUserExists_ThenUpdateUser() {
        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto("newEmail123@google.com", "John", "Doe",
                LocalDate.of(1990, 2, 13), "some address", "380999999999");

        User user = mock(User.class);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.updateEntity(user, updateRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        UserDto userDto = mock(UserDto.class);
        when(userDto.getId()).thenReturn(USER_ID);
        when(userDto.getEmail()).thenReturn(updateRequest.getEmail());
        when(userMapper.toDto(user)).thenReturn(userDto);

        ResponseDto result = userService.updateUser(USER_ID, updateRequest);

        assertThat(result.getData().getId()).isEqualTo(USER_ID);
        assertThat(result.getData().getEmail()).isEqualTo(updateRequest.getEmail());
    }

    @Test
    void testDeleteUser_WhenUserExists_ThenDeleteUser() {
        User user = mock(User.class);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.deleteUser(USER_ID);

        verify(userRepository).delete(user);
    }

    @Test
    void testDeleteUser_WhenUserDoesNotExist_ThenThrowResourceNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> userService.deleteUser(USER_ID))
                .withMessage(String.format(ExceptionMessage.USER_NOT_FOUND, USER_ID));
    }

    @Test
    void testFindUsersByBirthDateRange_WhenValidRange_ThenReturnUsers() {
        LocalDate from = LocalDate.of(1990, 1, 1);
        LocalDate to = LocalDate.of(1995, 12, 31);

        List<User> users = List.of(mock(User.class), mock(User.class), mock(User.class));
        when(userRepository.findByBirthDateBetween(from, to)).thenReturn(users);

        UserDto userDto1 = mock(UserDto.class);
        when(userDto1.getBirthDate()).thenReturn(LocalDate.of(1991, 1, 1));
        UserDto userDto2 = mock(UserDto.class);
        when(userDto2.getBirthDate()).thenReturn(LocalDate.of(1992, 1, 1));
        UserDto userDto3 = mock(UserDto.class);
        when(userDto3.getBirthDate()).thenReturn(LocalDate.of(1993, 1, 1));

        when(userMapper.toDto(users.getFirst())).thenReturn(userDto1);
        when(userMapper.toDto(users.get(1))).thenReturn(userDto2);
        when(userMapper.toDto(users.getLast())).thenReturn(userDto3);

        MultipleResponseDto result = userService.findUsersByBirthDateRange(from, to);

        assertThat(result.getData()).extracting(UserDto::getBirthDate)
                .containsExactlyInAnyOrder(
                        LocalDate.of(1991, 1, 1),
                        LocalDate.of(1992, 1, 1),
                        LocalDate.of(1993, 1, 1));
    }

    @Test
    void testFindUsersByBirthDateRange_WhenGivenInvalidRange_ThenThrowIllegalArgumentException() {
        LocalDate from = LocalDate.of(1995, 12, 31);
        LocalDate to = LocalDate.of(1990, 1, 1);

        doThrow(new IllegalArgumentException(ExceptionMessage.INVALID_DATE_RANGE))
                .when(userValidator).validateBirthDateRange(from, to);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> userService.findUsersByBirthDateRange(from, to))
                .withMessage(ExceptionMessage.INVALID_DATE_RANGE);
    }

}
