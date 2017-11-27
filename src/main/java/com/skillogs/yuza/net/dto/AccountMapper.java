package com.skillogs.yuza.net.dto;


import com.skillogs.yuza.domain.account.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "password", ignore = true)
    AccountDto toDTO(Account account);

    AccountDto toDTOWithPassword(Account account);

    Account to(AccountDto u);
}
