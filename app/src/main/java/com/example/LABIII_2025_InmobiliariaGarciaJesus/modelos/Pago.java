package com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Pago implements Serializable {
    @SerializedName("id")
    private int id;
    
    @SerializedName("numero")
    private int numero;
    
    @SerializedName("fechaPago")
    private String fechaPago;
    
    @SerializedName("contratoId")
    private int contratoId;
    
    @SerializedName("importe")
    private double importe;
    
    @SerializedName("intereses")
    private double intereses;
    
    @SerializedName("multas")
    private double multas;
    
    @SerializedName("totalAPagar")
    private double totalAPagar;
    
    @SerializedName("fechaVencimiento")
    private String fechaVencimiento;
    
    @SerializedName("estado")
    private String estado;
    
    @SerializedName("metodoPago")
    private String metodoPago;
    
    @SerializedName("observaciones")
    private String observaciones;
    
    @SerializedName("fechaCreacion")
    private String fechaCreacion;

    public Pago() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public int getContratoId() {
        return contratoId;
    }

    public void setContratoId(int contratoId) {
        this.contratoId = contratoId;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public double getIntereses() {
        return intereses;
    }

    public void setIntereses(double intereses) {
        this.intereses = intereses;
    }

    public double getMultas() {
        return multas;
    }

    public void setMultas(double multas) {
        this.multas = multas;
    }

    public double getTotalAPagar() {
        return totalAPagar;
    }

    public void setTotalAPagar(double totalAPagar) {
        this.totalAPagar = totalAPagar;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return "Pago{" +
                "id=" + id +
                ", numero=" + numero +
                ", contratoId=" + contratoId +
                ", importe=" + importe +
                ", totalAPagar=" + totalAPagar +
                ", fechaVencimiento='" + fechaVencimiento + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
