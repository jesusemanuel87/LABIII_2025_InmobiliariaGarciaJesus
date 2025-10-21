package com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ActualizarEstadoInmuebleRequest implements Serializable {
    @SerializedName("estado")
    private String estado;

    public ActualizarEstadoInmuebleRequest() {
    }

    public ActualizarEstadoInmuebleRequest(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "ActualizarEstadoInmuebleRequest{" +
                "estado='" + estado + '\'' +
                '}';
    }
}
