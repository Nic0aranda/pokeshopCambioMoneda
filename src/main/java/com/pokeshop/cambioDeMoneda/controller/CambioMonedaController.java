package com.pokeshop.cambioDeMoneda.controller;

import com.pokeshop.cambioDeMoneda.service.CambioMonedaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/moneda")
public class CambioMonedaController {

    @Autowired
    private CambioMonedaService service;

    // Endpoint para obtener solo el valor del dólar hoy
    @GetMapping("/dolar")
    public ResponseEntity<Double> getDolarPrice() {
        Double valor = service.obtenerValorDolarActual();
        return ResponseEntity.ok(valor);
    }

    // Endpoint para convertir un precio específico (Ej: /convertir?amount=19.90)
    @GetMapping("/convertir")
    public ResponseEntity<Double> convertUsdToClp(@RequestParam Double amount) {
        Double conversion = service.convertirUsdAClp(amount);
        return ResponseEntity.ok(conversion);
    }
}
