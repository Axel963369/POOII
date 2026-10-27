
package com.poo2.modelo;

public class RespuestaEvaluacion {
    private int idRespuesta;
    private int idEvaluacion;
    private int idCriterio;
    private String cumplimiento;
    private String observacion;

    public RespuestaEvaluacion() {
    }

    public RespuestaEvaluacion(
            int idEvaluacion,
            int idCriterio,
            String cumplimiento,
            String observacion) {

        this.idEvaluacion = idEvaluacion;
        this.idCriterio = idCriterio;
        this.cumplimiento = cumplimiento;
        this.observacion = observacion;
    }

    public int getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(int idRespuesta) {
        this.idRespuesta = idRespuesta;
    }

    public int getIdEvaluacion() {
        return idEvaluacion;
    }

    public void setIdEvaluacion(int idEvaluacion) {
        this.idEvaluacion = idEvaluacion;
    }

    public int getIdCriterio() {
        return idCriterio;
    }

    public void setIdCriterio(int idCriterio) {
        this.idCriterio = idCriterio;
    }

    public String getCumplimiento() {
        return cumplimiento;
    }

    public void setCumplimiento(String cumplimiento) {
        this.cumplimiento = cumplimiento;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
