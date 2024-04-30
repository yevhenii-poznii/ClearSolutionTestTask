package com.kiskee.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiskee.users.exception.DuplicateResourceException;
import com.kiskee.users.exception.ResourceNotFoundException;
import com.kiskee.users.model.dto.MultipleResponseDto;
import com.kiskee.users.model.dto.ResponseDto;
import com.kiskee.users.model.dto.user.UserCreateRequest;
import com.kiskee.users.model.dto.user.UserDto;
import com.kiskee.users.model.dto.user.UserPartialUpdateRequestDto;
import com.kiskee.users.model.dto.user.UserUpdateRequestDto;
import com.kiskee.users.service.user.UserService;
import com.kiskee.users.util.ExceptionMessage;
import com.kiskee.users.web.controller.UserController;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    private static final UUID USER_ID = UUID.fromString("e85bcd9a-00ed-45d3-87c9-ac0c8ad68203");

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("validUserCreateRequest")
    void testCreateUser_WhenProvidedValidBody_ThenCreateNewUserAndReturnCreatedStatus(UserCreateRequest createRequest) {
        UserDto createdUser = new UserDto(USER_ID, createRequest.getEmail(), createRequest.getFirstName(), createRequest.getLastName(),
                createRequest.getBirthDate(), createRequest.getAddress(), createRequest.getPhoneNumber());
        ResponseDto responseDto = new ResponseDto(createdUser);
        when(userService.createUser(createRequest)).thenReturn(responseDto);

        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(objectMapper.writeValueAsString(responseDto));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("invalidUserCreateRequest")
    void testCreateUser_WhenProvidedInvalidBody_ThenReturnBadRequest(UserCreateRequest createRequest) {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void testCreateUser_WhenUserWithTheSameEmailAlreadyExists_ThenReturnConflictStatus() {
        UserCreateRequest createRequest = new UserCreateRequest("email@google.com", "John", "Doe",
                LocalDate.of(1990, 2, 13), "some address", "+380999999999");

        when(userService.createUser(createRequest))
                .thenThrow(new DuplicateResourceException(createRequest.getEmail()));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpectAll(status().isConflict(),
                        jsonPath("$.errors.error").value(String.format(
                                ExceptionMessage.USER_ALREADY_EXISTS, createRequest.getEmail()
                        )));
    }

    @Test
    @SneakyThrows
    void testGetUser_WhenUserExists_ThenReturnUser() {
        UserDto user = new UserDto(USER_ID, "email@google.com", "John", "Doe",
                LocalDate.of(1998, 2, 13), "some address", "380999999999");
        ResponseDto responseDto = new ResponseDto(user);

        when(userService.getUser(USER_ID)).thenReturn(responseDto);

        MvcResult mvcResult = mockMvc.perform(get("/users/{userId}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(objectMapper.writeValueAsString(responseDto));
    }

    @Test
    @SneakyThrows
    void testGetUser_WhenUserDoesNotExist_ThenReturnNotFoundStatus() {
        when(userService.getUser(USER_ID))
                .thenThrow(new ResourceNotFoundException(USER_ID));

        mockMvc.perform(get("/users/{userId}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isNotFound(),
                        jsonPath("$.errors.error").value(String.format(
                                ExceptionMessage.USER_NOT_FOUND, USER_ID
                        )));
    }

    @Test
    @SneakyThrows
    void testUpdateUser_WhenProvidedValidBody_ThenReturnUpdatedUser() {
        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto("email@google.com", "John", "Doe",
                LocalDate.of(1998, 2, 13), "some address", "380999999999");

        when(userService.updateUser(USER_ID, updateRequest)).thenReturn(mock(ResponseDto.class));

        mockMvc.perform(put("/users/{userId}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void testUpdateUser_WhenUserDoesNotExist_ThenReturnNotFoundStatus() {
        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto("email@google.com", "John", "Doe",
                LocalDate.of(1998, 2, 13), "some address", "380999999999");

        when(userService.updateUser(USER_ID, updateRequest)).thenThrow(new ResourceNotFoundException(USER_ID));

        mockMvc.perform(put("/users/{userId}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpectAll(status().isNotFound(),
                        jsonPath("$.errors.error").value(String.format(
                                ExceptionMessage.USER_NOT_FOUND, USER_ID
                        )));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("invalidUserUpdateRequest")
    void testUpdateUser_WhenProvidedInvalidBody_ThenReturnBadRequest(UserUpdateRequestDto updateRequest) {
        mockMvc.perform(put("/users/{userId}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void testPartialUpdateUser_WhenProvidedValidBody_ThenReturnUpdatedUser() {
        UserPartialUpdateRequestDto partialUpdateRequest = new UserPartialUpdateRequestDto("newEmail@google.com",
                null, null, null, null, null);

        when(userService.updateUser(USER_ID, partialUpdateRequest)).thenReturn(mock(ResponseDto.class));

        mockMvc.perform(patch("/users/{userId}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void testPartialUpdateUser_WhenUserDoesNotExist_ThenReturnNotFoundStatus() {
        UserPartialUpdateRequestDto partialUpdateRequest = new UserPartialUpdateRequestDto("newEmail@google.com",
                null, null, null, null, null);

        when(userService.updateUser(USER_ID, partialUpdateRequest)).thenThrow(new ResourceNotFoundException(USER_ID));

        mockMvc.perform(patch("/users/{userId}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdateRequest)))
                .andDo(print())
                .andExpectAll(status().isNotFound(),
                        jsonPath("$.errors.error").value(String.format(
                                ExceptionMessage.USER_NOT_FOUND, USER_ID
                        )));
    }

    @Test
    @SneakyThrows
    void testDeleteUser_WhenUserExists_ThenReturnNoContentStatus() {
        mockMvc.perform(delete("/users/{userId}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void testDeleteUser_WhenUserDoesNotExist_ThenReturnNotFoundStatus() {
        doThrow(new ResourceNotFoundException(USER_ID)).when(userService).deleteUser(USER_ID);

        mockMvc.perform(delete("/users/{userId}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isNotFound(),
                        jsonPath("$.errors.error").value(String.format(
                                ExceptionMessage.USER_NOT_FOUND, USER_ID
                        )));
    }

    @Test
    @SneakyThrows
    void testFindUsersByBirthDateRange_WhenProvidedValidRange_ThenReturnUsers() {
        LocalDate from = LocalDate.of(1990, 1, 1);
        LocalDate to = LocalDate.of(1995, 12, 31);

        UserDto user1 = mock(UserDto.class);
        when(user1.getBirthDate()).thenReturn(LocalDate.of(1991, 1, 1));
        UserDto user2 = mock(UserDto.class);
        when(user2.getBirthDate()).thenReturn(LocalDate.of(1992, 1, 1));
        UserDto user3 = mock(UserDto.class);
        when(user3.getBirthDate()).thenReturn(LocalDate.of(1993, 1, 1));
        List<UserDto> users = List.of(user1, user2, user3);

        MultipleResponseDto response = new MultipleResponseDto(users);
        when(userService.findUsersByBirthDateRange(from, to)).thenReturn(response);

        MvcResult result = mockMvc.perform(get("/users")
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(objectMapper.writeValueAsString(response));
    }

    @Test
    @SneakyThrows
    void testFindUsersByBirthDateRange_WhenProvidedInvalidRange_ThenReturnBadRequest() {
        LocalDate from = LocalDate.of(1995, 1, 1);
        LocalDate to = LocalDate.of(1990, 12, 31);

        when(userService.findUsersByBirthDateRange(from, to))
                .thenThrow(new IllegalArgumentException(ExceptionMessage.INVALID_DATE_RANGE));

        mockMvc.perform(get("/users")
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.errors.error").value(ExceptionMessage.INVALID_DATE_RANGE));
    }

    static Stream<UserCreateRequest> validUserCreateRequest() {
        return Stream.of(
                new UserCreateRequest("email@google.com", "John", "Doe",
                        LocalDate.of(1990, 2, 13), "some address", "380999999999"),
                new UserCreateRequest("email@google.com", "John", "Doe",
                        LocalDate.of(1990, 2, 13), null, null)

        );
    }

    static Stream<UserCreateRequest> invalidUserCreateRequest() {
        return Stream.of(
                new UserCreateRequest(null, "John", "Doe",
                        LocalDate.of(1990, 2, 13), "some address", "380999999999"),
                new UserCreateRequest(null, null, "Doe",
                        LocalDate.of(1990, 2, 13), null, null),
                new UserCreateRequest(null, null, null,
                        LocalDate.of(1990, 2, 13), null, null),
                new UserCreateRequest(null, null, null,
                        null, null, null)
        );
    }

    static Stream<UserUpdateRequestDto> invalidUserUpdateRequest() {
        return Stream.of(
                new UserUpdateRequestDto(null, "John", "Doe",
                        LocalDate.of(1990, 2, 13), "some address", "380999999999"),
                new UserUpdateRequestDto(null, null, "Doe",
                        LocalDate.of(1990, 2, 13), "some address", "380999999999"),
                new UserUpdateRequestDto(null, null, null,
                        LocalDate.of(1990, 2, 13), "some address", "380999999999"),
                new UserUpdateRequestDto(null, null, null, null,
                        "some address", "380999999999"),
                new UserUpdateRequestDto(null, null, null, null, null, "380999999999"),
                new UserUpdateRequestDto(null, null, null, null, null, null),
                new UserUpdateRequestDto(null, "", "", null, null, null));
    }
}
