package com.realestate.backend.mappers;

import com.realestate.backend.dtos.requests.UserRequestDTO;
import com.realestate.backend.dtos.responses.UserResponseDTO;
import com.realestate.backend.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    void updateUserFromRequest(
            UserRequestDTO userRequestDTO,
            @MappingTarget User user
    );

    // REGISTER
    @Mapping(source = "phone", target = "phone")
    User userFromRequest(UserRequestDTO dto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "email", target = "email")


    UserResponseDTO toResponseDTO(User user);




    // PROFILE / ME
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "subscriptionPlan", target = "subscriptionPlan")
    UserResponseDTO responseFromUser(User user);


}
