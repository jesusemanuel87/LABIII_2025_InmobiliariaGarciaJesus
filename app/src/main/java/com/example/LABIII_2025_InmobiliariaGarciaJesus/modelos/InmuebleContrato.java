package com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class InmuebleContrato implements Serializable {
    @SerializedName("id")
    private int id;
    
    @SerializedName("direccion")
    private String direccion;
    
    @SerializedName("localidad")
    private String localidad;
    
    @SerializedName("provincia")
    private String provincia;
    
    @SerializedName("ambientes")
    private int ambientes;
    
    @SerializedName("imagenPortadaUrl")
    private String imagenPortadaUrl;

    public InmuebleContrato() {
    }

    public InmuebleContrato(int id, String direccion, String localidad, 
                            String provincia, int ambientes, String imagenPortadaUrl) {
        this.id = id;
        this.direccion = direccion;
        this.localidad = localidad;
        this.provincia = provincia;
        this.ambientes = ambientes;
        this.imagenPortadaUrl = imagenPortadaUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public int getAmbientes() {
        return ambientes;
    }

    public void setAmbientes(int ambientes) {
        this.ambientes = ambientes;
    }

    public String getImagenPortadaUrl() {
        return imagenPortadaUrl;
    }

    public void setImagenPortadaUrl(String imagenPortadaUrl) {
        this.imagenPortadaUrl = imagenPortadaUrl;
    }

    @Override
    public String toString() {
        return "InmuebleContrato{" +
                "id=" + id +
                ", direccion='" + direccion + '\'' +
                ", localidad='" + localidad + '\'' +
                ", provincia='" + provincia + '\'' +
                ", ambientes=" + ambientes +
                '}';
    }
}
