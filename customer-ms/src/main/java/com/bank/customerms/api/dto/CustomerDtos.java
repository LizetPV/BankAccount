package com.bank.customerms.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Contiene los DTOs relacionados con el cliente.
 */
public final class CustomerDtos {

  /**
   * DTO para mostrar la información de un cliente.
   *
   * @param id identificador del cliente
   * @param firstName nombre del cliente
   * @param lastName apellido del cliente
   * @param dni documento nacional de identidad
   * @param email correo electrónico
   */
  public record CustomerDto(
      Long id, String firstName, String lastName, String dni, String email
  ) {
  }

  /**
   * DTO para la creación de un cliente.
   *
   * @param firstName nombre del cliente
   * @param lastName apellido del cliente
   * @param dni documento nacional de identidad
   * @param email correo electrónico
   */
  public record CustomerCreateDto(
      @NotBlank String firstName,
      @NotBlank String lastName,
      @NotBlank String dni,
      @NotBlank @Email String email
  ) {
  }

  /**
   * DTO para la actualización de un cliente.
   *
   * @param firstName nombre del cliente
   * @param lastName apellido del cliente
   * @param email correo electrónico
   */
  public record CustomerUpdateDto(
      @NotBlank String firstName,
      @NotBlank String lastName,
      @NotBlank @Email String email
  ) {
  }

  private CustomerDtos() {
  }
}
