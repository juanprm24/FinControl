package com.example.fincontrol.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.fincontrol.R;
import com.example.fincontrol.db.ConsultasSQL;
import com.example.fincontrol.models.Categoria;
import com.example.fincontrol.models.Transaccion;
import com.example.fincontrol.utils.Formatos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity para mostrar estadísticas y gráficas
 */
public class EstadisticasActivity extends AppCompatActivity {

    private LinearLayout layoutEstadisticas;
    private TextView tvTotalIngresos, tvTotalGastos, tvBalance;
    private LinearLayout layoutGraficas;

    private ConsultasSQL consultasSQL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        // Configurar ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Estadísticas");
        }

        // Inicializar base de datos
        consultasSQL = new ConsultasSQL();

        // Inicializar vistas
        inicializarVistas();

        // Cargar estadísticas
        cargarEstadisticas();
    }

    /**
     * Inicializa todas las vistas de la activity
     */
    private void inicializarVistas() {
        layoutEstadisticas = findViewById(R.id.layoutEstadisticas);
        tvTotalIngresos = findViewById(R.id.tvTotalIngresos);
        tvTotalGastos = findViewById(R.id.tvTotalGastos);
        tvBalance = findViewById(R.id.tvBalance);
        layoutGraficas = findViewById(R.id.layoutGraficas);
    }

    /**
     * Carga las estadísticas desde la base de datos
     */
    private void cargarEstadisticas() {
        new Thread(() -> {
            // Obtener totales del mes
            final double ingresos = consultasSQL.obtenerIngresosMes();
            final double gastos = consultasSQL.obtenerGastosMes();
            final double balance = ingresos - gastos;

            // Obtener transacciones para análisis por categoría
            final List<Transaccion> transacciones = consultasSQL.obtenerTransacciones();
            final List<Categoria> categorias = consultasSQL.obtenerCategorias();

            runOnUiThread(() -> {
                // Mostrar totales
                tvTotalIngresos.setText(Formatos.formatearMoneda(ingresos));
                tvTotalGastos.setText(Formatos.formatearMoneda(gastos));
                tvBalance.setText(Formatos.formatearMoneda(balance));

                // Generar gráficas por categoría
                generarGraficasCategorias(transacciones, categorias);

                // Aplicar animación
                aplicarAnimacionFadeIn();
            });
        }).start();
    }

    /**
     * Genera gráficas simples por categoría usando barras de colores
     */
    private void generarGraficasCategorias(List<Transaccion> transacciones, List<Categoria> categorias) {
        // Calcular totales por categoría
        Map<String, Double> totalesPorCategoria = new HashMap<>();

        for (Transaccion t : transacciones) {
            String nombreCat = t.getNombreCategoria();
            if (nombreCat != null) {
                double total = totalesPorCategoria.getOrDefault(nombreCat, 0.0);
                totalesPorCategoria.put(nombreCat, total + t.getMonto());
            }
        }

        // Encontrar el máximo para calcular proporciones
        double maximo = 0;
        for (Double valor : totalesPorCategoria.values()) {
            if (valor > maximo) maximo = valor;
        }

        // Crear barras para cada categoría
        layoutGraficas.removeAllViews();

        for (Map.Entry<String, Double> entry : totalesPorCategoria.entrySet()) {
            String categoria = entry.getKey();
            double monto = entry.getValue();

            // Crear vista de barra
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setPadding(8, 8, 8, 8);

            // Nombre de categoría
            TextView tvCategoria = new TextView(this);
            tvCategoria.setText(categoria);
            tvCategoria.setTextSize(14);
            tvCategoria.setTextColor(Color.BLACK);

            // Barra de progreso visual
            LinearLayout barra = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) ((monto / maximo) * 800),
                    30
            );
            params.setMargins(0, 4, 0, 4);
            barra.setLayoutParams(params);
            barra.setBackgroundColor(Color.parseColor("#2196F3"));

            // Monto
            TextView tvMonto = new TextView(this);
            tvMonto.setText(Formatos.formatearMoneda(monto));
            tvMonto.setTextSize(12);
            tvMonto.setPadding(4, 0, 0, 0);

            itemLayout.addView(tvCategoria);
            itemLayout.addView(barra);
            itemLayout.addView(tvMonto);

            layoutGraficas.addView(itemLayout);
        }
    }

    /**
     * Aplica animación fadeIn al layout de estadísticas
     */
    private void aplicarAnimacionFadeIn() {
        layoutEstadisticas.setAlpha(0f);
        layoutEstadisticas.animate()
                .alpha(1f)
                .setDuration(600)
                .start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}