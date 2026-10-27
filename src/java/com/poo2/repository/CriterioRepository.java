
package com.poo2.repository;

import com.poo2.modelo.Criterio;
import java.util.List;

public interface CriterioRepository {
    List<Criterio> listarActivos();

    Criterio buscarPorId(int idCriterio);

    boolean registrar(Criterio criterio);

    boolean actualizar(Criterio criterio);

    boolean cambiarEstado(int idCriterio, boolean activo);
}
