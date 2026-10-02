package com.example.smartpantry.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.util.FormatUtils;

import java.util.Objects;

/**
 * Displays the pantry as a list of cards. Extends ListAdapter, which uses
 * DiffUtil to work out which rows changed, so updates animate smoothly.
 * The adapter only displays; click handling is delegated to a listener.
 */
public class PantryAdapter extends ListAdapter<PantryItem, PantryAdapter.PantryViewHolder> {

    /** Implemented by the screen that owns the list. */
    public interface OnPantryItemListener {
        /** The row itself was tapped (open for editing). */
        void onItemClick(PantryItem item);

        /** The delete icon on a row was tapped. */
        void onDeleteClick(PantryItem item);
    }

    private static final DiffUtil.ItemCallback<PantryItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<PantryItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull PantryItem oldItem, @NonNull PantryItem newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull PantryItem oldItem, @NonNull PantryItem newItem) {
                    return Objects.equals(oldItem.getName(), newItem.getName())
                            && Double.compare(oldItem.getQuantity(), newItem.getQuantity()) == 0
                            && Objects.equals(oldItem.getUnit(), newItem.getUnit())
                            && Objects.equals(oldItem.getExpiryDate(), newItem.getExpiryDate());
                }
            };

    private final OnPantryItemListener listener;

    public PantryAdapter(OnPantryItemListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    /** Lets the swipe handler look up the item behind a row. */
    public PantryItem getItemAt(int position) {
        return getItem(position);
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    /** Holds the views of one row and fills them from a PantryItem. */
    class PantryViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvQuantity;
        private final TextView tvExpiry;
        private final ImageButton btnDelete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvQuantity = itemView.findViewById(R.id.tv_item_quantity);
            tvExpiry = itemView.findViewById(R.id.tv_item_expiry);
            btnDelete = itemView.findViewById(R.id.btn_delete_item);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getItem(position));
                }
            });

            btnDelete.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteClick(getItem(position));
                }
            });
        }

        void bind(PantryItem item) {
            Context context = itemView.getContext();
            tvName.setText(item.getName());
            tvQuantity.setText(FormatUtils.formatAmount(item.getQuantity(), item.getUnit()));

            String expiry = item.getExpiryDate();
            if (expiry == null || expiry.isEmpty()) {
                tvExpiry.setText(R.string.no_expiry);
            } else {
                tvExpiry.setText(context.getString(R.string.expires_on, expiry));
            }
        }
    }
}