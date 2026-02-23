package com.ejemplo.ddd.infraestructura.persistencia;

import com.ejemplo.ddd.dominio.modelo.pedido.IdentificadorPedido;
import com.ejemplo.ddd.dominio.modelo.pedido.Pedido;
import com.ejemplo.ddd.dominio.repositorio.PedidoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación en memoria del Repositorio de Pedidos para fines de demostración.
 * En una aplicación real, esto usaría JPA, JDBC, u otra tecnología de persistencia.
 */
@Repository
public class PedidoRepositoryImpl implements PedidoRepository {

    private static final Logger logger = LoggerFactory.getLogger(PedidoRepositoryImpl.class);

    private final Map<IdentificadorPedido, Pedido> almacenDePedidos = new ConcurrentHashMap<>();

    @Override
    public void guardar(Pedido pedido) {
        // Para simular el comportamiento de un ORM que podría devolver una instancia diferente
        // o para asegurar que no se modifica el objeto original fuera del repositorio,
        // guardamos una "copia". En un sistema real con JPA, el EntityManager se encarga de esto.
        // Esta clonación es MUY simplificada.
        Pedido copiaParaAlmacen = clonarPedido(pedido);
        almacenDePedidos.put(pedido.getId(), copiaParaAlmacen);
        logger.info("Pedido guardado/actualizado en memoria: {}", pedido.getId().valor());
    }

    @Override
    public Optional<Pedido> buscarPorId(IdentificadorPedido id) {
        Pedido pedidoAlmacenado = almacenDePedidos.get(id);
        // Devolver una copia para simular que se obtiene una entidad "desapegada"
        return Optional.ofNullable(pedidoAlmacenado != null ? clonarPedido(pedidoAlmacenado) : null);
    }

    @Override
    public List<Pedido> buscarTodos() {
        return almacenDePedidos.values().stream()
                               .map(this::clonarPedido)
                               .toList();
    }

    @Override
    public void eliminarPorId(IdentificadorPedido id) {
        Pedido removido = almacenDePedidos.remove(id);
        if (removido != null) {
            logger.info("Pedido eliminado de memoria: {}", id.valor());
        } else {
            logger.warn("Intento de eliminar pedido no existente en memoria: {}", id.valor());
        }
    }

    /**
     * Método de clonación MUY SIMPLIFICADO.
     * En una implementación real con JPA, no necesitarías esto de esta forma.
     * El propósito aquí es simular que el repositorio devuelve instancias "frescas"
     * y que las modificaciones se hacen sobre esas instancias y luego se guardan.
     */
    private Pedido clonarPedido(Pedido original) {
        if (original == null) return null;

        // Reconstruir el pedido usando el constructor que toma todos los campos.
        // Esto es más seguro que intentar clonar campos individualmente con reflexión
        // y más alineado con la idea de reconstruir desde un estado persistido.
        return new Pedido(
            original.getId(),
            original.getIdCliente(),
            new com.ejemplo.ddd.dominio.modelo.pedido.Direccion( // Asegurar nueva instancia de VO
                original.getDireccionEnvio().calle(),
                original.getDireccionEnvio().ciudad(),
                original.getDireccionEnvio().codigoPostal(),
                original.getDireccionEnvio().pais()
            ),
            original.getLineasPedido().stream() // Clonar líneas también
                .map(lp -> new com.ejemplo.ddd.dominio.modelo.pedido.LineaPedido(
                    lp.getIdProducto(),
                    lp.getCantidad(),
                    new com.ejemplo.ddd.dominio.modelo.pedido.Dinero( // Asegurar nueva instancia de VO
                        lp.getPrecioUnitario().cantidad(),
                        lp.getPrecioUnitario().moneda()
                    )
                ))
                .collect(java.util.stream.Collectors.toList()),
            new com.ejemplo.ddd.dominio.modelo.pedido.Dinero( // Asegurar nueva instancia de VO
                original.getTotalPedido().cantidad(),
                original.getTotalPedido().moneda()
            ),
            original.getEstado(),
            original.getFechaCreacion(),
            original.getFechaUltimaModificacion()
        );
    }
}
