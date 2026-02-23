package com.ejemplo.ddd.infraestructura.web;

import com.ejemplo.ddd.aplicacion.dto.*;
import com.ejemplo.ddd.aplicacion.servicio.PedidoAplicacionService;
import com.ejemplo.ddd.aplicacion.servicio.PedidoNoEncontradoException;
import com.ejemplo.ddd.dominio.modelo.pedido.IdentificadorPedido;
import com.ejemplo.ddd.dominio.modelo.producto.IdentificadorProducto;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoAplicacionService pedidoAplicacionService;

    public PedidoController(PedidoAplicacionService pedidoAplicacionService) {
        this.pedidoAplicacionService = pedidoAplicacionService;
    }

    // --- CREATE ---
    @PostMapping
    public ResponseEntity<PedidoDTO> crearPedido(@Valid @RequestBody CrearPedidoRequest request) {
        PedidoDTO nuevoPedidoDTO = pedidoAplicacionService.gestionarCreacionPedido(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedidoDTO);
    }

    // --- READ ---
    @GetMapping("/{idPedido}")
    public ResponseEntity<PedidoDTO> obtenerPedidoPorId(@PathVariable String idPedido) {
        IdentificadorPedido identificador = IdentificadorPedido.deString(idPedido);
        return pedidoAplicacionService.obtenerPedidoPorId(identificador)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new PedidoNoEncontradoException("Pedido no encontrado con ID: " + idPedido));
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> obtenerTodosLosPedidos() {
        return ResponseEntity.ok(pedidoAplicacionService.obtenerTodosLosPedidos());
    }

    // --- UPDATE ---
    @PutMapping("/{idPedido}/direccion")
    public ResponseEntity<PedidoDTO> actualizarDireccionEnvio(
            @PathVariable String idPedido,
            @Valid @RequestBody ActualizarDireccionRequest request) {
        IdentificadorPedido identificador = IdentificadorPedido.deString(idPedido);
        return ResponseEntity.ok(pedidoAplicacionService.actualizarDireccionEnvio(identificador, request));
    }
    
    @PostMapping("/{idPedido}/lineas")
    public ResponseEntity<PedidoDTO> agregarLineaAPedido(
            @PathVariable String idPedido,
            @Valid @RequestBody AgregarLineaRequest request) {
        IdentificadorPedido identificador = IdentificadorPedido.deString(idPedido);
        return ResponseEntity.ok(pedidoAplicacionService.agregarLineaAPedido(identificador, request));
    }

    @DeleteMapping("/{idPedido}/lineas/{idProducto}")
    public ResponseEntity<PedidoDTO> eliminarLineaDePedido(
            @PathVariable String idPedido,
            @PathVariable String idProducto) {
        IdentificadorPedido idP = IdentificadorPedido.deString(idPedido);
        IdentificadorProducto idProd = IdentificadorProducto.deString(idProducto);
        return ResponseEntity.ok(pedidoAplicacionService.eliminarLineaDePedido(idP, idProd));
    }

    @PutMapping("/{idPedido}/lineas/{idProducto}/cantidad")
    public ResponseEntity<PedidoDTO> actualizarCantidadLinea(
            @PathVariable String idPedido,
            @PathVariable String idProducto,
            @Valid @RequestBody ActualizarCantidadLineaRequest request) {
        IdentificadorPedido idP = IdentificadorPedido.deString(idPedido);
        IdentificadorProducto idProd = IdentificadorProducto.deString(idProducto);
        return ResponseEntity.ok(pedidoAplicacionService.actualizarCantidadLinea(idP, idProd, request));
    }

    @PostMapping("/{idPedido}/confirmar")
    public ResponseEntity<PedidoDTO> confirmarPedido(@PathVariable String idPedido) {
        IdentificadorPedido identificador = IdentificadorPedido.deString(idPedido);
        return ResponseEntity.ok(pedidoAplicacionService.confirmarPedido(identificador));
    }
    
    @PostMapping("/{idPedido}/enviar")
    public ResponseEntity<PedidoDTO> marcarPedidoComoEnviado(@PathVariable String idPedido) {
        IdentificadorPedido identificador = IdentificadorPedido.deString(idPedido);
        return ResponseEntity.ok(pedidoAplicacionService.marcarPedidoComoEnviado(identificador));
    }

    @PostMapping("/{idPedido}/entregar")
    public ResponseEntity<PedidoDTO> marcarPedidoComoEntregado(@PathVariable String idPedido) {
        IdentificadorPedido identificador = IdentificadorPedido.deString(idPedido);
        return ResponseEntity.ok(pedidoAplicacionService.marcarPedidoComoEntregado(identificador));
    }
    
    @PostMapping("/{idPedido}/cancelar")
    public ResponseEntity<PedidoDTO> cancelarPedido(
            @PathVariable String idPedido,
            @Valid @RequestBody CancelarPedidoRequest request) {
        IdentificadorPedido identificador = IdentificadorPedido.deString(idPedido);
        return ResponseEntity.ok(pedidoAplicacionService.cancelarPedido(identificador, request));
    }

    // --- DELETE ---
    @DeleteMapping("/{idPedido}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable String idPedido) {
        IdentificadorPedido identificador = IdentificadorPedido.deString(idPedido);
        pedidoAplicacionService.eliminarPedido(identificador);
        return ResponseEntity.noContent().build();
    }
}
