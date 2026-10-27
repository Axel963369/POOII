
package com.poo2.modelo;

import java.time.LocalDateTime;

public class Evaluacion {
    private int idEvaluacion;
    private int idProyecto;
    private int idUsuario;
    private LocalDateTime fechaEvaluacion;
    private double porcentajeCumplimiento;
    private String nivelCalidad;
    private String estado;
    private String nombreProyecto;
    private String nombreUsuario;

    public Evaluacion() {
        this.fechaEvaluacion = LocalDateTime.now();
        this.porcentajeCumplimiento = 0.0;
        this.nivelCalidad = "PENDIENTE";
        this.estado = "EN_PROCESO";
    }

    public Evaluacion(int idProyecto, int idUsuario) {
        this.idProyecto = idProyecto;
        this.idUsuario = idUsuario;
        this.fechaEvaluacion = LocalDateTime.now();
        this.porcentajeCumplimiento = 0.0;
        this.nivelCalidad = "PENDIENTE";
        this.estado = "EN_PROCESO";
    }

    public int getIdEvaluacion() {
        return idEvaluacion;
    }

    public void setIdEvaluacion(int idEvaluacion) {
        this.idEvaluacion = idEvaluacion;
    }

    public int getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void setFechaEvaluacion(LocalDateTime fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public double getPorcentajeCumplimiento() {
        return porcentajeCumplimiento;
    }

    public void setPorcentajeCumplimiento(double porcentajeCumplimiento) {
        this.porcentajeCumplimiento = porcentajeCumplimiento;
    }

    public String getNivelCalidad() {
        return nivelCalidad;
    }

    public void setNivelCalidad(String nivelCalidad) {
        this.nivelCalidad = nivelCalidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getNombreProyecto() {
        return nombreProyecto;
    }
    
    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }
    
    public String getNombreUsuario() {
        return nombreUsuario;
    }
    
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}
