package com.pokeshop.cambioDeMoneda.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BcchResponse {
    @JsonProperty("Codigo")
    private Integer codigo;
    
    @JsonProperty("Descripcion")
    private String descripcion;
    
    @JsonProperty("Series")
    private BcchSeries series;
}
