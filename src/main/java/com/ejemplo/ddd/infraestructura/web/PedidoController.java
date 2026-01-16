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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

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
        try {
            PedidoDTO nuevoPedidoDTO = pedidoAplicacionService.gestionarCreacionPedido(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedidoDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            // Loggear el error: e.printStackTrace();
            System.err.println("Error interno al crear el pedido: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno al crear el pedido.");
        }
    }

    // --- READ ---
    @GetMapping("/{idPedido}")
    public ResponseEntity<PedidoDTO> obtenerPedidoPorId(@PathVariable String idPedido) {
        try {
            IdentificadorPedido identificador = IdentificadorPedido.deString(idPedido);
            return pedidoAplicacionService.obtenerPedidoPorId(identificador)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new PedidoNoEncontradoException("Pedido no encontrado con ID: " + idPedido));
        } catch (PedidoNoEncontradoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) { // Para UUID inválido
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> obtenerTodosLosPedidos() {
        List<PedidoDTO> pedidos = pedidoAplicacionService.obtenerTodosLosPedidos();
        return ResponseEntity.ok(pedidos);
    }

    // --- UPDATE ---
    @PutMapping("/{idPedido}/direccion")
    public ResponseEntity<PedidoDTO> actualizarDireccionEnvio(
            @PathVariable String idPedido,
            @Valid @RequestBody ActualizarDireccionRequest request) {
        try {
            IdentificadorPedido identificador = IdentificadorPedido.deString(idPedido);
            PedidoDTO pedidoActualizado = pedidoAplicacionService.actualizarDireccionEnvio(identificador, request);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (PedidoNoEncontradoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) { // Errores de dominio o UUID inválido
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    @PostMapping("/{idPedido}/lineas")
    public ResponseEntity<PedidoDTO> agregarLineaAPedido(
            @PathVariable String idPedido,
            @Valid @RequestBody AgregarLineaRequest request) {
        try {
            IdentificadorPedido identificador = IdentificadorPedido.deString(idPedido);
            PedidoDTO pedidoActualizado = pedidoAplicacionService.agregarLineaAPedido(identificador, request);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (PedidoNoEncontradoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{idPedido}/lineas/{idProducto}")
    public ResponseEntity<PedidoDTO> eliminarLineaDePedido(
            @PathVariable String idPedido,
            @PathVariable String idProducto) {
        try {
            IdentificadorPedido idP = IdentificadorPedido.deString(idPedido);
            IdentificadorProducto idProd = IdentificadorProducto.deString(idProducto);
            PedidoDTO pedidoActualizado = pedidoAplicacionService.eliminarLineaDePedido(idP, idProd);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (PedidoNoEncontradoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) { // Error de dominio, UUID inválido o línea no encontrada
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{idPedido}/lineas/{idProducto}/cantidad")
    public ResponseEntity<PedidoDTO> actualizarCantidadLinea(
            @PathVariable String idPedido,
            @PathVariable String idProducto,
            @Valid @RequestBody ActualizarCantidadLineaRequest request) {
        try {
            IdentificadorPedido idP = IdentificadorPedido.deString(idPedido);
            IdentificadorProducto idProd = IdentificadorProducto.deString(idProducto);
            PedidoDTO pedidoActualizado = pedidoAplicacionService.actualizarCantidadLinea(idP, idProd, request);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (PedidoNoEncontradoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{idPedido}/confirmar")
    public ResponseEntity<PedidoDTO> confirmarPedido(@PathVariable String idPedido) {
        try {
            IdentificadorPedido identificador = IdentificadorPedido.deString(idPedido);
            PedidoDTO pedidoActualizado = pedidoAplicacionService.confirmarPedido(identificador);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (PedidoNoEncontradoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    @PostMapping("/{idPedido}/enviar")
    public ResponseEntity<PedidoDTO> marcarPedidoComoEnviado(@PathVariable String idPedido) {
        try {
            IdentificadorPedido identificador = IdentificadorPedido.deString(idPedido);
            PedidoDTO pedidoActualizado = pedidoAplicacionService.marcarPedidoComoEnviado(identificador);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (PedidoNoEncontradoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{idPedido}/entregar")
    public ResponseEntity<PedidoDTO> marcarPedidoComoEntregado(@PathVariable String idPedido) {
         try {
            IdentificadorPedido identificador = IdentificadorPedido.deString(idPedido);
            PedidoDTO pedidoActualizado = pedidoAplicacionService.marcarPedidoComoEntregado(identificador);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (PedidoNoEncontradoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    @PostMapping("/{idPedido}/cancelar")
    public ResponseEntity<PedidoDTO> cancelarPedido(
            @PathVariable String idPedido,
            @Valid @RequestBody CancelarPedidoRequest request) {
        try {
            IdentificadorPedido identificador = IdentificadorPedido.deString(idPedido);
            PedidoDTO pedidoActualizado = pedidoAplicacionService.cancelarPedido(identificador, request);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (PedidoNoEncontradoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // --- DELETE ---
    @DeleteMapping("/{idPedido}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable String idPedido) {
        try {
            IdentificadorPedido identificador = IdentificadorPedido.deString(idPedido);
            pedidoAplicacionService.eliminarPedido(identificador);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) { // Para UUID inválido
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        // No se lanza NOT_FOUND aquí si el servicio de aplicación no lo hace,
        // ya que DELETE es idempotente.
    }
}
