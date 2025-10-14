package com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

public class Propietarios implements Serializable{
    @SerializedName("idPropietario")
    private int idPropietario;
    
    @SerializedName("nombre")
    private String nombre;
    
    @SerializedName("apellido")
    private String apellido;
    
    @SerializedName("dni")
    private String dni;
    
    @SerializedName("telefono")
    private String telefono;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("clave")
    private String clave;

    // Constructor vacío (requerido por Gson)
    public Propietarios() {
    }

    // Constructor con parámetros
    public Propietarios(int idPropietario, String nombre, String apellido, String dni, String telefono, String email, String clave) {
        this.idPropietario = idPropietario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
        this.clave = clave;
    }

    public int getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(int idPropietario) {
        this.idPropietario = idPropietario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
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

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    @Override
    public String toString() {
        return "Propietarios{" +
                "idPropietario=" + idPropietario +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", dni='" + dni + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
