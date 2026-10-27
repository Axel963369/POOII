
package com.poo2.servicio;

import com.poo2.modelo.CuentaUsuario;
import com.poo2.repository.UsuarioRepository;
import com.poo2.repository.UsuarioRepositoryPostgreSQL;
import com.poo2.util.HashUtil;

public class AutenticacionService {
    // 1. Única instancia compartida del servicio.
    private static final AutenticacionService INSTANCIA =
            new AutenticacionService();

    // 2. Repositorio utilizado para acceder a PostgreSQL.
    private final UsuarioRepository usuarioRepository;

    // 3. Constructor privado: impide crear instancias desde otras clases.
    private AutenticacionService() {
        this.usuarioRepository = new UsuarioRepositoryPostgreSQL();
    }

    // 4. Punto de acceso público al Singleton.
    public static AutenticacionService getInstancia() {
        return INSTANCIA;
    }

    // 5. Método principal para iniciar sesión.
    public CuentaUsuario autenticar(String email, String password) {

        if (email == null || email.isBlank()
                || password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debes ingresar el correo y la contraseña.");
        }

        String correoNormalizado = email.trim();

        // Buscar la cuenta en la base de datos.
        CuentaUsuario cuenta =
                usuarioRepository.buscarPorEmail(correoNormalizado);

        // No revelar si un correo está registrado.
        if (cuenta == null) {
            throw new IllegalArgumentException(
                    "Correo o contraseña incorrectos.");
        }

        // Rechazar cuentas bloqueadas o inactivas.
        if (cuenta.isBloqueado()
                || !"ACTIVO".equalsIgnoreCase(cuenta.getEstado())) {
            throw new CuentaBloqueadaException(
                    "La cuenta está bloqueada o inactiva. "
                    + "Contacta con el administrador.");
        }

        // Comparar el hash de la contraseña ingresada.
        String hashIngresado = HashUtil.sha256(password);

        if (!hashIngresado.equals(cuenta.getPasswordHash())) {

            // Registrar el fallo en PostgreSQL.
            boolean bloqueada =
                    usuarioRepository.registrarIntentoFallido(
                            correoNormalizado);

            if (bloqueada) {
                throw new CuentaBloqueadaException(
                        "Has alcanzado tres intentos fallidos. "
                        + "Tu cuenta ha sido bloqueada.");
            }

            throw new IllegalArgumentException(
                    "Correo o contraseña incorrectos.");
        }

        // Credenciales correctas: actualizar acceso e intentos.
        usuarioRepository.registrarAccesoExitoso(
                cuenta.getIdUsuario());

        return cuenta;
    }
    
    public boolean registrarUsuario(
            String nombre, String email, String password) {

        if (nombre == null || nombre.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "Todos los campos son obligatorios.");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres.");
        }

        CuentaUsuario cuenta = new CuentaUsuario();
        cuenta.setNombreUsuario(nombre.trim());
        cuenta.setEmail(email.trim());
        cuenta.setUsuarioRegistro("REGISTRO_WEB");

        String hash = HashUtil.sha256(password);

        return usuarioRepository.registrar(cuenta, hash);
    }
}
