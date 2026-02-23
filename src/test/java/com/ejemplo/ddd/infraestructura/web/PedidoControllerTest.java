package com.ejemplo.ddd.infraestructura.web;

import com.ejemplo.ddd.aplicacion.dto.CrearPedidoRequest;
import com.ejemplo.ddd.aplicacion.dto.PedidoDTO;
import com.ejemplo.ddd.aplicacion.servicio.PedidoAplicacionService;
import com.ejemplo.ddd.aplicacion.servicio.PedidoNoEncontradoException;
import com.ejemplo.ddd.dominio.modelo.pedido.EstadoPedido;
import com.ejemplo.ddd.dominio.modelo.pedido.IdentificadorPedido;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
public class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PedidoAplicacionService pedidoAplicacionService;

    private String idPedidoStr;
    private IdentificadorPedido identificadorPedido;
    private PedidoDTO pedidoDTOMock;

    @BeforeEach
    void setUp() {
        idPedidoStr = UUID.randomUUID().toString();
        identificadorPedido = IdentificadorPedido.deString(idPedidoStr);
        
        PedidoDTO.DireccionDTO direccionDTO = new PedidoDTO.DireccionDTO("Calle 1", "Ciudad", "28000", "ES");
        pedidoDTOMock = new PedidoDTO(
            UUID.fromString(idPedidoStr),
            "cliente1",
            direccionDTO,
            List.of(),
            BigDecimal.ZERO,
            "EUR",
            EstadoPedido.PENDIENTE,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Test
    void crearPedido_ConDatosValidos_DebeRetornar201() throws Exception {
        CrearPedidoRequest.DireccionData direccionData = new CrearPedidoRequest.DireccionData("Calle", "Ciudad", "28000", "ES");
        CrearPedidoRequest.LineaPedidoData lineaData = new CrearPedidoRequest.LineaPedidoData(UUID.randomUUID(), 1, BigDecimal.TEN);
        CrearPedidoRequest req = new CrearPedidoRequest(
            "cliente1",
            direccionData,
            List.of(lineaData),
            "EUR"
        );

        Mockito.when(pedidoAplicacionService.gestionarCreacionPedido(any(CrearPedidoRequest.class)))
               .thenReturn(pedidoDTOMock);

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.idPedido").value(idPedidoStr));
    }

    @Test
    void crearPedido_ConDatosInvalidos_DebeRetornar400() throws Exception {
        CrearPedidoRequest reqInvalido = new CrearPedidoRequest("", null, List.of(), ""); // Faltan datos requeridos

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqInvalido)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("Error de Validación"));
    }

    @Test
    void obtenerPedidoPorId_Existente_DebeRetornar200() throws Exception {
        Mockito.when(pedidoAplicacionService.obtenerPedidoPorId(identificadorPedido))
               .thenReturn(Optional.of(pedidoDTOMock));

        mockMvc.perform(get("/api/pedidos/{id}", idPedidoStr))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.idPedido").value(idPedidoStr));
    }

    @Test
    void obtenerPedidoPorId_NoExistente_DebeRetornar404() throws Exception {
        Mockito.when(pedidoAplicacionService.obtenerPedidoPorId(any()))
               .thenThrow(new PedidoNoEncontradoException("Pedido no encontrado con ID: " + idPedidoStr));

        mockMvc.perform(get("/api/pedidos/{id}", idPedidoStr))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void obtenerPedidoPorId_FormatoUUIDInvalido_DebeRetornar400() throws Exception {
        mockMvc.perform(get("/api/pedidos/id-invalido"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("Bad Request"));
    }
}
