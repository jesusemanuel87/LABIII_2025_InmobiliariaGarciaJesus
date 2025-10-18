package com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class LoginResponse implements Serializable {
    @SerializedName("token")
    private String token;
    
    @SerializedName("propietario")
    private Propietario propietario;
    
    @SerializedName("expiracion")
    private String expiracion;

    public LoginResponse() {
    }

    public LoginResponse(String token, Propietario propietario, String expiracion) {
        this.token = token;
        this.propietario = propietario;
        this.expiracion = expiracion;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Propietario getPropietario() {
        return propietario;
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }

    public String getExpiracion() {
        return expiracion;
    }

    public void setExpiracion(String expiracion) {
        this.expiracion = expiracion;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", propietario=" + propietario +
                ", expiracion='" + expiracion + '\'' +
                '}';
    }
}
