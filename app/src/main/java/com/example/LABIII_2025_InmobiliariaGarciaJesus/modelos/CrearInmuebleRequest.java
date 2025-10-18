package com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class CrearInmuebleRequest implements Serializable {
    @SerializedName("direccion")
    private String direccion;
    
    @SerializedName("localidad")
    private String localidad;
    
    @SerializedName("provincia")
    private String provincia;
    
    @SerializedName("tipoId")
    private int tipoId;
    
    @SerializedName("ambientes")
    private int ambientes;
    
    @SerializedName("superficie")
    private Double superficie;
    
    @SerializedName("latitud")
    private Double latitud;
    
    @SerializedName("longitud")
    private Double longitud;
    
    @SerializedName("precio")
    private double precio;
    
    @SerializedName("uso")
    private int uso; // 0=Residencial, 1=Comercial, 2=Industrial
    
    @SerializedName("imagenBase64")
    private String imagenBase64;
    
    @SerializedName("imagenNombre")
    private String imagenNombre;

    public CrearInmuebleRequest() {
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

    public int getTipoId() {
        return tipoId;
    }

    public void setTipoId(int tipoId) {
        this.tipoId = tipoId;
    }

    public int getAmbientes() {
        return ambientes;
    }

    public void setAmbientes(int ambientes) {
        this.ambientes = ambientes;
    }

    public Double getSuperficie() {
        return superficie;
    }

    public void setSuperficie(Double superficie) {
        this.superficie = superficie;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getUso() {
        return uso;
    }

    public void setUso(int uso) {
        this.uso = uso;
    }

    public String getImagenBase64() {
        return imagenBase64;
    }

    public void setImagenBase64(String imagenBase64) {
        this.imagenBase64 = imagenBase64;
    }

    public String getImagenNombre() {
        return imagenNombre;
    }

    public void setImagenNombre(String imagenNombre) {
        this.imagenNombre = imagenNombre;
    }

    @Override
    public String toString() {
        return "CrearInmuebleRequest{" +
                "direccion='" + direccion + '\'' +
                ", localidad='" + localidad + '\'' +
                ", provincia='" + provincia + '\'' +
                ", ambientes=" + ambientes +
                ", precio=" + precio +
                ", uso=" + uso +
                '}';
    }
}
