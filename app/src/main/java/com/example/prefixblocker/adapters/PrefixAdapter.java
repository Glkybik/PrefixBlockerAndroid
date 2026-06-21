package com.example.prefixblocker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prefixblocker.R;
import com.example.prefixblocker.data.Prefix;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.List;

public class PrefixAdapter extends RecyclerView.Adapter<PrefixAdapter.PrefixViewHolder> {

    private List<Prefix> prefixes;
    private OnPrefixActionListener listener;

    public interface OnPrefixActionListener {
        void onDelete(String prefix);
        void onToggle(String prefix);
    }

    public PrefixAdapter(List<Prefix> prefixes, OnPrefixActionListener listener) {
        this.prefixes = prefixes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PrefixViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prefix, parent, false);
        return new PrefixViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrefixViewHolder holder, int position) {
        Prefix prefix = prefixes.get(position);

        holder.tvPrefix.setText("+7 " + prefix.getPrefix());
        holder.switchActive.setChecked(prefix.isActive());

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(prefix.getPrefix());
            }
        });

        holder.switchActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onToggle(prefix.getPrefix());
            }
        });
    }

    @Override
    public int getItemCount() {
        return prefixes.size();
    }

    public void updateList(List<Prefix> newList) {
        this.prefixes = newList;
        notifyDataSetChanged();
    }

    static class PrefixViewHolder extends RecyclerView.ViewHolder {
        TextView tvPrefix;
        SwitchMaterial switchActive;
        MaterialButton btnDelete;

        PrefixViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPrefix = itemView.findViewById(R.id.tv_prefix);
            switchActive = itemView.findViewById(R.id.switch_active);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}