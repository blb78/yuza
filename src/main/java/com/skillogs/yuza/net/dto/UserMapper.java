package com.skillogs.yuza.net.dto;


import com.skillogs.yuza.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    UserDto toDTO(User user);

    UserDto toDTOWithPassword(User user);

    User to(UserDto u);
}
