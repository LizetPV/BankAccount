package com.bank.customerms.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public final class CustomerDtos {

    public record CustomerDto(
            Long id, String firstName, String lastName, String dni, String email
    ) {}

    public record CustomerCreateDto(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotBlank String dni,
            @NotBlank @Email String email
    ) {}

    public record CustomerUpdateDto(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotBlank @Email String email
    ) {}

    private CustomerDtos() {}
}
