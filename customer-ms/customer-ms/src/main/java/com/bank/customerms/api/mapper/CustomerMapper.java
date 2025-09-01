package com.bank.customerms.api.mapper;

import com.bank.customerms.api.dto.CustomerDtos.CustomerDto;
import com.bank.customerms.domain.Customer;

public final class CustomerMapper {
    private CustomerMapper(){}

    public static CustomerDto toDto(Customer c) {
        return new CustomerDto(
                c.getId(), c.getFirstName(), c.getLastName(), c.getDni(), c.getEmail()
        );
    }
}
