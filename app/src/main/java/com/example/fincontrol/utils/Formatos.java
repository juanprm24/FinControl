package com.example.fincontrol.utils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Clase utilitaria para formatear números, fechas y monedas
 */
public class Formatos {

    /**
     * Formatea un número double como moneda
     * @param monto Cantidad a formatear
     * @return String con formato de moneda (ej: $1,234.56)
     */
    public static String formatearMoneda(double monto) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
        return formato.format(monto);
    }

    /**
     * Formatea una fecha en formato corto
     * @param fecha Objeto Date
     * @return String con formato dd/MM/yyyy
     */
    public static String formatearFecha(Date fecha) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return formato.format(fecha);
    }

    /**
     * Formatea una fecha en formato largo
     * @param fecha Objeto Date
     * @return String con formato dd MMM yyyy (ej: 15 Dic 2024)
     */
    public static String formatearFechaLarga(Date fecha) {
        SimpleDateFormat formato = new SimpleDateFormat("dd MMM yyyy", new Locale("es", "MX"));
        return formato.format(fecha);
    }

    /**
     * Formatea una fecha con hora
     * @param fecha Objeto Date
     * @return String con formato dd/MM/yyyy HH:mm
     */
    public static String formatearFechaHora(Date fecha) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return formato.format(fecha);
    }

    /**
     * Formatea un número con separadores de miles
     * @param numero Número a formatear
     * @return String con formato #,###.##
     */
    public static String formatearNumero(double numero) {
        NumberFormat formato = NumberFormat.getNumberInstance(Locale.getDefault());
        formato.setMaximumFractionDigits(2);
        formato.setMinimumFractionDigits(2);
        return formato.format(numero);
    }
}
