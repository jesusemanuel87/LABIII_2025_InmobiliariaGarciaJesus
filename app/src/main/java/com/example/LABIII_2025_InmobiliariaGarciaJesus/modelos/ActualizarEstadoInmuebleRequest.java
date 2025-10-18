package com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ActualizarEstadoInmuebleRequest implements Serializable {
    @SerializedName("disponible")
    private boolean disponible;

    public ActualizarEstadoInmuebleRequest() {
    }

    public ActualizarEstadoInmuebleRequest(boolean disponible) {
        this.disponible = disponible;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return "ActualizarEstadoInmuebleRequest{" +
                "disponible=" + disponible +
                '}';
    }
}
