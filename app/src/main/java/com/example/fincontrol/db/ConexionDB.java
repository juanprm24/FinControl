package com.example.fincontrol.db;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión directa a MySQL desde Android
 * MODIFICADO: Ya no almacena la conexión globalmente.
 */
public class ConexionDB {

    private static final String TAG = "ConexionDB";

    // Configuración de la base de datos
    private static final String HOST = "192.168.1.75"; // Asegúrate que esta IP sea accesible (o usa 10.0.2.2 para emulador)
    private static final String PUERTO = "3306";
    private static final String DATABASE = "fincontrol_db";
    private static final String USER = "root"; // Cambia por tu usuario
    private static final String PASSWORD = "n0m3l0"; // Cambia por tu contraseña

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + DATABASE + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false";

    // NOTA: Se eliminó 'private Connection conexion;' para evitar conflictos multihilo.

    /**
     * Constructor que inicializa la política de red para permitir operaciones en el hilo principal
     * NOTA: En producción, todas las operaciones de BD deben ir en hilos secundarios (lo cual ya haces).
     */
    public ConexionDB() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    /**
     * Abre una conexión a la base de datos MySQL
     * @return Connection objeto de conexión nueva e independiente
     */
    public Connection abrirConexion() {
        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.jdbc.Driver");

            // Establecer la conexión
            Connection newConnection = DriverManager.getConnection(URL, USER, PASSWORD);

            Log.i(TAG, "Conexión a MySQL establecida correctamente");
            return newConnection;

        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Error: No se encontró el driver de MySQL", e);
            return null;
        } catch (SQLException e) {
            // Error al conectar (esto es lo que pasaba con el permiso del Host)
            Log.e(TAG, "Error al conectar con MySQL: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Cierra la conexión a la base de datos recibida como parámetro
     */
    public void cerrarConexion(Connection conn) { // <--- MODIFICACIÓN CLAVE
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                Log.i(TAG, "Conexión cerrada correctamente");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error al cerrar la conexión", e);
        }
    }

    // NOTA: Se eliminaron los métodos estaConectado() y getConexion() porque ya no son necesarios
    // al no tener una conexión global.
}