package com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class InmuebleImagen implements Serializable {
    @SerializedName("id")
    private int id;
    
    @SerializedName("nombreArchivo")
    private String nombreArchivo;
    
    @SerializedName("rutaCompleta")
    private String rutaCompleta;
    
    @SerializedName("esPortada")
    private boolean esPortada;
    
    @SerializedName("descripcion")
    private String descripcion;

    public InmuebleImagen() {
    }

    public InmuebleImagen(int id, String nombreArchivo, String rutaCompleta, 
                          boolean esPortada, String descripcion) {
        this.id = id;
        this.nombreArchivo = nombreArchivo;
        this.rutaCompleta = rutaCompleta;
        this.esPortada = esPortada;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getRutaCompleta() {
        return rutaCompleta;
    }

    public void setRutaCompleta(String rutaCompleta) {
        this.rutaCompleta = rutaCompleta;
    }

    public boolean isEsPortada() {
        return esPortada;
    }

    public void setEsPortada(boolean esPortada) {
        this.esPortada = esPortada;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "InmuebleImagen{" +
                "id=" + id +
                ", nombreArchivo='" + nombreArchivo + '\'' +
                ", esPortada=" + esPortada +
                '}';
    }
}
