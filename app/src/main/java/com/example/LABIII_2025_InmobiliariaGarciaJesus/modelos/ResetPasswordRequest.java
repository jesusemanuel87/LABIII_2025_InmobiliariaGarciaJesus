package com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ResetPasswordRequest implements Serializable {
    @SerializedName("email")
    private String email;
    
    @SerializedName("dni")
    private String dni;

    public ResetPasswordRequest() {
    }

    public ResetPasswordRequest(String email, String dni) {
        this.email = email;
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    @Override
    public String toString() {
        return "ResetPasswordRequest{" +
                "email='" + email + '\'' +
                ", dni='" + dni + '\'' +
                '}';
    }
}
