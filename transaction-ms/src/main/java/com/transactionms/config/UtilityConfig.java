package com.transactionms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Configuración para beans de utilidad.
 * Permite inyectar Clock para mejorar testabilidad de fechas/tiempo.
 */
@Configuration
public class UtilityConfig {

    /**
     * Bean Clock para uso en la aplicación.
     * Puede ser fácilmente mockeado en tests.
     * @return Clock del sistema en UTC
     */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}