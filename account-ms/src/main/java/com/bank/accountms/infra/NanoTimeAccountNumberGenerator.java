package com.bank.accountms.infra;

import com.bank.accountms.domain.service.AccountNumberGenerator;
import org.springframework.stereotype.Component;

/** Implementación simple; en prod podrías usar secuencia DB/UUID. */
@Component
public class NanoTimeAccountNumberGenerator implements AccountNumberGenerator {
    @Override public String next() { return "ACC" + System.nanoTime(); }
}
