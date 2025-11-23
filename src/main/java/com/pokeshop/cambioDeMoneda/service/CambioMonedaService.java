package com.pokeshop.cambioDeMoneda.service;

import com.pokeshop.cambioDeMoneda.dto.BcchResponse;
import com.pokeshop.cambioDeMoneda.model.TasaCambio;
import com.pokeshop.cambioDeMoneda.repository.TasaCambioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class CambioMonedaService {

    @Autowired
    private TasaCambioRepository repository;

    @Value("${bcch.api.url}")
    private String apiUrl;

    @Value("${bcch.api.user}")
    private String user;

    @Value("${bcch.api.pass}")
    private String pass;

    @Value("${bcch.api.series.dolar}")
    private String seriesDolar;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Obtiene el valor del dólar.
     * 1. Busca en BD local para el día de hoy.
     * 2. Si no existe, llama a la API externa (recursiva si es fin de semana).
     * 3. Guarda el resultado en BD.
     */
    public Double obtenerValorDolarActual() {
        LocalDate hoy = LocalDate.now();

        // 1. Intentar buscar en base de datos local para hoy
        Optional<TasaCambio> tasaGuardada = repository.findByFechaAndCodigoMoneda(hoy, "USD");
        if (tasaGuardada.isPresent()) {
            return tasaGuardada.get().getValorEnClp();
        }

        // 2. Si no está guardado, buscar afuera
        Double valorExterno = buscarEnApiBancoCentral(hoy);

        // 3. Guardar en BD para futuras consultas de hoy
        if (valorExterno != null) {
            TasaCambio nuevaTasa = new TasaCambio();
            nuevaTasa.setCodigoMoneda("USD");
            nuevaTasa.setNombreMoneda("Dolar Observado");
            nuevaTasa.setValorEnClp(valorExterno);
            nuevaTasa.setFecha(hoy);
            repository.save(nuevaTasa);
        }

        return valorExterno;
    }

    /**
     * Convierte un monto en USD a CLP usando la tasa actual.
     */
    public Double convertirUsdAClp(Double montoEnUsd) {
        Double tasa = obtenerValorDolarActual();
        if (tasa == null) {
            throw new RuntimeException("No se pudo obtener la tasa de cambio.");
        }
        // Redondear a 2 decimales si quieres, aquí va crudo
        return montoEnUsd * tasa;
    }

    // Lógica recursiva para buscar días anteriores si hoy no hay dato (ej: feriado/finde)
    private Double buscarEnApiBancoCentral(LocalDate fecha) {
        // Límite de recursión: si buscamos más de 7 días atrás, paramos para evitar loop infinito
        if (fecha.isBefore(LocalDate.now().minusDays(7))) {
            return null; 
        }

        String fechaStr = fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("user", user)
                .queryParam("pass", pass)
                .queryParam("function", "GetSeries")
                .queryParam("timeseries", seriesDolar)
                .queryParam("firstdate", fechaStr)
                .queryParam("lastdate", fechaStr)
                .toUriString();

        try {
            BcchResponse response = restTemplate.getForObject(url, BcchResponse.class);

            if (response != null && response.getSeries() != null 
                && response.getSeries().getObs() != null 
                && !response.getSeries().getObs().isEmpty()) {
                
                String valStr = response.getSeries().getObs().get(0).getValue();
                return Double.parseDouble(valStr);
            } else {
                // Si no hay dato hoy, probamos ayer
                System.out.println("Sin datos para " + fechaStr + ". Probando día anterior...");
                return buscarEnApiBancoCentral(fecha.minusDays(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Si falla la conexión, intentamos día anterior por si es un error puntual de fecha
            return buscarEnApiBancoCentral(fecha.minusDays(1));
        }
    }

    /**
     * Tarea programada: Se ejecuta todos los lunes a las 00:00 AM
     * Borra todo el historial de la tabla.
     * Cron: Seg Min Hora DiaMes Mes DiaSemana
     */
    @Scheduled(cron = "0 0 0 * * MON")
    public void limpiarTablaSemanalmente() {
        System.out.println("Limpiando tabla de tasas de cambio (Programado: Lunes)...");
        repository.deleteAll();
    }
}