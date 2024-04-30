package com.kiskee.users.service.user;

import com.kiskee.users.exception.DuplicateResourceException;
import com.kiskee.users.exception.ResourceNotFoundException;
import com.kiskee.users.mapper.UserMapper;
import com.kiskee.users.model.dto.MultipleResponseDto;
import com.kiskee.users.model.dto.ResponseDto;
import com.kiskee.users.model.dto.user.UserCreateRequest;
import com.kiskee.users.model.dto.user.UserDto;
import com.kiskee.users.model.dto.user.UserUpdateRequest;
import com.kiskee.users.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    @Override
    @Transactional
    public ResponseDto createUser(UserCreateRequest createRequest) {
        userValidator.validateRegistrationRequest(createRequest);

        if (userRepository.existsByEmail(createRequest.getEmail())) {
            throw new DuplicateResourceException(createRequest.getEmail());
        }
        return userMapper.toEntityOpt(createRequest)
                .map(userRepository::save)
                .map(userMapper::toDto)
                .map(ResponseDto::new)
                .orElseThrow();
    }

    @Override
    public ResponseDto getUser(UUID userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .map(ResponseDto::new)
                .orElseThrow(() -> new ResourceNotFoundException(userId));
    }

    @Override
    @Transactional
    public ResponseDto updateUser(UUID userId, UserUpdateRequest updateRequest) {
        return userRepository.findById(userId)
                .map(user -> userMapper.updateEntity(user, updateRequest))
                .map(userRepository::save)
                .map(userMapper::toDto)
                .map(ResponseDto::new)
                .orElseThrow(() -> new ResourceNotFoundException(userId));
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        userRepository.findById(id)
                .ifPresentOrElse(userRepository::delete,
                        () -> {
                            throw new ResourceNotFoundException(id);
                        });
    }

    @Override
    public MultipleResponseDto findUsersByBirthDateRange(LocalDate from, LocalDate to) {
        userValidator.validateBirthDateRange(from, to);

        List<UserDto> data = userRepository.findByBirthDateBetween(from, to).stream()
                .map(userMapper::toDto)
                .toList();

        return new MultipleResponseDto(data);
    }
}
