package com.example.fincontrol.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    private Button btnGuardar, btnCancelar, btnSeleccionarFecha;

    private ConsultasSQL consultasSQL;
    private List<Categoria> listaCategorias;
    private Date fechaSeleccionada;

    private Transaccion transaccionEditar; // null si es nueva transacción
    private boolean modoEdicion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_transacciones);

        // Inicializar base de datos
        consultasSQL = new ConsultasSQL();

        // Inicializar vistas
        inicializarVistas();

        // Configurar fecha actual por defecto
        fechaSeleccionada = new Date();
        tvFechaSeleccionada.setText(Formatos.formatearFecha(fechaSeleccionada));

        // Cargar categorías
        cargarCategorias();

        // Verificar si es modo edición
        verificarModoEdicion();

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
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha);
    }

    /**
     * Carga las categorías desde la base de datos
     */
    private void cargarCategorias() {
        new Thread(() -> {
            listaCategorias = consultasSQL.obtenerCategorias();

            runOnUiThread(() -> {
                ArrayAdapter<Categoria> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        listaCategorias
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategoria.setAdapter(adapter);
            });
        }).start();
    }

    /**
     * Verifica si se está editando una transacción existente
     */
    private void verificarModoEdicion() {
        // Aquí podrías recibir datos del Intent si es modo edición
        // Por ahora está configurado solo para crear nuevas transacciones
        modoEdicion = false;
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
                    nuevaFecha.set(year, month, dayOfMonth);
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
        String tipoSeleccionado = rbIngreso.isChecked() ? "Ingreso" : "Gasto";

        new Thread(() -> {
            List<Categoria> categoriasFiltradas = consultasSQL.obtenerCategoriasPorTipo(tipoSeleccionado);

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
     * Guarda la transacción en la base de datos
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

        // Crear objeto Transaccion
        Transaccion transaccion = new Transaccion(
                monto,
                descripcion,
                fechaSeleccionada,
                tipo,
                categoriaSeleccionada.getIdCategoria()
        );

        // Guardar en segundo plano
        new Thread(() -> {
            boolean resultado = consultasSQL.insertarTransaccion(transaccion);

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

        if (spinnerCategoria.getSelectedItem() == null) {
            Toast.makeText(this, "Seleccione una categoría", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}