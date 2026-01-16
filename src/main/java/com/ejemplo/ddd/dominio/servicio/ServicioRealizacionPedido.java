package com.ejemplo.ddd.dominio.servicio;

import com.ejemplo.ddd.dominio.modelo.pedido.Direccion;
import com.ejemplo.ddd.dominio.modelo.pedido.Pedido;
import com.ejemplo.ddd.dominio.modelo.producto.IdentificadorProducto;
import com.ejemplo.ddd.dominio.modelo.pedido.Dinero;
import com.ejemplo.ddd.dominio.repositorio.PedidoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Para asegurar atomicidad si se usa BD

import java.util.List;
import java.util.Objects;
import java.util.Currency; // Importar Currency

/**
 * Servicio de Dominio para orquestar la creación de un Pedido.
 * Puede contener lógica que no pertenece naturalmente al Agregado Pedido,
 * como la coordinación con otros Agregados (ej. Productos para verificar stock).
 */
@Service
public class ServicioRealizacionPedido {

    private final PedidoRepository pedidoRepository;

    public ServicioRealizacionPedido(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    /**
     * Crea y persiste un nuevo pedido.
     * @param idCliente Identificador del cliente.
     * @param direccionEnvio Dirección de envío.
     * @param lineas Información de las líneas de pedido (idProducto, cantidad, precio).
     * @param moneda La moneda del pedido.
     * @return El Pedido creado.
     * @throws IllegalArgumentException si los datos de entrada son inválidos.
     */
    @Transactional // Importante si se interactúa con una base de datos transaccional
    public Pedido realizarPedido(String idCliente, Direccion direccionEnvio, List<InfoLineaPedido> lineas, Currency moneda) {
        Objects.requireNonNull(idCliente, "El ID del cliente no puede ser nulo");
        Objects.requireNonNull(direccionEnvio, "La dirección de envío no puede ser nula");
        Objects.requireNonNull(lineas, "Las líneas de pedido no pueden ser nulas");
        Objects.requireNonNull(moneda, "La moneda del pedido no puede ser nula");
        if (lineas.isEmpty()) {
            throw new IllegalArgumentException("Un pedido debe tener al menos una línea");
        }

        // 1. Crear el objeto Pedido (Raíz del Agregado)
        Pedido nuevoPedido = Pedido.crearNuevoPedido(idCliente, direccionEnvio, moneda);

        // 2. Lógica de dominio que podría involucrar a otros agregados (ej. verificar stock)
        // En este ejemplo la verificación de stock y coordinación con otros agregados
        // queda fuera del servicio por simplicidad. Si se necesitase, se inyectaría
        // el repositorio correspondiente y se implementaría la lógica aquí.

        // 3. Agregar las líneas al pedido. Las validaciones y el recálculo del total
        // están encapsulados dentro del Agregado Pedido.
        for (InfoLineaPedido infoLinea : lineas) {
            // Asumimos que el precio viene dado y es en la moneda del pedido.
            // En un sistema real, el precio podría obtenerse del catálogo de productos.
            Dinero precio = new Dinero(infoLinea.precioUnitario(), moneda);
            nuevoPedido.agregarLineaPedido(infoLinea.idProducto(), infoLinea.cantidad(), precio);
        }
        // El total se recalcula dentro de agregarLineaPedido.

        // 4. Guardar el Agregado a través del Repositorio
        pedidoRepository.guardar(nuevoPedido);

        return nuevoPedido;
    }

    /**
     * DTO inmutable que transmite la información necesaria para añadir una línea al pedido.
     * Está intencionadamente vacío de comportamiento (solo datos), por eso no contiene métodos.
     */
    public record InfoLineaPedido(IdentificadorProducto idProducto, int cantidad, java.math.BigDecimal precioUnitario) {
        // Record usado como DTO inmutable; sin lógica adicional.
    }
}
