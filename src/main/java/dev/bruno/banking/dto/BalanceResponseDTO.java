package dev.bruno.banking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BalanceResponseDTO {
    @JsonProperty("userId")
    private String userId;

    @JsonProperty("balance")
    private Double balance;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("lastUpdate")
    private String lastUpdate;
}
