package com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class InquilinoContrato implements Serializable {
    @SerializedName("id")
    private int id;
    
    @SerializedName("nombreCompleto")
    private String nombreCompleto;
    
    @SerializedName("dni")
    private String dni;
    
    @SerializedName("telefono")
    private String telefono;
    
    @SerializedName("email")
    private String email;

    public InquilinoContrato() {
    }

    public InquilinoContrato(int id, String nombreCompleto, String dni, 
                             String telefono, String email) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "InquilinoContrato{" +
                "id=" + id +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", dni='" + dni + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
