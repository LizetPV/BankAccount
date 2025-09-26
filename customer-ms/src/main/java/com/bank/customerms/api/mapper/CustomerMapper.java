package com.bank.customerms.api.mapper;

import com.bank.customerms.api.dto.CustomerDtos.CustomerDto;
import com.bank.customerms.domain.Customer;

/**
 * Mapper para convertir entidades {@link Customer} en DTOs.
 */
public final class CustomerMapper {

  private CustomerMapper() {
  }

  /**
   * Convierte un {@link Customer} en un {@link CustomerDto}.
   *
   * @param c entidad del cliente
   * @return DTO con los datos del cliente
   */
  public static CustomerDto toDto(Customer c) {
    return new CustomerDto(
        c.getId(), c.getFirstName(), c.getLastName(), c.getDni(), c.getEmail()
    );
  }
}
