package com.ejemplo.ddd.aplicacion.servicio;

import com.ejemplo.ddd.aplicacion.dto.ActualizarDireccionRequest;
import com.ejemplo.ddd.aplicacion.dto.AgregarLineaRequest;
import com.ejemplo.ddd.aplicacion.dto.CrearPedidoRequest;
import com.ejemplo.ddd.aplicacion.dto.PedidoDTO;
import com.ejemplo.ddd.dominio.modelo.pedido.*;
import com.ejemplo.ddd.dominio.modelo.producto.IdentificadorProducto;
import com.ejemplo.ddd.dominio.repositorio.PedidoRepository;
import com.ejemplo.ddd.dominio.servicio.ServicioRealizacionPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoAplicacionServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ServicioRealizacionPedido servicioRealizacionPedido;

    @InjectMocks
    private PedidoAplicacionService pedidoAplicacionService;

    private IdentificadorPedido idPedido;
    private IdentificadorProducto idProducto;
    private Pedido pedidoMock;
    private final String clienteId = "cliente1";
    private final Currency moneda = Currency.getInstance("EUR");

    @BeforeEach
    void setUp() {
        idProducto = IdentificadorProducto.nuevo();
        Direccion direccion = new Direccion("Calle Test", "Ciudad Test", "28000", "ES");
        pedidoMock = Pedido.crearNuevoPedido(clienteId, direccion, moneda);
        idPedido = pedidoMock.getId();
    }

    @Test
    void gestionarCreacionPedido_ConMonedaInvalida_LanzaExcepcion() {
        CrearPedidoRequest.DireccionData direccionData = new CrearPedidoRequest.DireccionData("Calle", "Ciudad", "28000", "ES");
        CrearPedidoRequest req = new CrearPedidoRequest("cliente1", direccionData, List.of(), "INVALIDA");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> pedidoAplicacionService.gestionarCreacionPedido(req));
        assertTrue(exception.getMessage().contains("Código de moneda no válido"));
    }

    @Test
    void obtenerPedidoPorId_NoExistente_DevuelveVacio() {
        when(pedidoRepository.buscarPorId(idPedido)).thenReturn(Optional.empty());
        Optional<PedidoDTO> result = pedidoAplicacionService.obtenerPedidoPorId(idPedido);
        assertTrue(result.isEmpty());
    }

    @Test
    void actualizarDireccionEnvio_Existente_ActualizaYGuarda() {
        when(pedidoRepository.buscarPorId(idPedido)).thenReturn(Optional.of(pedidoMock));

        ActualizarDireccionRequest req = new ActualizarDireccionRequest(new ActualizarDireccionRequest.DireccionData("Nueva Calle", "Otra Ciudad", "28001", "ES"));
        PedidoDTO result = pedidoAplicacionService.actualizarDireccionEnvio(idPedido, req);

        assertEquals("Nueva Calle", result.direccionEnvio().calle());
        assertEquals("Otra Ciudad", result.direccionEnvio().ciudad());
        verify(pedidoRepository).guardar(pedidoMock);
    }

    @Test
    void agregarLineaAPedido_Existente_AgregaYGuarda() {
        when(pedidoRepository.buscarPorId(idPedido)).thenReturn(Optional.of(pedidoMock));

        AgregarLineaRequest req = new AgregarLineaRequest(idProducto.valor(), 2, BigDecimal.valueOf(15.0));
        PedidoDTO result = pedidoAplicacionService.agregarLineaAPedido(idPedido, req);

        assertFalse(result.lineasPedido().isEmpty());
        assertEquals(1, result.lineasPedido().size());
        assertEquals(2, result.lineasPedido().get(0).cantidad());
        assertEquals(0, BigDecimal.valueOf(15.0).compareTo(result.lineasPedido().get(0).precioUnitario()));
        verify(pedidoRepository).guardar(pedidoMock);
    }

    @Test
    void eliminarLineaDePedido_Existente() {
        pedidoMock.agregarLineaPedido(idProducto, 1, new Dinero(BigDecimal.TEN, moneda));
        when(pedidoRepository.buscarPorId(idPedido)).thenReturn(Optional.of(pedidoMock));

        PedidoDTO result = pedidoAplicacionService.eliminarLineaDePedido(idPedido, idProducto);

        assertTrue(result.lineasPedido().isEmpty());
        verify(pedidoRepository).guardar(pedidoMock);
    }

    @Test
    void confirmarPedido_Valido() {
        pedidoMock.agregarLineaPedido(idProducto, 1, new Dinero(BigDecimal.TEN, moneda));
        when(pedidoRepository.buscarPorId(idPedido)).thenReturn(Optional.of(pedidoMock));

        PedidoDTO result = pedidoAplicacionService.confirmarPedido(idPedido);

        assertEquals(EstadoPedido.PROCESANDO, result.estado());
        verify(pedidoRepository).guardar(pedidoMock);
    }
    
    @Test
    void confirmarPedido_NoExistente_LanzaPedidoNoEncontradoException() {
        when(pedidoRepository.buscarPorId(idPedido)).thenReturn(Optional.empty());

        assertThrows(PedidoNoEncontradoException.class, () -> pedidoAplicacionService.confirmarPedido(idPedido));
    }
}
