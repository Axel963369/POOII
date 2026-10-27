
package com.poo2.modelo;

import java.time.LocalDateTime;

public class Proyecto {
    private int idProyecto;
    private String nombre;
    private String descripcion;
    private String tecnologia;
    private String estado;
    private int idUsuario;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    
    public Proyecto() {
    }

    // Constructor para registrar un proyecto nuevo
    public Proyecto(String nombre, String descripcion,
                    String tecnologia, int idUsuario) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tecnologia = tecnologia;
        this.idUsuario = idUsuario;
        this.estado = "REGISTRADO";
    }

    // Getters y setters

    public int getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTecnologia() {
        return tecnologia;
    }

    public void setTecnologia(String tecnologia) {
        this.tecnologia = tecnologia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(
            LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
