package com.bank.domain;

//se definen dos constantes para cuenta ahorro y cuenta corriente savings cuenta de ahorros y checking cuenta corriente
/** Tipos de cuenta soportados por el sistema. */
public enum AccountType { SAVINGS, CHECKING }
// SAVINGS (Ahorros: no puede quedar en negativo)
// CHECKING (Corriente: permite sobregiro hasta -500.00)
// Es un enum para controlar reglas distintas por tipo de cuenta