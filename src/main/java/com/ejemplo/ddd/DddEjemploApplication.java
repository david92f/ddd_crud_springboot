package com.ejemplo.ddd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DddEjemploApplication {
    public static void main(String[] args) {
        SpringApplication.run(DddEjemploApplication.class, args);
        System.out.println("\nINFO: Aplicación DDD CRUD de Pedidos iniciada.");
        System.out.println("INFO: Puede probar los endpoints con Postman o curl.");
        System.out.println("Ejemplo para crear un pedido (POST a /api/pedidos):");
        System.out.println("""
        {
            "idCliente": "cliente-123",
            "direccionEnvio": {
                "calle": "Calle Falsa 123",
                "ciudad": "Springfield",
                "codigoPostal": "12345",
                "pais": "EEUU"
            },
            "lineas": [
                {
                    "idProducto": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
                    "cantidad": 2,
                    "precioUnitario": 10.50
                },
                {
                    "idProducto": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12",
                    "cantidad": 1,
                    "precioUnitario": 25.00
                }
            ],
            "moneda": "EUR"
        }
        """);
         System.out.println("Un ID de producto de ejemplo para usar en las líneas: " + com.ejemplo.ddd.dominio.modelo.producto.IdentificadorProducto.nuevo().valor());
    }
}
