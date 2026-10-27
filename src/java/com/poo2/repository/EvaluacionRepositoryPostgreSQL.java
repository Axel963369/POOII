
package com.poo2.repository;

import com.poo2.modelo.Evaluacion;
import com.poo2.modelo.RespuestaEvaluacion;
import com.poo2.util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluacionRepositoryPostgreSQL implements EvaluacionRepository{
    @Override
    public int registrarEvaluacion(
            Evaluacion evaluacion,
            List<RespuestaEvaluacion> respuestas) {

        if (evaluacion == null || respuestas == null
                || respuestas.isEmpty()) {
            throw new IllegalArgumentException(
                    "La evaluación y sus respuestas son obligatorias.");
        }

        String sqlEvaluacion = """
            INSERT INTO evaluaciones
                (id_proyecto, id_usuario, porcentaje_cumplimiento,
                 nivel_calidad, estado)
            VALUES (?, ?, 0, 'PENDIENTE', 'COMPLETADA')
            """;

        String sqlRespuesta = """
            INSERT INTO respuestas_evaluacion
                (id_evaluacion, id_criterio, cumplimiento, observacion)
            VALUES (?, ?, ?, ?)
            """;

        String sqlActualizar = """
            UPDATE evaluaciones
            SET porcentaje_cumplimiento = ?, nivel_calidad = ?
            WHERE id_evaluacion = ?
            """;

        Connection con = null;

        try {
            con = Conexion.getConexion();

            if (con == null) {
                throw new SQLException(
                        "No se pudo obtener la conexión.");
            }

            boolean autoCommitOriginal = con.getAutoCommit();
            con.setAutoCommit(false);

            try {
                int idEvaluacion;

                // 1. Registrar la evaluación.
                try (PreparedStatement ps = con.prepareStatement(
                        sqlEvaluacion, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, evaluacion.getIdProyecto());
                    ps.setInt(2, evaluacion.getIdUsuario());
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "No se pudo obtener el ID de la evaluación.");
                        }

                        idEvaluacion = rs.getInt(1);
                    }
                }

                // 2. Guardar las respuestas y calcular el resultado.
                double puntosObtenidos = 0;
                double puntosPosibles = 0;

                String sqlPeso = """
                    SELECT peso
                    FROM criterios
                    WHERE id_criterio = ? AND activo = TRUE
                    """;

                try (PreparedStatement psRespuesta =
                             con.prepareStatement(sqlRespuesta);
                     PreparedStatement psPeso =
                             con.prepareStatement(sqlPeso)) {

                    for (RespuestaEvaluacion respuesta : respuestas) {

                        if (respuesta == null) {
                            throw new IllegalArgumentException(
                                    "Se encontró una respuesta vacía.");
                        }

                        String cumplimiento =
                                respuesta.getCumplimiento();

                        if (cumplimiento == null
                                || !cumplimiento.matches(
                                    "CUMPLE|PARCIAL|NO_CUMPLE|NO_APLICA")) {
                            throw new IllegalArgumentException(
                                    "Una respuesta tiene un valor inválido.");
                        }

                        psPeso.setInt(
                                1, respuesta.getIdCriterio());

                        int peso;

                        try (ResultSet rs = psPeso.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException(
                                        "El criterio no existe o está inactivo.");
                            }

                            peso = rs.getInt("peso");
                        }

                        psRespuesta.setInt(1, idEvaluacion);
                        psRespuesta.setInt(
                                2, respuesta.getIdCriterio());
                        psRespuesta.setString(3, cumplimiento);

                        String observacion =
                                respuesta.getObservacion();

                        psRespuesta.setString(
                                4,
                                observacion == null
                                        || observacion.isBlank()
                                        ? null : observacion.trim());

                        psRespuesta.executeUpdate();

                        if (!"NO_APLICA".equals(cumplimiento)) {
                            puntosPosibles += peso;

                            switch (cumplimiento) {
                                case "CUMPLE" ->
                                    puntosObtenidos += peso;
                                case "PARCIAL" ->
                                    puntosObtenidos += peso * 0.5;
                                case "NO_CUMPLE" -> {
                                    // No suma puntos.
                                }
                            }
                        }
                    }
                }

                if (puntosPosibles == 0) {
                    throw new IllegalArgumentException(
                            "No se puede calcular el porcentaje: "
                            + "ningún criterio es aplicable.");
                }

                double porcentaje = Math.round(
                        (puntosObtenidos / puntosPosibles) * 10000.0
                ) / 100.0;

                String nivelCalidad;

                if (porcentaje >= 90) {
                    nivelCalidad = "EXCELENTE";
                } else if (porcentaje >= 75) {
                    nivelCalidad = "BUENA";
                } else if (porcentaje >= 60) {
                    nivelCalidad = "ACEPTABLE";
                } else {
                    nivelCalidad = "BAJA";
                }

                // 3. Actualizar el resultado final.
                try (PreparedStatement ps =
                             con.prepareStatement(sqlActualizar)) {

                    ps.setDouble(1, porcentaje);
                    ps.setString(2, nivelCalidad);
                    ps.setInt(3, idEvaluacion);
                    ps.executeUpdate();
                }

                con.commit();

                evaluacion.setIdEvaluacion(idEvaluacion);
                evaluacion.setPorcentajeCumplimiento(porcentaje);
                evaluacion.setNivelCalidad(nivelCalidad);
                evaluacion.setEstado("COMPLETADA");

                return idEvaluacion;

            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(autoCommitOriginal);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al guardar la evaluación: "
                    + e.getMessage(), e);
        }
    }

    // =====================================================
    // HISTORIAL DE EVALUACIONES
    // =====================================================

    @Override
    public List<Evaluacion> listarEvaluaciones() {

        List<Evaluacion> evaluaciones = new ArrayList<>();

        String sql = """
            SELECT e.id_evaluacion,
                   e.id_proyecto,
                   e.id_usuario,
                   e.fecha_evaluacion,
                   e.porcentaje_cumplimiento,
                   e.nivel_calidad,
                   e.estado,
                   p.nombre AS nombre_proyecto,
                   u.nombre AS nombre_usuario
            FROM evaluaciones e
            INNER JOIN proyectos p
                ON p.id_proyecto = e.id_proyecto
            INNER JOIN usuarios u
                ON u.id = e.id_usuario
            ORDER BY e.fecha_evaluacion DESC,
                     e.id_evaluacion DESC
            """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Evaluacion e = new Evaluacion();

                e.setIdEvaluacion(
                        rs.getInt("id_evaluacion"));
                e.setIdProyecto(
                        rs.getInt("id_proyecto"));
                e.setIdUsuario(
                        rs.getInt("id_usuario"));

                java.sql.Timestamp fecha =
                        rs.getTimestamp("fecha_evaluacion");

                if (fecha != null) {
                    e.setFechaEvaluacion(
                            fecha.toLocalDateTime());
                }

                e.setPorcentajeCumplimiento(
                        rs.getDouble("porcentaje_cumplimiento"));
                e.setNivelCalidad(
                        rs.getString("nivel_calidad"));
                e.setEstado(rs.getString("estado"));
                e.setNombreProyecto(
                        rs.getString("nombre_proyecto"));
                e.setNombreUsuario(
                        rs.getString("nombre_usuario"));

                evaluaciones.add(e);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al consultar el historial de evaluaciones.",
                    e);
        }

        return evaluaciones;
    }

    // =====================================================
    // BUSCAR UNA EVALUACIÓN
    // =====================================================

    @Override
    public Evaluacion buscarEvaluacionPorId(int idEvaluacion) {

        String sql = """
            SELECT e.id_evaluacion,
                   e.id_proyecto,
                   e.id_usuario,
                   e.fecha_evaluacion,
                   e.porcentaje_cumplimiento,
                   e.nivel_calidad,
                   e.estado,
                   p.nombre AS nombre_proyecto,
                   u.nombre AS nombre_usuario
            FROM evaluaciones e
            INNER JOIN proyectos p
                ON p.id_proyecto = e.id_proyecto
            INNER JOIN usuarios u
                ON u.id = e.id_usuario
            WHERE e.id_evaluacion = ?
            """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEvaluacion);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                Evaluacion e = new Evaluacion();

                e.setIdEvaluacion(
                        rs.getInt("id_evaluacion"));
                e.setIdProyecto(
                        rs.getInt("id_proyecto"));
                e.setIdUsuario(
                        rs.getInt("id_usuario"));

                java.sql.Timestamp fecha =
                        rs.getTimestamp("fecha_evaluacion");

                if (fecha != null) {
                    e.setFechaEvaluacion(
                            fecha.toLocalDateTime());
                }

                e.setPorcentajeCumplimiento(
                        rs.getDouble("porcentaje_cumplimiento"));
                e.setNivelCalidad(
                        rs.getString("nivel_calidad"));
                e.setEstado(rs.getString("estado"));
                e.setNombreProyecto(
                        rs.getString("nombre_proyecto"));
                e.setNombreUsuario(
                        rs.getString("nombre_usuario"));

                return e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al consultar la evaluación.", e);
        }
    }

    // =====================================================
    // DETALLE: CRITERIOS Y RESPUESTAS
    // =====================================================

    @Override
    public List<Map<String, Object>> listarRespuestasPorEvaluacion(
            int idEvaluacion) {

        List<Map<String, Object>> respuestas = new ArrayList<>();

        String sql = """
            SELECT c.id_criterio,
                   c.codigo,
                   c.nombre AS nombre_criterio,
                   c.descripcion,
                   c.categoria,
                   c.peso,
                   re.cumplimiento,
                   re.observacion
            FROM respuestas_evaluacion re
            INNER JOIN criterios c
                ON c.id_criterio = re.id_criterio
            WHERE re.id_evaluacion = ?
            ORDER BY c.categoria, c.codigo
            """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEvaluacion);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();

                    fila.put("idCriterio",
                            rs.getInt("id_criterio"));
                    fila.put("codigo",
                            rs.getString("codigo"));
                    fila.put("nombreCriterio",
                            rs.getString("nombre_criterio"));
                    fila.put("descripcion",
                            rs.getString("descripcion"));
                    fila.put("categoria",
                            rs.getString("categoria"));
                    fila.put("peso",
                            rs.getInt("peso"));
                    fila.put("cumplimiento",
                            rs.getString("cumplimiento"));
                    fila.put("observacion",
                            rs.getString("observacion"));

                    respuestas.add(fila);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al consultar las respuestas de la evaluación.",
                    e);
        }

        return respuestas;
    }

    // =====================================================
    // ESTADÍSTICAS GENERALES
    // =====================================================

    @Override
    public Map<String, Object> obtenerEstadisticas() {

        Map<String, Object> estadisticas = new HashMap<>();

        String sql = """
            SELECT COUNT(*) AS total_evaluaciones,
                   COUNT(DISTINCT id_proyecto) AS proyectos_evaluados,
                   COALESCE(
                       ROUND(AVG(porcentaje_cumplimiento), 2), 0
                   ) AS promedio_cumplimiento,
                   COUNT(*) FILTER (
                       WHERE nivel_calidad = 'EXCELENTE'
                   ) AS nivel_excelente,
                   COUNT(*) FILTER (
                       WHERE nivel_calidad = 'BUENA'
                   ) AS nivel_buena,
                   COUNT(*) FILTER (
                       WHERE nivel_calidad = 'ACEPTABLE'
                   ) AS nivel_aceptable,
                   COUNT(*) FILTER (
                       WHERE nivel_calidad = 'BAJA'
                   ) AS nivel_baja
            FROM evaluaciones
            WHERE estado = 'COMPLETADA'
            """;

        String sqlDeficiencias = """
            SELECT COUNT(*) AS total_deficiencias
            FROM respuestas_evaluacion re
            INNER JOIN evaluaciones e
                ON e.id_evaluacion = re.id_evaluacion
            WHERE e.estado = 'COMPLETADA'
              AND re.cumplimiento = 'NO_CUMPLE'
            """;

        try (Connection con = Conexion.getConexion()) {

            try (PreparedStatement ps =
                         con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    estadisticas.put("totalEvaluaciones",
                            rs.getInt("total_evaluaciones"));
                    estadisticas.put("proyectosEvaluados",
                            rs.getInt("proyectos_evaluados"));
                    estadisticas.put("promedioCumplimiento",
                            rs.getDouble("promedio_cumplimiento"));
                    estadisticas.put("nivelExcelente",
                            rs.getInt("nivel_excelente"));
                    estadisticas.put("nivelBuena",
                            rs.getInt("nivel_buena"));
                    estadisticas.put("nivelAceptable",
                            rs.getInt("nivel_aceptable"));
                    estadisticas.put("nivelBaja",
                            rs.getInt("nivel_baja"));
                }
            }

            try (PreparedStatement ps =
                         con.prepareStatement(sqlDeficiencias);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    estadisticas.put("totalDeficiencias",
                            rs.getInt("total_deficiencias"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al calcular las estadísticas de evaluaciones.",
                    e);
        }

        return estadisticas;
    }
}
