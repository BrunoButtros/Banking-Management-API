package dev.bruno.banking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeConversionResponseDTO {
    @JsonProperty("success")
    private boolean success;

    @JsonProperty("query")
    private Query query;

    @JsonProperty("info")
    private Info info;

    @JsonProperty("result")
    private Double result;

    @Getter
    @Setter
    public static class Query {
        @JsonProperty("from")
        private String from;

        @JsonProperty("to")
        private String to;

        @JsonProperty("amount")
        private Double amount;
    }

    @Getter
    @Setter
    public static class Info {
        @JsonProperty("quote")
        private Double rate;
    }
}
