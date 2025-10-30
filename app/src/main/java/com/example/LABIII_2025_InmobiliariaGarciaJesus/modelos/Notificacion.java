package com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos;

public class Notificacion {
    private int id;
    private String tipo;
    private String titulo;
    private String mensaje;
    private String datos;
    private boolean leida;
    private String fechaCreacion;
    private String fechaLeida;

    // Constructor vacío
    public Notificacion() {
    }

    // Constructor completo
    public Notificacion(int id, String tipo, String titulo, String mensaje, String datos, 
                       boolean leida, String fechaCreacion, String fechaLeida) {
        this.id = id;
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.datos = datos;
        this.leida = leida;
        this.fechaCreacion = fechaCreacion;
        this.fechaLeida = fechaLeida;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getDatos() {
        return datos;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaLeida() {
        return fechaLeida;
    }

    public void setFechaLeida(String fechaLeida) {
        this.fechaLeida = fechaLeida;
    }
}
