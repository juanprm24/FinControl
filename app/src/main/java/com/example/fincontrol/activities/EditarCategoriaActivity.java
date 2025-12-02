package com.example.fincontrol.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fincontrol.R;
import com.example.fincontrol.db.ConsultasSQL;
import com.example.fincontrol.models.Categoria;

/**
 * Activity para crear o editar categorías
 */
public class EditarCategoriaActivity extends AppCompatActivity {

    private TextView tvTitulo;
    private EditText etNombre;
    private RadioGroup rgTipo;
    private RadioButton rbIngreso, rbGasto;
    private Button btnGuardar, btnCancelar, btnEliminar;

    private ConsultasSQL consultasSQL;
    private Categoria categoriaEditar;
    private boolean modoEdicion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_categorias);

        // Configurar ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicializar base de datos
        consultasSQL = new ConsultasSQL();

        // Inicializar vistas
        inicializarVistas();

        // Verificar si es modo edición
        verificarModoEdicion();

        // Configurar listeners
        configurarListeners();
    }

    /**
     * Inicializa todas las vistas de la activity
     */
    private void inicializarVistas() {
        tvTitulo = findViewById(R.id.tvTituloCategoria);
        etNombre = findViewById(R.id.etNombreCategoria);
        rgTipo = findViewById(R.id.rgTipoCategoria);
        rbIngreso = findViewById(R.id.rbIngresoCategoria);
        rbGasto = findViewById(R.id.rbGastoCategoria);
        btnGuardar = findViewById(R.id.btnGuardarCategoria);
        btnCancelar = findViewById(R.id.btnCancelarCategoria);
        btnEliminar = findViewById(R.id.btnEliminarCategoria);
    }

    /**
     * Verifica si se está editando una categoría existente
     */
    private void verificarModoEdicion() {
        // Recibir datos del Intent
        int idCategoria = getIntent().getIntExtra("id_categoria", -1);

        if (idCategoria != -1) {
            // Modo edición
            modoEdicion = true;
            tvTitulo.setText("Editar Categoría");
            btnEliminar.setVisibility(View.VISIBLE);

            // Cargar datos de la categoría
            String nombre = getIntent().getStringExtra("nombre");
            String tipo = getIntent().getStringExtra("tipo");

            etNombre.setText(nombre);
            if (tipo.equals("Ingreso")) {
                rbIngreso.setChecked(true);
            } else {
                rbGasto.setChecked(true);
            }

            // Crear objeto para editar
            categoriaEditar = new Categoria(idCategoria, nombre, tipo);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Editar Categoría");
            }
        } else {
            // Modo creación
            modoEdicion = false;
            tvTitulo.setText("Nueva Categoría");
            btnEliminar.setVisibility(View.GONE);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Nueva Categoría");
            }
        }
    }

    /**
     * Configura los listeners de los botones
     */
    private void configurarListeners() {
        // Botón guardar
        btnGuardar.setOnClickListener(v -> guardarCategoria());

        // Botón cancelar
        btnCancelar.setOnClickListener(v -> finish());

        // Botón eliminar
        btnEliminar.setOnClickListener(v -> mostrarDialogoEliminar());
    }

    /**
     * Guarda o actualiza la categoría en la base de datos
     */
    private void guardarCategoria() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }

        // Obtener valores
        String nombre = etNombre.getText().toString().trim();
        String tipo = rbIngreso.isChecked() ? "Ingreso" : "Gasto";

        if (modoEdicion) {
            // Actualizar categoría existente
            categoriaEditar.setNombre(nombre);
            categoriaEditar.setTipo(tipo);

            new Thread(() -> {
                boolean resultado = consultasSQL.actualizarCategoria(categoriaEditar);

                runOnUiThread(() -> {
                    if (resultado) {
                        Toast.makeText(this, R.string.categoria_guardada, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, R.string.error_conexion, Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();

        } else {
            // Crear nueva categoría
            Categoria nuevaCategoria = new Categoria(nombre, tipo);

            new Thread(() -> {
                boolean resultado = consultasSQL.insertarCategoria(nuevaCategoria);

                runOnUiThread(() -> {
                    if (resultado) {
                        Toast.makeText(this, R.string.categoria_guardada, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, R.string.error_conexion, Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        }
    }

    /**
     * Valida que todos los campos estén completos
     */
    private boolean validarCampos() {
        if (etNombre.getText().toString().trim().isEmpty()) {
            etNombre.setError("Ingrese el nombre de la categoría");
            etNombre.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Muestra diálogo de confirmación para eliminar
     */
    private void mostrarDialogoEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Categoría")
                .setMessage(R.string.confirmar_eliminar)
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarCategoria())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Elimina la categoría de la base de datos
     */
    private void eliminarCategoria() {
        new Thread(() -> {
            boolean resultado = consultasSQL.eliminarCategoria(categoriaEditar.getIdCategoria());

            runOnUiThread(() -> {
                if (resultado) {
                    Toast.makeText(this, R.string.categoria_eliminada, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, R.string.error_conexion, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}