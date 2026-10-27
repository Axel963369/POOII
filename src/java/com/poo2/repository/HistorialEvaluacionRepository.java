
package com.poo2.repository;

import com.poo2.modelo.Evaluacion;
import java.util.List;

public interface HistorialEvaluacionRepository {
    List<Evaluacion> listarPorUsuario(int idUsuario);

    List<Evaluacion> listarPorProyecto(int idProyecto, int idUsuario);
}
