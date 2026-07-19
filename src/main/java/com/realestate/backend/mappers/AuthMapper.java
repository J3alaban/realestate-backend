package com.realestate.backend.mappers;

import com.realestate.backend.dtos.requests.RegisterUserRequestDTO;
import com.realestate.backend.dtos.requests.UserRequestDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    UserRequestDTO addRequestFromRegisterRequest(RegisterUserRequestDTO request);
}
