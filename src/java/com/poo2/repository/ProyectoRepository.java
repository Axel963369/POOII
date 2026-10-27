
package com.poo2.repository;

import com.poo2.modelo.Proyecto;
import java.util.List;

public interface ProyectoRepository {
     // Registrar un proyecto nuevo.
    boolean registrar(Proyecto proyecto);

    // Listar solamente los proyectos de un usuario.
    List<Proyecto> listarPorUsuario(int idUsuario);

    // Listar todos los proyectos para el administrador.
    List<Proyecto> listarTodos();

    // Buscar un proyecto por su identificador.
    Proyecto buscarPorId(int idProyecto);

    // Actualizar un proyecto propio del usuario.
    boolean actualizar(Proyecto proyecto);

    // Actualizar un proyecto como administrador.
    boolean actualizarComoAdmin(Proyecto proyecto);

    // Cambiar el estado de un proyecto.
    boolean cambiarEstado(int idProyecto, String estado);
}
