package com.example.fincontrol.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fincontrol.R;
import com.example.fincontrol.models.Transaccion;
import com.example.fincontrol.utils.Formatos;

import java.util.List;

/**
 * Adapter para mostrar transacciones en un RecyclerView
 */
public class TransaccionesAdapter extends RecyclerView.Adapter<TransaccionesAdapter.TransaccionViewHolder> {

    private Context context;
    private List<Transaccion> transacciones;
    private OnItemClickListener listener;

    // Interface para manejar clicks
    public interface OnItemClickListener {
        void onItemClick(Transaccion transaccion);
        void onItemLongClick(Transaccion transaccion);
    }

    public TransaccionesAdapter(Context context, List<Transaccion> transacciones) {
        this.context = context;
        this.transacciones = transacciones;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransaccionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaccion, parent, false);
        return new TransaccionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransaccionViewHolder holder, int position) {
        Transaccion transaccion = transacciones.get(position);

        holder.tvMonto.setText(Formatos.formatearMoneda(transaccion.getMonto()));
        holder.tvDescripcion.setText(transaccion.getDescripcion());
        holder.tvCategoria.setText(transaccion.getNombreCategoria());
        holder.tvFecha.setText(Formatos.formatearFecha(transaccion.getFecha()));
        holder.tvTipo.setText(transaccion.getTipo());

        // Color según el tipo
        if (transaccion.getTipo().equals("Ingreso")) {
            holder.tvMonto.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.tvTipo.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
        } else {
            holder.tvMonto.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            holder.tvTipo.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
        }

        // Manejar clicks
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(transaccion);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(transaccion);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return transacciones.size();
    }

    /**
     * ViewHolder para las transacciones
     */
    public static class TransaccionViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvMonto, tvDescripcion, tvCategoria, tvFecha, tvTipo;

        public TransaccionViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewTransaccion);
            tvMonto = itemView.findViewById(R.id.tvMontoTransaccion);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionTransaccion);
            tvCategoria = itemView.findViewById(R.id.tvCategoriaTransaccion);
            tvFecha = itemView.findViewById(R.id.tvFechaTransaccion);
            tvTipo = itemView.findViewById(R.id.tvTipoTransaccion);
        }
    }

    /**
     * Actualiza la lista de transacciones
     */
    public void actualizarLista(List<Transaccion> nuevasTransacciones) {
        this.transacciones = nuevasTransacciones;
        notifyDataSetChanged();
    }
}