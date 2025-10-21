package com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Inmueble implements Serializable {
    @SerializedName("id")
    private int id;
    
    @SerializedName("direccion")
    private String direccion;
    
    @SerializedName("localidad")
    private String localidad;
    
    @SerializedName("provincia")
    private String provincia;
    
    @SerializedName("tipoId")
    private int tipoId;
    
    @SerializedName("tipoNombre")
    private String tipoNombre;
    
    @SerializedName("ambientes")
    private int ambientes;
    
    @SerializedName("superficie")
    private Double superficie;
    
    @SerializedName("latitud")
    private Double latitud;
    
    @SerializedName("longitud")
    private Double longitud;
    
    @SerializedName("disponible")
    private boolean disponible;
    
    @SerializedName("disponibilidad")
    private String disponibilidad;
    
    @SerializedName("precio")
    private Double precio;
    
    @SerializedName("estado")
    private String estado;
    
    @SerializedName("uso")
    private String uso;
    
    @SerializedName("fechaCreacion")
    private String fechaCreacion;
    
    @SerializedName("imagenPortadaUrl")
    private String imagenPortadaUrl;
    
    @SerializedName("imagenes")
    private List<InmuebleImagen> imagenes;

    public Inmueble() {
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

    public int getTipoId() {
        return tipoId;
    }

    public void setTipoId(int tipoId) {
        this.tipoId = tipoId;
    }

    public String getTipoNombre() {
        return tipoNombre;
    }

    public void setTipoNombre(String tipoNombre) {
        this.tipoNombre = tipoNombre;
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

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
    public String getDisponibilidad() {
        return disponibilidad;
    }
    
    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUso() {
        return uso;
    }

    public void setUso(String uso) {
        this.uso = uso;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getImagenPortadaUrl() {
        return imagenPortadaUrl;
    }

    public void setImagenPortadaUrl(String imagenPortadaUrl) {
        this.imagenPortadaUrl = imagenPortadaUrl;
    }

    public List<InmuebleImagen> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<InmuebleImagen> imagenes) {
        this.imagenes = imagenes;
    }

    @Override
    public String toString() {
        return "Inmueble{" +
                "id=" + id +
                ", direccion='" + direccion + '\'' +
                ", localidad='" + localidad + '\'' +
                ", provincia='" + provincia + '\'' +
                ", tipoNombre='" + tipoNombre + '\'' +
                ", ambientes=" + ambientes +
                ", precio=" + precio +
                ", estado='" + estado + '\'' +
                ", disponible=" + disponible +
                '}';
    }
}
