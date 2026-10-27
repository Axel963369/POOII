
package com.poo2.servicio;

import com.poo2.modelo.Criterio;
import com.poo2.repository.CriterioRepository;
import com.poo2.repository.CriterioRepositoryPostgreSQL;
import java.util.List;

public class CriterioService {
    private final CriterioRepository criterioRepository;

    public CriterioService() {
        this.criterioRepository = new CriterioRepositoryPostgreSQL();
    }

    public List<Criterio> listarCriteriosActivos() {
        return criterioRepository.listarActivos();
    }

    public Criterio buscarCriterio(int idCriterio) {
        if (idCriterio <= 0) {
            throw new IllegalArgumentException(
                "El identificador del criterio no es válido."
            );
        }

        return criterioRepository.buscarPorId(idCriterio);
    }

    public boolean registrarCriterio(Criterio criterio) {
        validarCriterio(criterio);
        return criterioRepository.registrar(criterio);
    }

    public boolean actualizarCriterio(Criterio criterio) {
        if (criterio == null || criterio.getIdCriterio() <= 0) {
            throw new IllegalArgumentException(
                "El criterio que deseas actualizar no es válido."
            );
        }

        validarCriterio(criterio);
        return criterioRepository.actualizar(criterio);
    }

    public boolean cambiarEstado(int idCriterio, boolean activo) {
        if (idCriterio <= 0) {
            throw new IllegalArgumentException(
                "El identificador del criterio no es válido."
            );
        }

        return criterioRepository.cambiarEstado(idCriterio, activo);
    }

    private void validarCriterio(Criterio criterio) {
        if (criterio == null) {
            throw new IllegalArgumentException(
                "Debes proporcionar un criterio."
            );
        }

        if (criterio.getCodigo() == null
                || criterio.getCodigo().isBlank()
                || criterio.getNombre() == null
                || criterio.getNombre().isBlank()
                || criterio.getDescripcion() == null
                || criterio.getDescripcion().isBlank()
                || criterio.getCategoria() == null
                || criterio.getCategoria().isBlank()) {
            throw new IllegalArgumentException(
                "Completa el código, nombre, descripción y categoría."
            );
        }

        if (criterio.getPeso() <= 0) {
            throw new IllegalArgumentException(
                "El peso del criterio debe ser mayor que cero."
            );
        }
    }
}
