
package com.poo2.servicio;

public class CuentaBloqueadaException extends RuntimeException {
    public CuentaBloqueadaException(String mensaje) {
        super(mensaje);
    }
}
