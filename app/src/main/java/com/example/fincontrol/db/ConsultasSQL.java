package com.example.fincontrol.db;

import android.util.Log;


import com.example.fincontrol.models.Categoria;
import com.example.fincontrol.models.Transaccion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que contiene todos los métodos CRUD para la base de datos
 */
public class ConsultasSQL {

    private static final String TAG = "ConsultasSQL";
    private ConexionDB conexionDB;

    public ConsultasSQL() {
        this.conexionDB = new ConexionDB();
    }

    // ==================== CRUD CATEGORÍAS ====================

    /**
     * Inserta una nueva categoría en la base de datos
     * @param categoria Objeto Categoria a insertar
     * @return true si se insertó correctamente, false en caso contrario
     */
    public boolean insertarCategoria(Categoria categoria) {
        Connection conn = conexionDB.abrirConexion();
        if (conn == null) return false;

        String sql = "INSERT INTO categorias (nombre, tipo) VALUES (?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getTipo());

            int resultado = ps.executeUpdate();
            ps.close();
            conexionDB.cerrarConexion();

            Log.i(TAG, "Categoría insertada correctamente");
            return resultado > 0;

        } catch (SQLException e) {
            Log.e(TAG, "Error al insertar categoría", e);
            conexionDB.cerrarConexion();
            return false;
        }
    }

    /**
     * Actualiza una categoría existente
     * @param categoria Objeto Categoria con los datos actualizados
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public boolean actualizarCategoria(Categoria categoria) {
        Connection conn = conexionDB.abrirConexion();
        if (conn == null) return false;

        String sql = "UPDATE categorias SET nombre = ?, tipo = ? WHERE id_categoria = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getTipo());
            ps.setInt(3, categoria.getIdCategoria());

            int resultado = ps.executeUpdate();
            ps.close();
            conexionDB.cerrarConexion();

            Log.i(TAG, "Categoría actualizada correctamente");
            return resultado > 0;

        } catch (SQLException e) {
            Log.e(TAG, "Error al actualizar categoría", e);
            conexionDB.cerrarConexion();
            return false;
        }
    }

    /**
     * Elimina una categoría de la base de datos
     * @param idCategoria ID de la categoría a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarCategoria(int idCategoria) {
        Connection conn = conexionDB.abrirConexion();
        if (conn == null) return false;

        String sql = "DELETE FROM categorias WHERE id_categoria = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCategoria);

            int resultado = ps.executeUpdate();
            ps.close();
            conexionDB.cerrarConexion();

            Log.i(TAG, "Categoría eliminada correctamente");
            return resultado > 0;

        } catch (SQLException e) {
            Log.e(TAG, "Error al eliminar categoría", e);
            conexionDB.cerrarConexion();
            return false;
        }
    }

    /**
     * Obtiene todas las categorías de la base de datos
     * @return Lista de categorías
     */
    public List<Categoria> obtenerCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        Connection conn = conexionDB.abrirConexion();
        if (conn == null) return categorias;

        String sql = "SELECT * FROM categorias ORDER BY nombre";

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Categoria cat = new Categoria();
                cat.setIdCategoria(rs.getInt("id_categoria"));
                cat.setNombre(rs.getString("nombre"));
                cat.setTipo(rs.getString("tipo"));
                categorias.add(cat);
            }

            rs.close();
            st.close();
            conexionDB.cerrarConexion();

            Log.i(TAG, "Se obtuvieron " + categorias.size() + " categorías");

        } catch (SQLException e) {
            Log.e(TAG, "Error al obtener categorías", e);
            conexionDB.cerrarConexion();
        }

        return categorias;
    }

    /**
     * Obtiene categorías filtradas por tipo
     * @param tipo "Gasto" o "Ingreso"
     * @return Lista de categorías del tipo especificado
     */
    public List<Categoria> obtenerCategoriasPorTipo(String tipo) {
        List<Categoria> categorias = new ArrayList<>();
        Connection conn = conexionDB.abrirConexion();
        if (conn == null) return categorias;

        String sql = "SELECT * FROM categorias WHERE tipo = ? ORDER BY nombre";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tipo);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Categoria cat = new Categoria();
                cat.setIdCategoria(rs.getInt("id_categoria"));
                cat.setNombre(rs.getString("nombre"));
                cat.setTipo(rs.getString("tipo"));
                categorias.add(cat);
            }

            rs.close();
            ps.close();
            conexionDB.cerrarConexion();

        } catch (SQLException e) {
            Log.e(TAG, "Error al obtener categorías por tipo", e);
            conexionDB.cerrarConexion();
        }

        return categorias;
    }

    // ==================== CRUD TRANSACCIONES ====================

    /**
     * Inserta una nueva transacción en la base de datos
     * @param transaccion Objeto Transaccion a insertar
     * @return true si se insertó correctamente, false en caso contrario
     */
    public boolean insertarTransaccion(Transaccion transaccion) {
        Connection conn = conexionDB.abrirConexion();
        if (conn == null) return false;

        String sql = "INSERT INTO transacciones (monto, descripcion, fecha, tipo, id_categoria) " +
                "VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, transaccion.getMonto());
            ps.setString(2, transaccion.getDescripcion());
            ps.setTimestamp(3, new Timestamp(transaccion.getFecha().getTime()));
            ps.setString(4, transaccion.getTipo());
            ps.setInt(5, transaccion.getIdCategoria());

            int resultado = ps.executeUpdate();
            ps.close();
            conexionDB.cerrarConexion();

            Log.i(TAG, "Transacción insertada correctamente");
            return resultado > 0;

        } catch (SQLException e) {
            Log.e(TAG, "Error al insertar transacción", e);
            conexionDB.cerrarConexion();
            return false;
        }
    }

    /**
     * Actualiza una transacción existente
     * @param transaccion Objeto Transaccion con los datos actualizados
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public boolean actualizarTransaccion(Transaccion transaccion) {
        Connection conn = conexionDB.abrirConexion();
        if (conn == null) return false;

        String sql = "UPDATE transacciones SET monto = ?, descripcion = ?, fecha = ?, " +
                "tipo = ?, id_categoria = ? WHERE id_transaccion = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, transaccion.getMonto());
            ps.setString(2, transaccion.getDescripcion());
            ps.setTimestamp(3, new Timestamp(transaccion.getFecha().getTime()));
            ps.setString(4, transaccion.getTipo());
            ps.setInt(5, transaccion.getIdCategoria());
            ps.setInt(6, transaccion.getIdTransaccion());

            int resultado = ps.executeUpdate();
            ps.close();
            conexionDB.cerrarConexion();

            Log.i(TAG, "Transacción actualizada correctamente");
            return resultado > 0;

        } catch (SQLException e) {
            Log.e(TAG, "Error al actualizar transacción", e);
            conexionDB.cerrarConexion();
            return false;
        }
    }

    /**
     * Elimina una transacción de la base de datos
     * @param idTransaccion ID de la transacción a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarTransaccion(int idTransaccion) {
        Connection conn = conexionDB.abrirConexion();
        if (conn == null) return false;

        String sql = "DELETE FROM transacciones WHERE id_transaccion = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idTransaccion);

            int resultado = ps.executeUpdate();
            ps.close();
            conexionDB.cerrarConexion();

            Log.i(TAG, "Transacción eliminada correctamente");
            return resultado > 0;

        } catch (SQLException e) {
            Log.e(TAG, "Error al eliminar transacción", e);
            conexionDB.cerrarConexion();
            return false;
        }
    }

    /**
     * Obtiene todas las transacciones de la base de datos
     * @return Lista de transacciones con nombre de categoría incluido
     */
    public List<Transaccion> obtenerTransacciones() {
        List<Transaccion> transacciones = new ArrayList<>();
        Connection conn = conexionDB.abrirConexion();
        if (conn == null) return transacciones;

        String sql = "SELECT t.*, c.nombre as nombre_categoria " +
                "FROM transacciones t " +
                "LEFT JOIN categorias c ON t.id_categoria = c.id_categoria " +
                "ORDER BY t.fecha DESC";

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Transaccion trans = new Transaccion();
                trans.setIdTransaccion(rs.getInt("id_transaccion"));
                trans.setMonto(rs.getDouble("monto"));
                trans.setDescripcion(rs.getString("descripcion"));
                trans.setFecha(rs.getTimestamp("fecha"));
                trans.setTipo(rs.getString("tipo"));
                trans.setIdCategoria(rs.getInt("id_categoria"));
                trans.setNombreCategoria(rs.getString("nombre_categoria"));
                transacciones.add(trans);
            }

            rs.close();
            st.close();
            conexionDB.cerrarConexion();

            Log.i(TAG, "Se obtuvieron " + transacciones.size() + " transacciones");

        } catch (SQLException e) {
            Log.e(TAG, "Error al obtener transacciones", e);
            conexionDB.cerrarConexion();
        }

        return transacciones;
    }

    /**
     * Obtiene el total de ingresos del mes actual
     * @return Suma de todos los ingresos del mes
     */
    public double obtenerIngresosMes() {
        double total = 0;
        Connection conn = conexionDB.abrirConexion();
        if (conn == null) return total;

        String sql = "SELECT SUM(monto) as total FROM transacciones " +
                "WHERE tipo = 'Ingreso' " +
                "AND MONTH(fecha) = MONTH(CURDATE()) " +
                "AND YEAR(fecha) = YEAR(CURDATE())";

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                total = rs.getDouble("total");
            }

            rs.close();
            st.close();
            conexionDB.cerrarConexion();

        } catch (SQLException e) {
            Log.e(TAG, "Error al obtener ingresos del mes", e);
            conexionDB.cerrarConexion();
        }

        return total;
    }

    /**
     * Obtiene el total de gastos del mes actual
     * @return Suma de todos los gastos del mes
     */
    public double obtenerGastosMes() {
        double total = 0;
        Connection conn = conexionDB.abrirConexion();
        if (conn == null) return total;

        String sql = "SELECT SUM(monto) as total FROM transacciones " +
                "WHERE tipo = 'Gasto' " +
                "AND MONTH(fecha) = MONTH(CURDATE()) " +
                "AND YEAR(fecha) = YEAR(CURDATE())";

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                total = rs.getDouble("total");
            }

            rs.close();
            st.close();
            conexionDB.cerrarConexion();

        } catch (SQLException e) {
            Log.e(TAG, "Error al obtener gastos del mes", e);
            conexionDB.cerrarConexion();
        }

        return total;
    }
}
