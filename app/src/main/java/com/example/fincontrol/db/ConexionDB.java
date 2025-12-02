package com.example.fincontrol.db;


import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión directa a MySQL desde Android
 */
public class ConexionDB {

    private static final String TAG = "ConexionDB";

    // Configuración de la base de datos
    private static final String HOST = "192.168.1.75"; // Cambia por tu IP del servidor MySQL
    private static final String PUERTO = "3306";
    private static final String DATABASE = "fincontrol_db";
    private static final String USER = "root"; // Cambia por tu usuario
    private static final String PASSWORD = "n0m3l0"; // Cambia por tu contraseña

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + DATABASE;

    private Connection conexion;

    /**
     * Constructor que inicializa la política de red para permitir operaciones en el hilo principal
     * NOTA: En producción, todas las operaciones de BD deben ir en hilos secundarios
     */
    public ConexionDB() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    /**
     * Abre una conexión a la base de datos MySQL
     * @return Connection objeto de conexión
     */
    public Connection abrirConexion() {
        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.jdbc.Driver");

            // Establecer la conexión
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);

            Log.i(TAG, "Conexión a MySQL establecida correctamente");
            return conexion;

        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Error: No se encontró el driver de MySQL", e);
            return null;
        } catch (SQLException e) {
            Log.e(TAG, "Error al conectar con MySQL: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Cierra la conexión a la base de datos
     */
    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                Log.i(TAG, "Conexión cerrada correctamente");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error al cerrar la conexión", e);
        }
    }

    /**
     * Verifica si la conexión está activa
     * @return true si está conectado, false en caso contrario
     */
    public boolean estaConectado() {
        try {
            return conexion != null && !conexion.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Obtiene la conexión actual
     * @return Connection objeto de conexión
     */
    public Connection getConexion() {
        return conexion;
    }
}
