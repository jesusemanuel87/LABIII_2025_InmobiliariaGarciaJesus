package com.example.LABIII_2025_InmobiliariaGarciaJesus.request;

import com.google.gson.annotations.SerializedName;

public class CambiarPasswordRequest {
    
    @SerializedName("passwordActual")
    private String passwordActual;
    
    @SerializedName("passwordNueva")
    private String passwordNueva;

    public CambiarPasswordRequest(String passwordActual, String passwordNueva) {
        this.passwordActual = passwordActual;
        this.passwordNueva = passwordNueva;
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
}
