package com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos;

public class DatosNotificacion {
    private Integer pagoId;
    private Integer contratoId;
    private Integer inmuebleId;
    private Double monto;
    private Integer numeroCuota;

    // Constructor vacío
    public DatosNotificacion() {
    }

    // Constructor completo
    public DatosNotificacion(Integer pagoId, Integer contratoId, Integer inmuebleId, 
                            Double monto, Integer numeroCuota) {
        this.pagoId = pagoId;
        this.contratoId = contratoId;
        this.inmuebleId = inmuebleId;
        this.monto = monto;
        this.numeroCuota = numeroCuota;
    }

    // Getters y Setters
    public Integer getPagoId() {
        return pagoId;
    }

    public void setPagoId(Integer pagoId) {
        this.pagoId = pagoId;
    }

    public Integer getContratoId() {
        return contratoId;
    }

    public void setContratoId(Integer contratoId) {
        this.contratoId = contratoId;
    }

    public Integer getInmuebleId() {
        return inmuebleId;
    }

    public void setInmuebleId(Integer inmuebleId) {
        this.inmuebleId = inmuebleId;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public Integer getNumeroCuota() {
        return numeroCuota;
    }

    public void setNumeroCuota(Integer numeroCuota) {
        this.numeroCuota = numeroCuota;
    }
}
