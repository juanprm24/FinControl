package com.example.fincontrol.models;


import java.util.Date;

public class Transaccion {
    private int idTransaccion;
    private double monto;
    private String descripcion;
    private Date fecha;
    private String tipo; // "Gasto" o "Ingreso"
    private int idCategoria;
    private String nombreCategoria; // Para mostrar en la lista

    // Constructor vacío
    public Transaccion() {
    }

    // Constructor completo
    public Transaccion(int idTransaccion, double monto, String descripcion,
                       Date fecha, String tipo, int idCategoria) {
        this.idTransaccion = idTransaccion;
        this.monto = monto;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.tipo = tipo;
        this.idCategoria = idCategoria;
    }

    // Constructor sin ID (para insertar)
    public Transaccion(double monto, String descripcion, Date fecha,
                       String tipo, int idCategoria) {
        this.monto = monto;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.tipo = tipo;
        this.idCategoria = idCategoria;
    }

    // Getters y Setters
    public int getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(int idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }
}
