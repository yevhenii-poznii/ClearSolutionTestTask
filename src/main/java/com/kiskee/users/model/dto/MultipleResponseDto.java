package com.kiskee.users.model.dto;

import com.kiskee.users.model.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MultipleResponseDto {

    List<UserDto> data;
}
