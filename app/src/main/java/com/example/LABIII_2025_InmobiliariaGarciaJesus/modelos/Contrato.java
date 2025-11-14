package com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Contrato implements Serializable {
    @SerializedName("id")
    private int id;
    
    @SerializedName("fechaInicio")
    private String fechaInicio;
    
    @SerializedName("fechaFin")
    private String fechaFin;
    
    @SerializedName("precio")
    private double precio;
    
    @SerializedName("estado")
    private String estado;
    
    @SerializedName("fechaCreacion")
    private String fechaCreacion;
    
    @SerializedName("motivoCancelacion")
    private String motivoCancelacion;
    
    @SerializedName("fechaFinalizacionReal")
    private String fechaFinalizacionReal;
    
    @SerializedName("multaFinalizacion")
    private Double multaFinalizacion;
    
    @SerializedName("mesesAdeudados")
    private Integer mesesAdeudados;
    
    @SerializedName("importeAdeudado")
    private Double importeAdeudado;
    
    @SerializedName("inmueble")
    private Inmueble inmueble;
    
    @SerializedName("inquilino")
    private InquilinoContrato inquilino;
    
    @SerializedName("pagos")
    private List<Pago> pagos;

    public Contrato() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    public String getFechaFinalizacionReal() {
        return fechaFinalizacionReal;
    }

    public void setFechaFinalizacionReal(String fechaFinalizacionReal) {
        this.fechaFinalizacionReal = fechaFinalizacionReal;
    }

    public Double getMultaFinalizacion() {
        return multaFinalizacion;
    }

    public void setMultaFinalizacion(Double multaFinalizacion) {
        this.multaFinalizacion = multaFinalizacion;
    }

    public Integer getMesesAdeudados() {
        return mesesAdeudados;
    }

    public void setMesesAdeudados(Integer mesesAdeudados) {
        this.mesesAdeudados = mesesAdeudados;
    }

    public Double getImporteAdeudado() {
        return importeAdeudado;
    }

    public void setImporteAdeudado(Double importeAdeudado) {
        this.importeAdeudado = importeAdeudado;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public InquilinoContrato getInquilino() {
        return inquilino;
    }

    public void setInquilino(InquilinoContrato inquilino) {
        this.inquilino = inquilino;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    @Override
    public String toString() {
        return "Contrato{" +
                "id=" + id +
                ", fechaInicio='" + fechaInicio + '\'' +
                ", fechaFin='" + fechaFin + '\'' +
                ", precio=" + precio +
                ", estado='" + estado + '\'' +
                ", inmueble=" + inmueble +
                ", inquilino=" + inquilino +
                ", pagos=" + (pagos != null ? pagos.size() : 0) + " pagos" +
                '}';
    }
}
