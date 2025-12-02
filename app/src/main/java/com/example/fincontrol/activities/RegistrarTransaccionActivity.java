package com.example.fincontrol.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton; // Importado correctamente
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fincontrol.R;
import com.example.fincontrol.db.ConsultasSQL;
import com.example.fincontrol.models.Categoria;
import com.example.fincontrol.models.Transaccion;
import com.example.fincontrol.utils.Formatos;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Activity para registrar o editar transacciones
 */
public class RegistrarTransaccionActivity extends AppCompatActivity {

    private EditText etMonto, etDescripcion;
    private RadioGroup rgTipo;
    private RadioButton rbIngreso, rbGasto;
    private Spinner spinnerCategoria;
    private TextView tvFechaSeleccionada;
    private Button btnGuardar, btnCancelar;
    private ImageButton btnSeleccionarFecha; // CORREGIDO: Declarado como ImageButton

    private ConsultasSQL consultasSQL;
    private List<Categoria> listaCategorias;
    private Date fechaSeleccionada;

    private Transaccion transaccionEditar; // Objeto para manejar la edición
    private boolean modoEdicion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_transacciones);

        // Configurar ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Registrar Transacción");
        }

        // Inicializar base de datos
        consultasSQL = new ConsultasSQL();

        // Inicializar vistas
        inicializarVistas();

        // Configurar fecha actual por defecto
        fechaSeleccionada = new Date();
        tvFechaSeleccionada.setText(Formatos.formatearFecha(fechaSeleccionada));

        // Verificar si es modo edición ANTES de cargar categorías
        verificarModoEdicion();

        // Cargar categorías (ya filtradas si es modo edición)
        cargarCategorias();

        // Configurar listeners
        configurarListeners();
    }

    /**
     * Inicializa todas las vistas de la activity
     */
    private void inicializarVistas() {
        etMonto = findViewById(R.id.etMonto);
        etDescripcion = findViewById(R.id.etDescripcion);
        rgTipo = findViewById(R.id.rgTipo);
        rbIngreso = findViewById(R.id.rbIngreso);
        rbGasto = findViewById(R.id.rbGasto);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        tvFechaSeleccionada = findViewById(R.id.tvFechaSeleccionada);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha); // CORREGIDO
    }

    /**
     * Carga las categorías desde la base de datos y selecciona la categoría si está en modo edición.
     */
    private void cargarCategorias() {
        // Determinar el tipo inicial de categoría a cargar
        String tipoInicial = modoEdicion ? transaccionEditar.getTipo() : (rbIngreso.isChecked() ? "Ingreso" : "Gasto");

        new Thread(() -> {
            // Se usa obtenerCategoriasPorTipo para optimizar la carga inicial
            listaCategorias = consultasSQL.obtenerCategoriasPorTipo(tipoInicial);

            runOnUiThread(() -> {
                ArrayAdapter<Categoria> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        listaCategorias
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategoria.setAdapter(adapter);

                // Si es modo edición, seleccionar la categoría correcta
                if (modoEdicion) {
                    seleccionarCategoriaEditada();
                }
            });
        }).start();
    }

    /**
     * Selecciona la categoría en el Spinner basándose en el ID de la transacción a editar.
     */
    private void seleccionarCategoriaEditada() {
        if (transaccionEditar != null && listaCategorias != null) {
            for (int i = 0; i < listaCategorias.size(); i++) {
                if (listaCategorias.get(i).getIdCategoria() == transaccionEditar.getIdCategoria()) {
                    spinnerCategoria.setSelection(i);
                    break;
                }
            }
        }
    }


    /**
     * Verifica si se está editando una transacción existente y carga los datos.
     * CORREGIDO: Implementación completa del modo edición.
     */
    private void verificarModoEdicion() {
        // Recibir datos del Intent
        if (getIntent().hasExtra("id_transaccion")) {
            modoEdicion = true;

            // Cargar datos recibidos para crear el objeto de edición
            int idTransaccion = getIntent().getIntExtra("id_transaccion", -1);
            double monto = getIntent().getDoubleExtra("monto", 0.0);
            String descripcion = getIntent().getStringExtra("descripcion");
            long fechaMillis = getIntent().getLongExtra("fecha", new Date().getTime());
            String tipo = getIntent().getStringExtra("tipo");
            int idCategoria = getIntent().getIntExtra("id_categoria", -1);

            // Crear objeto Transaccion con los datos para poder actualizarlo luego
            transaccionEditar = new Transaccion(idTransaccion, monto, descripcion, new Date(fechaMillis), tipo, idCategoria);

            // Poblar campos
            etMonto.setText(Formatos.formatearNumero(monto));
            etDescripcion.setText(descripcion);
            fechaSeleccionada = new Date(fechaMillis);
            tvFechaSeleccionada.setText(Formatos.formatearFecha(fechaSeleccionada));

            if (tipo.equals("Ingreso")) {
                rbIngreso.setChecked(true);
            } else {
                rbGasto.setChecked(true);
            }

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Editar Transacción");
            }
        } else {
            modoEdicion = false;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Nueva Transacción");
            }
        }
    }

    /**
     * Configura los listeners de los botones
     */
    private void configurarListeners() {
        // Botón para seleccionar fecha
        btnSeleccionarFecha.setOnClickListener(v -> mostrarDatePicker());
        tvFechaSeleccionada.setOnClickListener(v -> mostrarDatePicker());

        // Listener para cambio de tipo (Ingreso/Gasto)
        rgTipo.setOnCheckedChangeListener((group, checkedId) -> {
            filtrarCategoriasPorTipo();
        });

        // Botón guardar
        btnGuardar.setOnClickListener(v -> guardarTransaccion());

        // Botón cancelar
        btnCancelar.setOnClickListener(v -> finish());
    }

    /**
     * Muestra el DatePicker para seleccionar fecha
     */
    private void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaSeleccionada);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar nuevaFecha = Calendar.getInstance();
                    // Preservar la hora actual para evitar problemas con la base de datos
                    Calendar horaActual = Calendar.getInstance();
                    nuevaFecha.set(year, month, dayOfMonth, horaActual.get(Calendar.HOUR_OF_DAY), horaActual.get(Calendar.MINUTE));

                    fechaSeleccionada = nuevaFecha.getTime();
                    tvFechaSeleccionada.setText(Formatos.formatearFecha(fechaSeleccionada));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    /**
     * Filtra las categorías según el tipo seleccionado
     */
    private void filtrarCategoriasPorTipo() {
        // Obtenemos el tipo seleccionado
        String tipoSeleccionado = rbIngreso.isChecked() ? "Ingreso" : "Gasto";

        new Thread(() -> {
            List<Categoria> categoriasFiltradas = consultasSQL.obtenerCategoriasPorTipo(tipoSeleccionado);
            listaCategorias = categoriasFiltradas; // Actualizar lista principal

            runOnUiThread(() -> {
                ArrayAdapter<Categoria> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categoriasFiltradas
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategoria.setAdapter(adapter);
            });
        }).start();
    }

    /**
     * Guarda la transacción en la base de datos (Insertar o Actualizar)
     * CORREGIDO: Lógica para manejar la actualización.
     */
    private void guardarTransaccion() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }

        // Obtener valores
        double monto = Double.parseDouble(etMonto.getText().toString());
        String descripcion = etDescripcion.getText().toString();
        String tipo = rbIngreso.isChecked() ? "Ingreso" : "Gasto";
        Categoria categoriaSeleccionada = (Categoria) spinnerCategoria.getSelectedItem();

        // Crear objeto Transaccion base
        Transaccion transaccion = new Transaccion(
                monto,
                descripcion,
                fechaSeleccionada,
                tipo,
                categoriaSeleccionada.getIdCategoria()
        );

        new Thread(() -> {
            boolean resultado;

            if (modoEdicion) {
                // Modo edición: Asignar ID y actualizar
                transaccion.setIdTransaccion(transaccionEditar.getIdTransaccion());
                resultado = consultasSQL.actualizarTransaccion(transaccion);
            } else {
                // Modo inserción: Insertar nueva transacción
                resultado = consultasSQL.insertarTransaccion(transaccion);
            }

            runOnUiThread(() -> {
                if (resultado) {
                    Toast.makeText(this, R.string.transaccion_guardada, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, R.string.error_conexion, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    /**
     * Valida que todos los campos estén completos
     */
    private boolean validarCampos() {
        if (etMonto.getText().toString().isEmpty()) {
            etMonto.setError("Ingrese el monto");
            return false;
        }

        if (etDescripcion.getText().toString().isEmpty()) {
            etDescripcion.setError("Ingrese la descripción");
            return false;
        }

        if (spinnerCategoria.getAdapter() == null || spinnerCategoria.getSelectedItem() == null) {
            Toast.makeText(this, "Seleccione una categoría", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}