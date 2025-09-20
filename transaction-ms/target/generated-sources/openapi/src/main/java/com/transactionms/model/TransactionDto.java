package com.transactionms.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.util.Arrays;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.NoSuchElementException;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * TransactionDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-09-19T20:17:02.650148800-05:00[America/Lima]", comments = "Generator version: 7.4.0")
public class TransactionDto {

  private String id;

  /**
   * Gets or Sets type
   */
  public enum TypeEnum {
    DEPOSIT("DEPOSIT"),
    
    WITHDRAWAL("WITHDRAWAL"),
    
    TRANSFER("TRANSFER");

    private String value;

    TypeEnum(String value) {
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
    public static TypeEnum fromValue(String value) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private TypeEnum type;

  private Double amount;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime date;

  private JsonNullable<String> originAccountNumber = JsonNullable.<String>undefined();

  private JsonNullable<String> destinationAccountNumber = JsonNullable.<String>undefined();

  public TransactionDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public TransactionDto(String id, TypeEnum type, Double amount, OffsetDateTime date) {
    this.id = id;
    this.type = type;
    this.amount = amount;
    this.date = date;
  }

  public TransactionDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Identificador único de la transacción
   * @return id
  */
  @NotNull 
  @Schema(name = "id", description = "Identificador único de la transacción", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public TransactionDto type(TypeEnum type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
  */
  @NotNull 
  @Schema(name = "type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("type")
  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public TransactionDto amount(Double amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Get amount
   * @return amount
  */
  @NotNull 
  @Schema(name = "amount", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public TransactionDto date(OffsetDateTime date) {
    this.date = date;
    return this;
  }

  /**
   * Get date
   * @return date
  */
  @NotNull @Valid 
  @Schema(name = "date", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("date")
  public OffsetDateTime getDate() {
    return date;
  }

  public void setDate(OffsetDateTime date) {
    this.date = date;
  }

  public TransactionDto originAccountNumber(String originAccountNumber) {
    this.originAccountNumber = JsonNullable.of(originAccountNumber);
    return this;
  }

  /**
   * Cuenta de origen (null para depósitos)
   * @return originAccountNumber
  */
  
  @Schema(name = "originAccountNumber", description = "Cuenta de origen (null para depósitos)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("originAccountNumber")
  public JsonNullable<String> getOriginAccountNumber() {
    return originAccountNumber;
  }

  public void setOriginAccountNumber(JsonNullable<String> originAccountNumber) {
    this.originAccountNumber = originAccountNumber;
  }

  public TransactionDto destinationAccountNumber(String destinationAccountNumber) {
    this.destinationAccountNumber = JsonNullable.of(destinationAccountNumber);
    return this;
  }

  /**
   * Cuenta de destino (null para retiros)
   * @return destinationAccountNumber
  */
  
  @Schema(name = "destinationAccountNumber", description = "Cuenta de destino (null para retiros)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("destinationAccountNumber")
  public JsonNullable<String> getDestinationAccountNumber() {
    return destinationAccountNumber;
  }

  public void setDestinationAccountNumber(JsonNullable<String> destinationAccountNumber) {
    this.destinationAccountNumber = destinationAccountNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TransactionDto transactionDto = (TransactionDto) o;
    return Objects.equals(this.id, transactionDto.id) &&
        Objects.equals(this.type, transactionDto.type) &&
        Objects.equals(this.amount, transactionDto.amount) &&
        Objects.equals(this.date, transactionDto.date) &&
        equalsNullable(this.originAccountNumber, transactionDto.originAccountNumber) &&
        equalsNullable(this.destinationAccountNumber, transactionDto.destinationAccountNumber);
  }

  private static <T> boolean equalsNullable(JsonNullable<T> a, JsonNullable<T> b) {
    return a == b || (a != null && b != null && a.isPresent() && b.isPresent() && Objects.deepEquals(a.get(), b.get()));
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, type, amount, date, hashCodeNullable(originAccountNumber), hashCodeNullable(destinationAccountNumber));
  }

  private static <T> int hashCodeNullable(JsonNullable<T> a) {
    if (a == null) {
      return 1;
    }
    return a.isPresent() ? Arrays.deepHashCode(new Object[]{a.get()}) : 31;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransactionDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    originAccountNumber: ").append(toIndentedString(originAccountNumber)).append("\n");
    sb.append("    destinationAccountNumber: ").append(toIndentedString(destinationAccountNumber)).append("\n");
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

