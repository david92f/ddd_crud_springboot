package com.ejemplo.ddd.dominio.modelo.pedido;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class DineroTest {

    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency USD = Currency.getInstance("USD");

    @Test
    @DisplayName("Should create money with valid amount and currency")
    void shouldCreateMoneyWithValidAmountAndCurrency() {
        BigDecimal amount = new BigDecimal("100.50");
        Dinero dinero = new Dinero(amount, EUR);
        
        assertNotNull(dinero);
        assertEquals(amount, dinero.cantidad());
        assertEquals(EUR, dinero.moneda());
    }

    @Test
    @DisplayName("Should throw exception when amount is null")
    void shouldThrowExceptionWhenAmountIsNull() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new Dinero(null, EUR)
        );
        
        assertTrue(exception.getMessage().contains("La cantidad no puede ser nula"));
    }

    @Test
    @DisplayName("Should throw exception when currency is null")
    void shouldThrowExceptionWhenCurrencyIsNull() {
        BigDecimal amount = new BigDecimal("100.50");
        
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new Dinero(amount, null)
        );
        
        assertTrue(exception.getMessage().contains("La moneda no puede ser nula"));
    }

    @Test
    @DisplayName("Should throw exception when amount is negative")
    void shouldThrowExceptionWhenAmountIsNegative() {
        BigDecimal negativeAmount = new BigDecimal("-100.00");
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Dinero(negativeAmount, EUR)
        );
        
        assertTrue(exception.getMessage().contains("La cantidad no puede ser negativa"));
    }

    @Test
    @DisplayName("Should sum money with same currency")
    void shouldSumMoneyWithSameCurrency() {
        Dinero dinero1 = new Dinero(new BigDecimal("100.00"), EUR);
        Dinero dinero2 = new Dinero(new BigDecimal("50.00"), EUR);
        
        Dinero resultado = dinero1.sumar(dinero2);
        
        assertEquals(new BigDecimal("150.00"), resultado.cantidad());
        assertEquals(EUR, resultado.moneda());
    }

    @Test
    @DisplayName("Should throw exception when summing money with different currencies")
    void shouldThrowExceptionWhenSummingMoneyWithDifferentCurrencies() {
        Dinero dineroEUR = new Dinero(new BigDecimal("100.00"), EUR);
        Dinero dineroUSD = new Dinero(new BigDecimal("100.00"), USD);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> dineroEUR.sumar(dineroUSD)
        );
        
        assertTrue(exception.getMessage().contains("No se pueden sumar cantidades con diferentes monedas"));
    }

    @Test
    @DisplayName("Should multiply money by factor")
    void shouldMultiplyMoneyByFactor() {
        Dinero dinero = new Dinero(new BigDecimal("100.00"), EUR);
        BigDecimal factor = new BigDecimal("2.5");
        
        Dinero resultado = dinero.multiplicar(factor);
        
        assertEquals(0, new BigDecimal("250.00").compareTo(resultado.cantidad()));
        assertEquals(EUR, resultado.moneda());
    }

    @Test
    @DisplayName("Should throw exception when multiplying by null factor")
    void shouldThrowExceptionWhenMultiplyingByNullFactor() {
        Dinero dinero = new Dinero(new BigDecimal("100.00"), EUR);
        
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> dinero.multiplicar(null)
        );
        
        assertTrue(exception.getMessage().contains("El factor no puede ser nulo"));
    }

    @Test
    @DisplayName("Should handle zero amount")
    void shouldHandleZeroAmount() {
        Dinero dineroCero = new Dinero(BigDecimal.ZERO, EUR);
        
        assertEquals(BigDecimal.ZERO, dineroCero.cantidad());
        assertEquals(EUR, dineroCero.moneda());
        
        // Should be able to sum with zero
        Dinero otroDinero = new Dinero(new BigDecimal("100.00"), EUR);
        Dinero resultado = dineroCero.sumar(otroDinero);
        assertEquals(new BigDecimal("100.00"), resultado.cantidad());
    }

    @Test
    @DisplayName("Should maintain precision in operations")
    void shouldMaintainPrecisionInOperations() {
        Dinero dinero1 = new Dinero(new BigDecimal("100.123"), EUR);
        Dinero dinero2 = new Dinero(new BigDecimal("200.456"), EUR);
        
        Dinero resultado = dinero1.sumar(dinero2);
        assertEquals(0, new BigDecimal("300.579").compareTo(resultado.cantidad()));
    }
}