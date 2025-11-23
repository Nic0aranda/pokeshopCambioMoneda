package com.pokeshop.cambioDeMoneda.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "tasa_cambio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TasaCambio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreMoneda; // Ej: "Dolar Observado"
    
    private String codigoMoneda; // Ej: "USD"

    private Double valorEnClp; // El valor del dólar ese día (ej: 980.5)

    private LocalDate fecha; // Fecha del valor
}
