package org.example.malaquita.config;

import lombok.RequiredArgsConstructor;
import org.example.malaquita.service.CarritoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class CarritoAbandonadoScheduler {

    private final CarritoService carritoService;

    @Value("${app.carrito.abandonado-dias:7}")
    private int abandonadoDias;

    @Scheduled(cron = "0 0 3 * * *")
    public void marcarAbandonados() {
        carritoService.marcarAbandonados(abandonadoDias);
    }
}