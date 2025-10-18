package com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class CambiarPasswordRequest implements Serializable {
    @SerializedName("passwordActual")
    private String passwordActual;
    
    @SerializedName("passwordNueva")
    private String passwordNueva;
    
    @SerializedName("passwordConfirmacion")
    private String passwordConfirmacion;

    public CambiarPasswordRequest() {
    }

    public CambiarPasswordRequest(String passwordActual, String passwordNueva, String passwordConfirmacion) {
        this.passwordActual = passwordActual;
        this.passwordNueva = passwordNueva;
        this.passwordConfirmacion = passwordConfirmacion;
    }

    public String getPasswordActual() {
        return passwordActual;
    }

    public void setPasswordActual(String passwordActual) {
        this.passwordActual = passwordActual;
    }

    public String getPasswordNueva() {
        return passwordNueva;
    }

    public void setPasswordNueva(String passwordNueva) {
        this.passwordNueva = passwordNueva;
    }

    public String getPasswordConfirmacion() {
        return passwordConfirmacion;
    }

    public void setPasswordConfirmacion(String passwordConfirmacion) {
        this.passwordConfirmacion = passwordConfirmacion;
    }

    @Override
    public String toString() {
        return "CambiarPasswordRequest{" +
                "passwordActual='***'" +
                ", passwordNueva='***'" +
                ", passwordConfirmacion='***'" +
                '}';
    }
}
