package com.skillogs.yuza.net.http;


import com.skillogs.yuza.domain.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDTO(User user);

}
