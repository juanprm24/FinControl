package com.example.fincontrol.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fincontrol.R;
import com.example.fincontrol.adapters.TransaccionesAdapter; // Usando el nombre corregido
import com.example.fincontrol.db.ConsultasSQL;
import com.example.fincontrol.models.Transaccion;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity para mostrar el historial completo de transacciones
 */
public class HistorialActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvNoTransacciones;

    private ConsultasSQL consultasSQL;
    private List<Transaccion> listaTransacciones;
    // Asumiendo que el archivo se ha renombrado a TransaccionesAdapter.java
    private TransaccionesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        // Configurar ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Historial");
        }

        // Inicializar base de datos
        consultasSQL = new ConsultasSQL();

        // Inicializar vistas
        inicializarVistas();

        // Configurar RecyclerView
        configurarRecyclerView();

        // Cargar transacciones
        cargarTransacciones();
    }

    /**
     * Inicializa todas las vistas de la activity
     */
    private void inicializarVistas() {
        recyclerView = findViewById(R.id.recyclerViewHistorial);
        tvNoTransacciones = findViewById(R.id.tvNoTransacciones);
    }

    /**
     * Configura el RecyclerView con su layout manager
     */
    private void configurarRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        listaTransacciones = new ArrayList<>();
        adapter = new TransaccionesAdapter(this, listaTransacciones);
        recyclerView.setAdapter(adapter);

        // Configurar listeners del adapter
        adapter.setOnItemClickListener(new TransaccionesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Transaccion transaccion) {
                // Al hacer click, muestra el diálogo de opciones para editar/eliminar
                mostrarDialogoOpciones(transaccion);
            }

            @Override
            public void onItemLongClick(Transaccion transaccion) {
                // Mantiene el diálogo de opciones también para LongClick
                mostrarDialogoOpciones(transaccion);
            }
        });
    }

    /**
     * Carga las transacciones desde la base de datos
     */
    private void cargarTransacciones() {
        new Thread(() -> {
            listaTransacciones = consultasSQL.obtenerTransacciones();

            runOnUiThread(() -> {
                if (listaTransacciones.isEmpty()) {
                    tvNoTransacciones.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvNoTransacciones.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.actualizarLista(listaTransacciones);

                    // Aplicar animación
                    aplicarAnimacionSlide();
                }
            });
        }).start();
    }

    /**
     * Aplica animación slide a los items del RecyclerView
     */
    private void aplicarAnimacionSlide() {
        recyclerView.setAlpha(0f);
        recyclerView.setTranslationY(50f);
        recyclerView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .start();
    }

    /**
     * Muestra diálogo con opciones para la transacción
     */
    private void mostrarDialogoOpciones(Transaccion transaccion) {
        String[] opciones = {"Editar", "Eliminar"};

        new AlertDialog.Builder(this)
                .setTitle(transaccion.getDescripcion())
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        // Editar
                        editarTransaccion(transaccion); // Llamada al método corregido
                    } else {
                        // Eliminar
                        mostrarDialogoEliminar(transaccion);
                    }
                })
                .show();
    }

    /**
     * Abre la activity para editar la transacción
     * CORREGIDO: Se pasa toda la información de la transacción al intent.
     */
    private void editarTransaccion(Transaccion transaccion) {
        Intent intent = new Intent(this, RegistrarTransaccionActivity.class);

        // Pasar todos los datos de la transacción para el modo edición
        intent.putExtra("id_transaccion", transaccion.getIdTransaccion());
        intent.putExtra("monto", transaccion.getMonto());
        intent.putExtra("descripcion", transaccion.getDescripcion());
        // Pasamos la fecha como long (milisegundos) que es más seguro entre Activities
        intent.putExtra("fecha", transaccion.getFecha().getTime());
        intent.putExtra("tipo", transaccion.getTipo());
        intent.putExtra("id_categoria", transaccion.getIdCategoria());

        startActivity(intent);
    }

    /**
     * Muestra diálogo de confirmación para eliminar
     */
    private void mostrarDialogoEliminar(Transaccion transaccion) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Transacción")
                .setMessage(R.string.confirmar_eliminar)
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarTransaccion(transaccion))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Elimina una transacción de la base de datos
     */
    private void eliminarTransaccion(Transaccion transaccion) {
        new Thread(() -> {
            boolean resultado = consultasSQL.eliminarTransaccion(transaccion.getIdTransaccion());

            runOnUiThread(() -> {
                if (resultado) {
                    Toast.makeText(this, R.string.transaccion_eliminada, Toast.LENGTH_SHORT).show();
                    cargarTransacciones();
                } else {
                    Toast.makeText(this, R.string.error_conexion, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar transacciones cada vez que volvemos a esta actividad
        cargarTransacciones();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}