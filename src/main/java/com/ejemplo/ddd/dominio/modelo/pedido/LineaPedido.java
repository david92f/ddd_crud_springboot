package com.ejemplo.ddd.dominio.modelo.pedido;

import com.ejemplo.ddd.dominio.modelo.producto.IdentificadorProducto;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entidad que representa un artículo dentro de un Pedido.
 * Forma parte del Agregado Pedido. Su ciclo de vida está ligado al Pedido.
 */
public class LineaPedido {
    // Identificador de la línea dentro del contexto del pedido (generalmente el producto)
    private final IdentificadorProducto idProducto;
    private int cantidad;
    private final Dinero precioUnitario; // Precio en el momento de la compra, inmutable para esta línea

    public LineaPedido(IdentificadorProducto idProducto, int cantidad, Dinero precioUnitario) {
        Objects.requireNonNull(idProducto, "El ID del producto no puede ser nulo");
        Objects.requireNonNull(precioUnitario, "El precio unitario no puede ser nulo");
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public IdentificadorProducto getIdProducto() {
        return idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public Dinero getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Calcula el subtotal para esta línea de pedido.
     * @return El Dinero que representa el subtotal.
     */
    public Dinero calcularSubtotal() {
        return precioUnitario.multiplicar(BigDecimal.valueOf(cantidad));
    }

    /**
     * Actualiza la cantidad de esta línea de pedido.
     * Solo debería ser llamado desde el Agregado Pedido para mantener la consistencia.
     * @param nuevaCantidad La nueva cantidad, debe ser positiva.
     */
    protected void actualizarCantidad(int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La nueva cantidad debe ser positiva");
        }
        this.cantidad = nuevaCantidad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineaPedido that = (LineaPedido) o;
        // Una línea de pedido se identifica unívocamente por el producto dentro del pedido.
        return idProducto.equals(that.idProducto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProducto);
    }

    @Override
    public String toString() {
        return "LineaPedido{" +
               "idProducto=" + idProducto.valor() +
               ", cantidad=" + cantidad +
               ", precioUnitario=" + precioUnitario +
               '}';
    }
}
