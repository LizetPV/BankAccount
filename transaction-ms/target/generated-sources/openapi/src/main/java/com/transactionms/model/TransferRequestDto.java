package com.transactionms.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * TransferRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-09-19T20:17:02.650148800-05:00[America/Lima]", comments = "Generator version: 7.4.0")
public class TransferRequestDto {

  private String originAccountNumber;

  private String destinationAccountNumber;

  private Double amount;

  public TransferRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public TransferRequestDto(String originAccountNumber, String destinationAccountNumber, Double amount) {
    this.originAccountNumber = originAccountNumber;
    this.destinationAccountNumber = destinationAccountNumber;
    this.amount = amount;
  }

  public TransferRequestDto originAccountNumber(String originAccountNumber) {
    this.originAccountNumber = originAccountNumber;
    return this;
  }

  /**
   * Get originAccountNumber
   * @return originAccountNumber
  */
  @NotNull 
  @Schema(name = "originAccountNumber", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("originAccountNumber")
  public String getOriginAccountNumber() {
    return originAccountNumber;
  }

  public void setOriginAccountNumber(String originAccountNumber) {
    this.originAccountNumber = originAccountNumber;
  }

  public TransferRequestDto destinationAccountNumber(String destinationAccountNumber) {
    this.destinationAccountNumber = destinationAccountNumber;
    return this;
  }

  /**
   * Get destinationAccountNumber
   * @return destinationAccountNumber
  */
  @NotNull 
  @Schema(name = "destinationAccountNumber", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("destinationAccountNumber")
  public String getDestinationAccountNumber() {
    return destinationAccountNumber;
  }

  public void setDestinationAccountNumber(String destinationAccountNumber) {
    this.destinationAccountNumber = destinationAccountNumber;
  }

  public TransferRequestDto amount(Double amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Get amount
   * minimum: 0.01
   * @return amount
  */
  @NotNull @DecimalMin("0.01") 
  @Schema(name = "amount", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TransferRequestDto transferRequestDto = (TransferRequestDto) o;
    return Objects.equals(this.originAccountNumber, transferRequestDto.originAccountNumber) &&
        Objects.equals(this.destinationAccountNumber, transferRequestDto.destinationAccountNumber) &&
        Objects.equals(this.amount, transferRequestDto.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(originAccountNumber, destinationAccountNumber, amount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransferRequestDto {\n");
    sb.append("    originAccountNumber: ").append(toIndentedString(originAccountNumber)).append("\n");
    sb.append("    destinationAccountNumber: ").append(toIndentedString(destinationAccountNumber)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
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

