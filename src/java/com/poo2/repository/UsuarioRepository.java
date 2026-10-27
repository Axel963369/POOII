
package com.poo2.repository;

import com.poo2.modelo.CuentaUsuario;
import java.util.List;
import java.util.Map;


public interface UsuarioRepository {
    
    // Buscar una cuenta para iniciar sesión.
    CuentaUsuario buscarPorEmail(String email);

    // Registrar una cuenta nueva.
    boolean registrar(CuentaUsuario cuenta, String passwordHash);

    // Registrar un intento fallido y bloquear al llegar a tres.
    boolean registrarIntentoFallido(String email);

    // Reiniciar los intentos y guardar la fecha del acceso exitoso.
    void registrarAccesoExitoso(int idUsuario);

    // Operaciones CRUD.
    List<CuentaUsuario> listar();

    CuentaUsuario buscarPorId(int idUsuario);

    boolean actualizar(CuentaUsuario cuenta);

    boolean eliminar(int idUsuario);
    
    Map<Integer, String> listarRoles();
    
    boolean desbloquear(int idUsuario);
    
    Map<String, Integer> obtenerEstadisticasUsuarios();
}
