package com.bank.accountms.contract.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AccountCreateDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.6.0")
public class AccountCreateDto {

  private Long customerId;

  /**
   * Gets or Sets accountType
   */
  public enum AccountTypeEnum {
    SAVINGS("SAVINGS"),
    
    CHECKING("CHECKING");

    private String value;

    AccountTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static AccountTypeEnum fromValue(String value) {
      for (AccountTypeEnum b : AccountTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private AccountTypeEnum accountType;

  private Double initialDeposit;

  public AccountCreateDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AccountCreateDto(Long customerId, AccountTypeEnum accountType, Double initialDeposit) {
    this.customerId = customerId;
    this.accountType = accountType;
    this.initialDeposit = initialDeposit;
  }

  public AccountCreateDto customerId(Long customerId) {
    this.customerId = customerId;
    return this;
  }

  /**
   * Get customerId
   * @return customerId
  */
  @NotNull 
  @Schema(name = "customerId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("customerId")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public AccountCreateDto accountType(AccountTypeEnum accountType) {
    this.accountType = accountType;
    return this;
  }

  /**
   * Get accountType
   * @return accountType
  */
  @NotNull 
  @Schema(name = "accountType", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("accountType")
  public AccountTypeEnum getAccountType() {
    return accountType;
  }

  public void setAccountType(AccountTypeEnum accountType) {
    this.accountType = accountType;
  }

  public AccountCreateDto initialDeposit(Double initialDeposit) {
    this.initialDeposit = initialDeposit;
    return this;
  }

  /**
   * Get initialDeposit
   * minimum: 1
   * @return initialDeposit
  */
  @NotNull @DecimalMin("1") 
  @Schema(name = "initialDeposit", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("initialDeposit")
  public Double getInitialDeposit() {
    return initialDeposit;
  }

  public void setInitialDeposit(Double initialDeposit) {
    this.initialDeposit = initialDeposit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccountCreateDto accountCreateDto = (AccountCreateDto) o;
    return Objects.equals(this.customerId, accountCreateDto.customerId) &&
        Objects.equals(this.accountType, accountCreateDto.accountType) &&
        Objects.equals(this.initialDeposit, accountCreateDto.initialDeposit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(customerId, accountType, initialDeposit);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccountCreateDto {\n");
    sb.append("    customerId: ").append(toIndentedString(customerId)).append("\n");
    sb.append("    accountType: ").append(toIndentedString(accountType)).append("\n");
    sb.append("    initialDeposit: ").append(toIndentedString(initialDeposit)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

