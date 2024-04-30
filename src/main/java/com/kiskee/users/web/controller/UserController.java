package com.kiskee.users.web.controller;

import com.kiskee.users.model.dto.MultipleResponseDto;
import com.kiskee.users.model.dto.ResponseDto;
import com.kiskee.users.model.dto.user.UserCreateRequest;
import com.kiskee.users.model.dto.user.UserPartialUpdateRequestDto;
import com.kiskee.users.model.dto.user.UserUpdateRequestDto;
import com.kiskee.users.service.user.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto createUser(@RequestBody @Valid UserCreateRequest registrationRequest) {
        return userService.createUser(registrationRequest);
    }

    @GetMapping("/{userId}")
    public ResponseDto getUser(@PathVariable UUID userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    public ResponseDto updateUser(@PathVariable UUID userId, @RequestBody @Valid UserUpdateRequestDto updateRequest) {
        return userService.updateUser(userId, updateRequest);
    }

    @PatchMapping("/{userId}")
    public ResponseDto partialUpdateUserPatch(@PathVariable UUID userId,
                                              @RequestBody @Valid UserPartialUpdateRequestDto updateRequest) {
        return userService.updateUser(userId, updateRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
    }

    @GetMapping
    public MultipleResponseDto findUsersByBirthDateRange(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        return userService.findUsersByBirthDateRange(from, to);
    }
}
