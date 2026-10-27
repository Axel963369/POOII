
package com.poo2.repository;

import com.poo2.modelo.Evaluacion;
import com.poo2.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class HistorialEvaluacionRepositoryPostgreSQL implements HistorialEvaluacionRepository{
    @Override
    public List<Evaluacion> listarPorUsuario(int idUsuario) {

        String sql = """
            SELECT e.id_evaluacion,
                   e.id_proyecto,
                   e.id_usuario,
                   e.fecha_evaluacion,
                   e.porcentaje_cumplimiento,
                   e.nivel_calidad,
                   e.estado,
                   p.nombre AS nombre_proyecto
            FROM evaluaciones e
            INNER JOIN proyectos p
                ON e.id_proyecto = p.id_proyecto
            WHERE e.id_usuario = ?
            ORDER BY e.fecha_evaluacion DESC,
                     e.id_evaluacion DESC
            """;

        return consultar(sql, idUsuario, null);
    }

    @Override
    public List<Evaluacion> listarPorProyecto(
            int idProyecto, int idUsuario) {

        String sql = """
            SELECT e.id_evaluacion,
                   e.id_proyecto,
                   e.id_usuario,
                   e.fecha_evaluacion,
                   e.porcentaje_cumplimiento,
                   e.nivel_calidad,
                   e.estado,
                   p.nombre AS nombre_proyecto
            FROM evaluaciones e
            INNER JOIN proyectos p
                ON e.id_proyecto = p.id_proyecto
            WHERE e.id_proyecto = ?
              AND e.id_usuario = ?
            ORDER BY e.fecha_evaluacion DESC,
                     e.id_evaluacion DESC
            """;

        return consultar(sql, idProyecto, idUsuario);
    }

    private List<Evaluacion> consultar(
            String sql, int primerParametro, Integer segundoParametro) {

        List<Evaluacion> evaluaciones = new ArrayList<>();

        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, primerParametro);

            if (segundoParametro != null) {
                ps.setInt(2, segundoParametro);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Evaluacion evaluacion = new Evaluacion();

                    evaluacion.setIdEvaluacion(
                            rs.getInt("id_evaluacion"));

                    evaluacion.setIdProyecto(
                            rs.getInt("id_proyecto"));

                    evaluacion.setIdUsuario(
                            rs.getInt("id_usuario"));

                    Timestamp fecha =
                            rs.getTimestamp("fecha_evaluacion");

                    if (fecha != null) {
                        evaluacion.setFechaEvaluacion(
                                fecha.toLocalDateTime());
                    }

                    evaluacion.setPorcentajeCumplimiento(
                            rs.getDouble("porcentaje_cumplimiento"));

                    evaluacion.setNivelCalidad(
                            rs.getString("nivel_calidad"));

                    evaluacion.setEstado(
                            rs.getString("estado"));

                    evaluacion.setNombreProyecto(
                            rs.getString("nombre_proyecto"));

                    evaluaciones.add(evaluacion);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al consultar el historial de evaluaciones.", e);
        }

        return evaluaciones;
    }
}
