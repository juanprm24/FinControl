package com.example.fincontrol.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fincontrol.R;
import com.example.fincontrol.models.Categoria;

import java.util.List;

/**
 * Adapter para mostrar categorías en un ListView
 */
public class CategoriasAdapter extends ArrayAdapter<Categoria> {

    private Context context;
    private List<Categoria> categorias;

    public CategoriasAdapter(@NonNull Context context, @NonNull List<Categoria> categorias) {
        super(context, 0, categorias);
        this.context = context;
        this.categorias = categorias;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.item_categoria, parent, false);
        }

        Categoria categoriaActual = categorias.get(position);

        // Referencias a los elementos del layout
        TextView tvNombre = listItem.findViewById(R.id.tvNombreCategoria);
        TextView tvTipo = listItem.findViewById(R.id.tvTipoCategoria);

        // Establecer valores
        tvNombre.setText(categoriaActual.getNombre());
        tvTipo.setText(categoriaActual.getTipo());

        // Cambiar color según el tipo
        if (categoriaActual.getTipo().equals("Ingreso")) {
            tvTipo.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvTipo.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        return listItem;
    }
}