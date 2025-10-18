package com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ResetPasswordResponse implements Serializable {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("nuevaPassword")
    private String nuevaPassword;

    public ResetPasswordResponse() {
    }

    public ResetPasswordResponse(boolean success, String message, String nuevaPassword) {
        this.success = success;
        this.message = message;
        this.nuevaPassword = nuevaPassword;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNuevaPassword() {
        return nuevaPassword;
    }

    public void setNuevaPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }

    @Override
    public String toString() {
        return "ResetPasswordResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", nuevaPassword='" + (nuevaPassword != null ? "***" : null) + '\'' +
                '}';
    }
}
