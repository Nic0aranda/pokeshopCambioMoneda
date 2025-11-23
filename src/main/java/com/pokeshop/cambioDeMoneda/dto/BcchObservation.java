package com.pokeshop.cambioDeMoneda.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BcchObservation {
    @JsonProperty("indexDateString")
    private String indexDateString; // Fecha
    
    @JsonProperty("value")
    private String value; // Valor (viene como String, ej: "850.5")
    
    @JsonProperty("statusCode")
    private String statusCode;
}