package com.pokeshop.cambioDeMoneda.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class BcchSeries {
    @JsonProperty("descripEsp")
    private String descripEsp;
    
    @JsonProperty("seriesId")
    private String seriesId;
    
    @JsonProperty("Obs")
    private List<BcchObservation> obs;
}
