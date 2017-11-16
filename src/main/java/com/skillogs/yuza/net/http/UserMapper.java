package com.skillogs.yuza.net.http;


import com.skillogs.yuza.domain.User;
import org.mapstruct.Mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;


@Mapper(componentModel = "spring")
public interface UserMapper {


    UserDto toDTO(User user);

}
