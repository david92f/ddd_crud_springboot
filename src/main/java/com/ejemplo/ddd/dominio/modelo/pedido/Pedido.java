package com.ejemplo.ddd.dominio.modelo.pedido;

import com.ejemplo.ddd.dominio.modelo.producto.IdentificadorProducto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Currency;
import java.util.Optional;
import java.math.BigDecimal;


/**
 * Entidad Raíz del Agregado Pedido.
 * Encapsula la lógica de negocio y las invariantes para un pedido.
 */
public class Pedido {
    private final IdentificadorPedido id;
    private final String idCliente; // Suponemos un ID de cliente simple (String)
    private Direccion direccionEnvio; // Mutable a través de un método específico
    private final List<LineaPedido> lineasPedido;
    private Dinero totalPedido;
    private EstadoPedido estado;
    private final LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimaModificacion;

    // Constructor privado para forzar la creación a través de métodos factoría o servicios de dominio
    private Pedido(IdentificadorPedido id, String idCliente, Direccion direccionEnvio, Currency monedaPorDefecto) {
        Objects.requireNonNull(id, "El ID del pedido no puede ser nulo");
        Objects.requireNonNull(idCliente, "El ID del cliente no puede ser nulo");
        if (idCliente.isBlank()) throw new IllegalArgumentException("El ID del cliente no puede estar vacío");
        Objects.requireNonNull(direccionEnvio, "La dirección de envío no puede ser nula");
        Objects.requireNonNull(monedaPorDefecto, "La moneda por defecto no puede ser nula");

        this.id = id;
        this.idCliente = idCliente;
        this.direccionEnvio = direccionEnvio;
        this.lineasPedido = new ArrayList<>();
        this.estado = EstadoPedido.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaUltimaModificacion = LocalDateTime.now();
        this.totalPedido = new Dinero(BigDecimal.ZERO, monedaPorDefecto);
    }

    /**
     * Método factoría estático para crear un nuevo pedido.
     * @param idCliente El identificador del cliente.
     * @param direccionEnvio La dirección de envío inicial.
     * @param monedaPorDefecto La moneda en la que se expresará el pedido.
     * @return Una nueva instancia de Pedido.
     */
    public static Pedido crearNuevoPedido(String idCliente, Direccion direccionEnvio, Currency monedaPorDefecto) {
        return new Pedido(IdentificadorPedido.nuevo(), idCliente, direccionEnvio, monedaPorDefecto);
    }

    // --- Métodos de Comportamiento del Agregado (Operaciones de Actualización) ---

    public void agregarLineaPedido(IdentificadorProducto idProducto, int cantidad, Dinero precioUnitario) {
        Objects.requireNonNull(idProducto, "El ID del producto no puede ser nulo para la nueva línea");
        Objects.requireNonNull(precioUnitario, "El precio unitario no puede ser nulo para la nueva línea");

        if (this.estado != EstadoPedido.PENDIENTE && this.estado != EstadoPedido.PROCESANDO) { // Permitir agregar si está procesando también
            throw new IllegalStateException("No se pueden agregar líneas a un pedido en estado: " + this.estado);
        }
        if (!this.totalPedido.moneda().equals(precioUnitario.moneda())) {
            throw new IllegalArgumentException("La moneda de la nueva línea (" + precioUnitario.moneda() +
                                               ") no coincide con la moneda del pedido (" + this.totalPedido.moneda() + ").");
        }

        Optional<LineaPedido> lineaExistente = encontrarLineaPorProducto(idProducto);
        if (lineaExistente.isPresent()) {
            // Podríamos decidir actualizar la cantidad o lanzar error. Aquí actualizamos.
            LineaPedido lp = lineaExistente.get();
            lp.actualizarCantidad(lp.getCantidad() + cantidad);
        } else {
            this.lineasPedido.add(new LineaPedido(idProducto, cantidad, precioUnitario));
        }

        recalcularTotal();
        marcarModificado();
    }

    public void eliminarLineaPedido(IdentificadorProducto idProducto) {
        Objects.requireNonNull(idProducto, "El ID del producto a eliminar no puede ser nulo");
        if (this.estado != EstadoPedido.PENDIENTE && this.estado != EstadoPedido.PROCESANDO) {
            throw new IllegalStateException("No se pueden eliminar líneas de un pedido en estado: " + this.estado);
        }
        boolean removed = this.lineasPedido.removeIf(lp -> lp.getIdProducto().equals(idProducto));
        if (removed) {
            recalcularTotal();
            marcarModificado();
        } else {
            throw new IllegalArgumentException("No se encontró la línea de pedido para el producto: " + idProducto.valor());
        }
    }

    public void actualizarCantidadLineaPedido(IdentificadorProducto idProducto, int nuevaCantidad) {
        Objects.requireNonNull(idProducto, "El ID del producto para actualizar cantidad no puede ser nulo");
        if (this.estado != EstadoPedido.PENDIENTE && this.estado != EstadoPedido.PROCESANDO) {
            throw new IllegalStateException("No se puede actualizar la cantidad de líneas en un pedido en estado: " + this.estado);
        }
        LineaPedido linea = encontrarLineaPorProducto(idProducto)
            .orElseThrow(() -> new IllegalArgumentException("No se encontró la línea de pedido para el producto: " + idProducto.valor()));
        
        linea.actualizarCantidad(nuevaCantidad);
        recalcularTotal();
        marcarModificado();
    }
    
    public void actualizarDireccionEnvio(Direccion nuevaDireccion) {
        Objects.requireNonNull(nuevaDireccion, "La nueva dirección de envío no puede ser nula");
        if (this.estado == EstadoPedido.ENVIADO || this.estado == EstadoPedido.ENTREGADO || this.estado == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("No se puede cambiar la dirección de un pedido que ya fue enviado, entregado o cancelado. Estado actual: " + this.estado);
        }
        this.direccionEnvio = nuevaDireccion;
        marcarModificado();
    }

    public void confirmarPedido() {
        if (this.estado != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden confirmar pedidos pendientes. Estado actual: " + this.estado);
        }
        if (this.lineasPedido.isEmpty()) {
            throw new IllegalStateException("No se puede confirmar un pedido vacío.");
        }
        this.estado = EstadoPedido.PROCESANDO;
        marcarModificado();
        // Aquí se podría publicar un Evento de Dominio: PedidoConfirmadoEvent
    }

    public void marcarComoEnviado() {
        if (this.estado != EstadoPedido.PROCESANDO) {
            throw new IllegalStateException("Solo se pueden marcar como enviados los pedidos en procesamiento. Estado actual: " + this.estado);
        }
        this.estado = EstadoPedido.ENVIADO;
        marcarModificado();
        // Evento: PedidoEnviadoEvent
    }

    public void marcarComoEntregado() {
        if (this.estado != EstadoPedido.ENVIADO) {
            throw new IllegalStateException("Solo se pueden marcar como entregados los pedidos enviados. Estado actual: " + this.estado);
        }
        this.estado = EstadoPedido.ENTREGADO;
        marcarModificado();
        // Evento: PedidoEntregadoEvent
    }

    public void cancelarPedido(String motivo) { // Motivo podría ser un VO
        Objects.requireNonNull(motivo, "El motivo de cancelación no puede ser nulo");
        if (motivo.isBlank()) throw new IllegalArgumentException("El motivo de cancelación no puede estar vacío");

        if (this.estado == EstadoPedido.ENTREGADO) {
            throw new IllegalStateException("No se puede cancelar un pedido que ya ha sido entregado.");
        }
        if (this.estado == EstadoPedido.CANCELADO) {
            // Ya está cancelado, no hacer nada o lanzar advertencia
            return;
        }
        this.estado = EstadoPedido.CANCELADO;
        marcarModificado();
        // Aquí se podría publicar un Evento de Dominio: PedidoCanceladoEvent(motivo)
    }

    // --- Métodos de Ayuda Internos ---

    private Optional<LineaPedido> encontrarLineaPorProducto(IdentificadorProducto idProducto) {
        return this.lineasPedido.stream()
            .filter(lp -> lp.getIdProducto().equals(idProducto))
            .findFirst();
    }

    private void recalcularTotal() {
        if (lineasPedido.isEmpty()) {
            this.totalPedido = new Dinero(BigDecimal.ZERO, this.totalPedido.moneda());
            return;
        }
        // Asegura que la moneda base del total sea la del pedido
        Currency monedaBase = this.totalPedido.moneda();

        this.totalPedido = lineasPedido.stream()
            .map(LineaPedido::calcularSubtotal)
            .peek(subtotal -> {
                if (!subtotal.moneda().equals(monedaBase)) {
                    // Esto no debería ocurrir si agregarLineaPedido valida la moneda
                    throw new IllegalStateException("Inconsistencia de monedas al calcular el total del pedido. Pedido: " + monedaBase + ", Línea: " + subtotal.moneda());
                }
            })
            .reduce(new Dinero(BigDecimal.ZERO, monedaBase), Dinero::sumar);
    }

    private void marcarModificado() {
        this.fechaUltimaModificacion = LocalDateTime.now();
    }

    // --- Getters (solo los necesarios para el exterior del Agregado) ---
    public IdentificadorPedido getId() {
        return id;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public Direccion getDireccionEnvio() {
        return direccionEnvio;
    }

    public List<LineaPedido> getLineasPedido() {
        return Collections.unmodifiableList(lineasPedido); // Proteger la colección interna
    }

    public Dinero getTotalPedido() {
        return totalPedido;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaUltimaModificacion() {
        return fechaUltimaModificacion;
    }

    // --- Métodos de Identidad y Estado ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return id.equals(pedido.id); // Las entidades se comparan por su ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Pedido{" +
               "id=" + id.valor() +
               ", idCliente='" + idCliente + "'" +
               ", estado=" + estado +
               ", total=" + totalPedido +
               ", lineas=" + lineasPedido.size() +
               '}';
    }
    
    // Constructor para la reconstrucción desde persistencia (ej. JPA)
    // Este constructor debe ser usado con cuidado y generalmente por el framework de persistencia.
    // Se asume que los datos vienen de una fuente confiable (la BD).
    // Nota: Lombok podría generar constructores, pero para DDD es mejor ser explícito.
    public Pedido(IdentificadorPedido id, String idCliente, Direccion direccionEnvio,
                  List<LineaPedido> lineasPedido, Dinero totalPedido, EstadoPedido estado,
                  LocalDateTime fechaCreacion, LocalDateTime fechaUltimaModificacion) {
        this.id = id;
        this.idCliente = idCliente;
        this.direccionEnvio = direccionEnvio;
        this.lineasPedido = new ArrayList<>(lineasPedido); // Copia defensiva
        this.totalPedido = totalPedido;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaUltimaModificacion = fechaUltimaModificacion;
    }
}
