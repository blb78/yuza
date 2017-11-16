package com.skillogs.yuza.net.http;


import com.skillogs.yuza.domain.User;
import org.mapstruct.Mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;


@Mapper(componentModel = "spring", uses = {})
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );

    UserDto userToUserDto(User user);

}
