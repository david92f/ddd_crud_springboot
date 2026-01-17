package com.ejemplo.ddd.aplicacion.servicio;

import com.ejemplo.ddd.aplicacion.dto.*;
import com.ejemplo.ddd.dominio.modelo.pedido.*;
import com.ejemplo.ddd.dominio.modelo.producto.IdentificadorProducto;
import com.ejemplo.ddd.dominio.repositorio.PedidoRepository;
import com.ejemplo.ddd.dominio.servicio.ServicioRealizacionPedido;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio de Aplicación para gestionar los casos de uso relacionados con Pedidos.
 * Orquesta la lógica de dominio y la persistencia.
 */
@Service
public class PedidoAplicacionService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoAplicacionService.class);

    private final PedidoRepository pedidoRepository;
    private final ServicioRealizacionPedido servicioRealizacionPedido;

    public PedidoAplicacionService(PedidoRepository pedidoRepository, ServicioRealizacionPedido servicioRealizacionPedido) {
        this.pedidoRepository = pedidoRepository;
        this.servicioRealizacionPedido = servicioRealizacionPedido;
    }

    // --- CREATE ---
    @Transactional
    public PedidoDTO gestionarCreacionPedido(CrearPedidoRequest request) {
        logger.info("Iniciando gestión de creación de pedido para cliente {}", request.idCliente());
        Direccion direccionDominio = new Direccion(
            request.direccionEnvio().calle(),
            request.direccionEnvio().ciudad(),
            request.direccionEnvio().codigoPostal(),
            request.direccionEnvio().pais()
        );

        List<ServicioRealizacionPedido.InfoLineaPedido> lineasInfo = request.lineas().stream()
            .map(l -> new ServicioRealizacionPedido.InfoLineaPedido(
                new IdentificadorProducto(l.idProducto()),
                l.cantidad(),
                l.precioUnitario()
            ))
            .collect(Collectors.toList());
        
        Currency moneda;
        try {
            moneda = Currency.getInstance(request.moneda().toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("Código de moneda no válido: {}", request.moneda());
            throw new IllegalArgumentException("Código de moneda no válido: " + request.moneda());
        }


        Pedido nuevoPedido = servicioRealizacionPedido.realizarPedido(
            request.idCliente(),
            direccionDominio,
            lineasInfo,
            moneda
        );
        PedidoDTO dto = convertirAPedidoDTO(nuevoPedido);
        logger.info("Pedido creado con id {} para cliente {}", dto.idPedido(), dto.idCliente());
        return dto;
    }

    // --- READ ---
    @Transactional(readOnly = true)
    public Optional<PedidoDTO> obtenerPedidoPorId(IdentificadorPedido idPedido) {
        logger.debug("Obtener pedido por id {}", idPedido.valor());
        return pedidoRepository.buscarPorId(idPedido)
                               .map(this::convertirAPedidoDTO);
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> obtenerTodosLosPedidos() {
        logger.debug("Obtener todos los pedidos");
        return pedidoRepository.buscarTodos().stream()
                               .map(this::convertirAPedidoDTO)
                               .collect(Collectors.toList());
    }

    // --- UPDATE ---
    @Transactional
    public PedidoDTO actualizarDireccionEnvio(IdentificadorPedido idPedido, ActualizarDireccionRequest request) {
        logger.info("Actualizar dirección pedido {}", idPedido.valor());
        Pedido pedido = buscarPedidoOExcepcion(idPedido);
        Direccion nuevaDireccion = new Direccion(
            request.nuevaDireccion().calle(),
            request.nuevaDireccion().ciudad(),
            request.nuevaDireccion().codigoPostal(),
            request.nuevaDireccion().pais()
        );
        pedido.actualizarDireccionEnvio(nuevaDireccion);
        pedidoRepository.guardar(pedido);
        return convertirAPedidoDTO(pedido);
    }
    
    @Transactional
    public PedidoDTO agregarLineaAPedido(IdentificadorPedido idPedido, AgregarLineaRequest request) {
        logger.info("Agregar línea al pedido {}: producto {} cantidad {}", idPedido.valor(), request.idProducto(), request.cantidad());
        Pedido pedido = buscarPedidoOExcepcion(idPedido);
        // La moneda de la nueva línea debe ser la misma que la del pedido.
        // El precio se proporciona en el request.
        Dinero precioNuevaLinea = new Dinero(request.precioUnitario(), pedido.getTotalPedido().moneda());
        
        pedido.agregarLineaPedido(
            new IdentificadorProducto(request.idProducto()),
            request.cantidad(),
            precioNuevaLinea
        );
        pedidoRepository.guardar(pedido);
        return convertirAPedidoDTO(pedido);
    }

    @Transactional
    public PedidoDTO eliminarLineaDePedido(IdentificadorPedido idPedido, IdentificadorProducto idProducto) {
        logger.info("Eliminar línea {} del pedido {}", idProducto.valor(), idPedido.valor());
        Pedido pedido = buscarPedidoOExcepcion(idPedido);
        pedido.eliminarLineaPedido(idProducto);
        pedidoRepository.guardar(pedido);
        return convertirAPedidoDTO(pedido);
    }
    
    @Transactional
    public PedidoDTO actualizarCantidadLinea(IdentificadorPedido idPedido, IdentificadorProducto idProducto, ActualizarCantidadLineaRequest request) {
        logger.info("Actualizar cantidad linea {} en pedido {} a {}", idProducto.valor(), idPedido.valor(), request.nuevaCantidad());
        Pedido pedido = buscarPedidoOExcepcion(idPedido);
        pedido.actualizarCantidadLineaPedido(idProducto, request.nuevaCantidad());
        pedidoRepository.guardar(pedido);
        return convertirAPedidoDTO(pedido);
    }

    @Transactional
    public PedidoDTO confirmarPedido(IdentificadorPedido idPedido) {
        logger.info("Confirmar pedido {}", idPedido.valor());
        Pedido pedido = buscarPedidoOExcepcion(idPedido);
        pedido.confirmarPedido();
        pedidoRepository.guardar(pedido);
        return convertirAPedidoDTO(pedido);
    }

    @Transactional
    public PedidoDTO marcarPedidoComoEnviado(IdentificadorPedido idPedido) {
        logger.info("Marcar pedido {} como enviado", idPedido.valor());
        Pedido pedido = buscarPedidoOExcepcion(idPedido);
        pedido.marcarComoEnviado();
        pedidoRepository.guardar(pedido);
        return convertirAPedidoDTO(pedido);
    }

    @Transactional
    public PedidoDTO marcarPedidoComoEntregado(IdentificadorPedido idPedido) {
        logger.info("Marcar pedido {} como entregado", idPedido.valor());
        Pedido pedido = buscarPedidoOExcepcion(idPedido);
        pedido.marcarComoEntregado();
        pedidoRepository.guardar(pedido);
        return convertirAPedidoDTO(pedido);
    }

    @Transactional
    public PedidoDTO cancelarPedido(IdentificadorPedido idPedido, CancelarPedidoRequest request) {
        logger.info("Cancelar pedido {} motivo {}", idPedido.valor(), request.motivo());
        Pedido pedido = buscarPedidoOExcepcion(idPedido);
        pedido.cancelarPedido(request.motivo());
        pedidoRepository.guardar(pedido);
        return convertirAPedidoDTO(pedido);
    }

    // --- DELETE ---
    @Transactional
    public void eliminarPedido(IdentificadorPedido idPedido) {
        logger.info("Eliminar pedido {}", idPedido.valor());
        if (pedidoRepository.buscarPorId(idPedido).isEmpty()) {
            // Podríamos lanzar una excepción personalizada o simplemente no hacer nada si no existe.
            // Para un DELETE, si no existe, a menudo se considera una operación exitosa (idempotencia).
            return; 
        }
        pedidoRepository.eliminarPorId(idPedido);
    }

    // --- Métodos de Ayuda ---
    private Pedido buscarPedidoOExcepcion(IdentificadorPedido idPedido) {
        return pedidoRepository.buscarPorId(idPedido)
            .orElseThrow(() -> new PedidoNoEncontradoException("Pedido no encontrado con ID: " + idPedido.valor()));
    }

    private PedidoDTO convertirAPedidoDTO(Pedido pedido) {
        PedidoDTO.DireccionDTO direccionDTO = new PedidoDTO.DireccionDTO(
            pedido.getDireccionEnvio().calle(),
            pedido.getDireccionEnvio().ciudad(),
            pedido.getDireccionEnvio().codigoPostal(),
            pedido.getDireccionEnvio().pais()
        );

        List<PedidoDTO.LineaPedidoDTO> lineasDTO = pedido.getLineasPedido().stream()
            .map(lp -> new PedidoDTO.LineaPedidoDTO(
                lp.getIdProducto().valor(),
                lp.getCantidad(),
                lp.getPrecioUnitario().cantidad(),
                lp.getPrecioUnitario().moneda().getCurrencyCode(),
                lp.calcularSubtotal().cantidad()
            ))
            .collect(Collectors.toList());

        return new PedidoDTO(
            pedido.getId().valor(),
            pedido.getIdCliente(),
            direccionDTO,
            lineasDTO,
            pedido.getTotalPedido().cantidad(),
            pedido.getTotalPedido().moneda().getCurrencyCode(),
            pedido.getEstado(),
            pedido.getFechaCreacion(),
            pedido.getFechaUltimaModificacion()
        );
    }
}
