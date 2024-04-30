package com.kiskee.users.model.dto;

import com.kiskee.users.model.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDto {

    private UserDto data;
}
