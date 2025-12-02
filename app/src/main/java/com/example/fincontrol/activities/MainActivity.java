package com.example.fincontrol.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.fincontrol.R;
import com.example.fincontrol.db.ConsultasSQL;
import com.example.fincontrol.utils.Formatos;
import com.google.android.material.navigation.NavigationView;

/**
 * Activity principal con Navigation Drawer y Dashboard
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FrameLayout contentFrame;

    private ConsultasSQL consultasSQL;

    // Vistas del Dashboard
    private View dashboardView;
    private TextView tvIngresosMes, tvGastosMes, tvBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar base de datos
        consultasSQL = new ConsultasSQL();

        // Inicializar vistas
        inicializarVistas();

        // Configurar toolbar
        setSupportActionBar(toolbar);

        // Configurar Navigation Drawer
        configurarNavigationDrawer();

        // Cargar dashboard por defecto
        cargarDashboard();
    }

    /**
     * Inicializa todas las vistas de la activity
     */
    private void inicializarVistas() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        contentFrame = findViewById(R.id.content_frame);
    }

    /**
     * Configura el Navigation Drawer con el menú lateral
     */
    private void configurarNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_inicio);
    }

    /**
     * Carga el dashboard principal con los datos del mes
     */
    private void cargarDashboard() {
        // Inflar el layout del dashboard
        dashboardView = getLayoutInflater().inflate(R.layout.fragment_dashboard, contentFrame, false);
        contentFrame.removeAllViews();
        contentFrame.addView(dashboardView);

        // Referencias a las vistas
        tvIngresosMes = dashboardView.findViewById(R.id.tvIngresosMes);
        tvGastosMes = dashboardView.findViewById(R.id.tvGastosMes);
        tvBalance = dashboardView.findViewById(R.id.tvBalance);

        // Cargar datos en segundo plano
        new Thread(() -> {
            final double ingresos = consultasSQL.obtenerIngresosMes();
            final double gastos = consultasSQL.obtenerGastosMes();
            final double balance = ingresos - gastos;

            // Actualizar UI en el hilo principal
            runOnUiThread(() -> {
                tvIngresosMes.setText(Formatos.formatearMoneda(ingresos));
                tvGastosMes.setText(Formatos.formatearMoneda(gastos));
                tvBalance.setText(Formatos.formatearMoneda(balance));

                // Aplicar animación fadeIn
                aplicarAnimacionFadeIn();
            });
        }).start();
    }

    /**
     * Aplica animación fadeIn a las cards del dashboard
     */
    private void aplicarAnimacionFadeIn() {
        View cardIngresos = dashboardView.findViewById(R.id.cardIngresos);
        View cardGastos = dashboardView.findViewById(R.id.cardGastos);
        View cardBalance = dashboardView.findViewById(R.id.cardBalance);

        cardIngresos.setAlpha(0f);
        cardGastos.setAlpha(0f);
        cardBalance.setAlpha(0f);

        cardIngresos.animate().alpha(1f).setDuration(500).setStartDelay(0).start();
        cardGastos.animate().alpha(1f).setDuration(500).setStartDelay(200).start();
        cardBalance.animate().alpha(1f).setDuration(500).setStartDelay(400).start();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_inicio) {
            cargarDashboard();
        } else if (id == R.id.nav_registrar) {
            Intent intent = new Intent(this, RegistrarTransaccionActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_categorias) {
            Intent intent = new Intent(this, CategoriasActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_historial) {
            Intent intent = new Intent(this, HistorialActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_estadisticas) {
            Intent intent = new Intent(this, EstadisticasActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_configuracion) {
            Intent intent = new Intent(this, ConfiguracionActivity.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos al volver a la activity
        if (navigationView.getCheckedItem().getItemId() == R.id.nav_inicio) {
            cargarDashboard();
        }
    }
}