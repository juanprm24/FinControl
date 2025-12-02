package com.example.fincontrol.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fincontrol.R;
import com.example.fincontrol.adapters.CategoriasAdapter;
import com.example.fincontrol.db.ConsultasSQL;
import com.example.fincontrol.models.Categoria;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity para mostrar y gestionar categorías
 */
public class CategoriasActivity extends AppCompatActivity {

    private ListView listViewCategorias;
    private TextView tvNoCategorias;
    private FloatingActionButton fabAgregar;

    private ConsultasSQL consultasSQL;
    private List<Categoria> listaCategorias;
    private CategoriasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        // Configurar ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Categorías");
        }

        // Inicializar base de datos
        consultasSQL = new ConsultasSQL();

        // Inicializar vistas
        inicializarVistas();

        // Cargar categorías
        cargarCategorias();

        // Configurar listeners
        configurarListeners();
    }

    /**
     * Inicializa todas las vistas de la activity
     */
    private void inicializarVistas() {
        listViewCategorias = findViewById(R.id.listViewCategorias);
        tvNoCategorias = findViewById(R.id.tvNoCategorias);
        fabAgregar = findViewById(R.id.fabAgregarCategoria);
    }

    /**
     * Carga las categorías desde la base de datos
     */
    private void cargarCategorias() {
        new Thread(() -> {
            listaCategorias = consultasSQL.obtenerCategorias();

            runOnUiThread(() -> {
                if (listaCategorias.isEmpty()) {
                    tvNoCategorias.setVisibility(View.VISIBLE);
                    listViewCategorias.setVisibility(View.GONE);
                } else {
                    tvNoCategorias.setVisibility(View.GONE);
                    listViewCategorias.setVisibility(View.VISIBLE);

                    adapter = new CategoriasAdapter(this, listaCategorias);
                    listViewCategorias.setAdapter(adapter);
                }
            });
        }).start();
    }

    /**
     * Configura los listeners de los elementos
     */
    private void configurarListeners() {
        // Click en FAB para agregar nueva categoría
        fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditarCategoriaActivity.class);
            startActivity(intent);
        });

        // Click en item de la lista para editar
        listViewCategorias.setOnItemClickListener((parent, view, position, id) -> {
            Categoria categoria = listaCategorias.get(position);
            Intent intent = new Intent(this, EditarCategoriaActivity.class);
            intent.putExtra("id_categoria", categoria.getIdCategoria());
            intent.putExtra("nombre", categoria.getNombre());
            intent.putExtra("tipo", categoria.getTipo());
            startActivity(intent);
        });

        // Long click para eliminar
        listViewCategorias.setOnItemLongClickListener((parent, view, position, id) -> {
            Categoria categoria = listaCategorias.get(position);
            mostrarDialogoEliminar(categoria);
            return true;
        });
    }

    /**
     * Muestra diálogo de confirmación para eliminar
     */
    private void mostrarDialogoEliminar(Categoria categoria) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Categoría")
                .setMessage("¿Está seguro de eliminar la categoría '" + categoria.getNombre() + "'?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarCategoria(categoria))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Elimina una categoría de la base de datos
     */
    private void eliminarCategoria(Categoria categoria) {
        new Thread(() -> {
            boolean resultado = consultasSQL.eliminarCategoria(categoria.getIdCategoria());

            runOnUiThread(() -> {
                if (resultado) {
                    Toast.makeText(this, R.string.categoria_eliminada, Toast.LENGTH_SHORT).show();
                    cargarCategorias();
                } else {
                    Toast.makeText(this, R.string.error_conexion, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCategorias();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}