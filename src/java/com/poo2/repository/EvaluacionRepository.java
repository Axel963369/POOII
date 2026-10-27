
package com.poo2.repository;

import com.poo2.modelo.Evaluacion;
import com.poo2.modelo.RespuestaEvaluacion;
import java.util.List;
import java.util.Map;

public interface EvaluacionRepository {
     // Registrar una evaluación y sus respuestas.
    int registrarEvaluacion(
            Evaluacion evaluacion,
            List<RespuestaEvaluacion> respuestas
    );

    // Consultar el historial de evaluaciones.
    List<Evaluacion> listarEvaluaciones();

    // Consultar una evaluación específica.
    Evaluacion buscarEvaluacionPorId(int idEvaluacion);

    // Consultar las respuestas y los criterios evaluados.
    List<Map<String, Object>> listarRespuestasPorEvaluacion(
            int idEvaluacion
    );

    // Obtener las estadísticas generales.
    Map<String, Object> obtenerEstadisticas();
}
