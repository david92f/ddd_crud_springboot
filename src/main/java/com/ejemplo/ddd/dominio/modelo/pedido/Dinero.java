package com.ejemplo.ddd.dominio.modelo.pedido;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Objeto de Valor para representar una cantidad monetaria.
 * Es inmutable.
 */
public record Dinero(BigDecimal cantidad, Currency moneda) {
    public Dinero {
        Objects.requireNonNull(cantidad, "La cantidad no puede ser nula");
        Objects.requireNonNull(moneda, "La moneda no puede ser nula");
        if (cantidad.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
    }

    public Dinero sumar(Dinero otro) {
        if (!this.moneda.equals(otro.moneda)) {
            throw new IllegalArgumentException("No se pueden sumar cantidades con diferentes monedas. Actual: " + this.moneda + ", Otro: " + otro.moneda());
        }
        return new Dinero(this.cantidad.add(otro.cantidad), this.moneda);
    }

    public Dinero multiplicar(BigDecimal factor) {
        Objects.requireNonNull(factor, "El factor no puede ser nulo");
        return new Dinero(this.cantidad.multiply(factor), this.moneda);
    }
}
