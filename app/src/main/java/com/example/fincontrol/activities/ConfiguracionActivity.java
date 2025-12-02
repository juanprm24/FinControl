package com.example.fincontrol.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.fincontrol.R;

/**
 * Activity para configuración de la aplicación
 */
public class ConfiguracionActivity extends AppCompatActivity {

    private Spinner spinnerMoneda;
    private CheckBox checkAnimaciones;

    private SharedPreferences preferences;
    private static final String PREFS_NAME = "FinControlPrefs";
    private static final String KEY_MONEDA = "moneda";
    private static final String KEY_TEMA = "tema";
    private static final String KEY_ANIMACIONES = "animaciones";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        // Configurar ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Configuración");
        }

        // Inicializar SharedPreferences
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Inicializar vistas
        inicializarVistas();

        // Cargar configuración guardada
        cargarConfiguracion();

        // Configurar listeners
        configurarListeners();
    }

    /**
     * Inicializa todas las vistas de la activity
     */
    private void inicializarVistas() {
        spinnerMoneda = findViewById(R.id.spinnerMoneda);
        checkAnimaciones = findViewById(R.id.checkAnimaciones);

        // Configurar Spinner de Moneda
        String[] monedas = {"MXN - Peso Mexicano", "USD - Dólar", "EUR - Euro"};
        ArrayAdapter<String> adapterMoneda = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                monedas
        );
        adapterMoneda.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMoneda.setAdapter(adapterMoneda);

        // Configurar Spinner de Tema
        String[] temas = {"Claro", "Oscuro", "Sistema"};
        ArrayAdapter<String> adapterTema = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                temas
        );
        adapterTema.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    /**
     * Carga la configuración guardada en SharedPreferences
     */
    private void cargarConfiguracion() {
        // Cargar moneda
        int monedaPos = preferences.getInt(KEY_MONEDA, 0);
        spinnerMoneda.setSelection(monedaPos);

        // Cargar tema
        int temaPos = preferences.getInt(KEY_TEMA, 0);

        // Cargar animaciones
        boolean animaciones = preferences.getBoolean(KEY_ANIMACIONES, true);
        checkAnimaciones.setChecked(animaciones);
    }

    /**
     * Configura los listeners para guardar cambios
     */
    private void configurarListeners() {
        // Listener para cambio de moneda
        spinnerMoneda.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                guardarPreferencia(KEY_MONEDA, position);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        // Listener para animaciones
        checkAnimaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
            guardarPreferencia(KEY_ANIMACIONES, isChecked);
            Toast.makeText(this,
                    isChecked ? "Animaciones activadas" : "Animaciones desactivadas",
                    Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Guarda una preferencia entera
     */
    private void guardarPreferencia(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Guarda una preferencia booleana
     */
    private void guardarPreferencia(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Aplica el tema seleccionado
     */
    private void aplicarTema(int posicion) {
        switch (posicion) {
            case 0: // Claro
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1: // Oscuro
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 2: // Sistema
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}