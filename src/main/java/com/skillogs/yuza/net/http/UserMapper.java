package com.skillogs.yuza.net.http;

import com.skillogs.yuza.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );


    UserDto userToUserDto(User user);
}
