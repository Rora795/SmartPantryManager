package com.smartpantry.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.app.R;
import com.smartpantry.app.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom RecyclerView.Adapter that binds a list of PantryItem objects to
 * row views (Section 3.1 - RecyclerView + custom Adapter). Exposes two
 * callbacks so the fragment decides what happens on tap / delete.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.VH> {

    /** Simple callback interface for row actions. */
    public interface OnItemAction {
        void run(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final OnItemAction onClick;
    private final OnItemAction onDelete;

    public PantryAdapter(OnItemAction onClick, OnItemAction onDelete) {
        this.onClick = onClick;
        this.onDelete = onDelete;
    }

    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_pantry_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        PantryItem item = items.get(position);
        h.name.setText(item.getName());

        String qty = trimNumber(item.getQuantity()) + " "
                + (item.getUnit() == null ? "" : item.getUnit());
        h.detail.setText(qty.trim());

        if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
            h.expiry.setVisibility(View.VISIBLE);
            h.expiry.setText(h.expiry.getContext().getString(R.string.label_expires, item.getExpiryDate()));
        } else {
            h.expiry.setVisibility(View.GONE);
        }

        h.itemView.setOnClickListener(v -> onClick.run(item));
        h.delete.setOnClickListener(v -> onDelete.run(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String trimNumber(double d) {
        if (d == Math.floor(d)) return String.valueOf((long) d);
        return String.valueOf(d);
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView name, detail, expiry;
        final ImageButton delete;

        VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.row_name);
            detail = itemView.findViewById(R.id.row_detail);
            expiry = itemView.findViewById(R.id.row_expiry);
            delete = itemView.findViewById(R.id.row_delete);
        }
    }
}
