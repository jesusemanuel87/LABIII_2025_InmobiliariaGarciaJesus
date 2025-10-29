package com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class TipoInmueble implements Serializable {
    @SerializedName("id")
    private int id;
    
    @SerializedName("nombre")
    private String nombre;
    
    @SerializedName("descripcion")
    private String descripcion;

    public TipoInmueble() {
    }

    public TipoInmueble(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public String toString() {
        return nombre; // Para mostrar en Spinner
    }
}
