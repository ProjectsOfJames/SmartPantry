package com.example.smartpantry.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.util.ExpiryUtils;
import com.example.smartpantry.util.FormatUtils;
import com.google.android.material.card.MaterialCardView;

import java.util.Objects;

/**
 * Displays the pantry as a list of cards. Extends ListAdapter, which uses
 * DiffUtil to work out which rows changed, so updates animate smoothly.
 * Expired items get a red outline and text; items expiring within the
 * "alert days" from Settings get an orange one.
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

    /** Items expiring within this many days are highlighted as "expiring soon". */
    private int alertDays = 3;

    public PantryAdapter(OnPantryItemListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    /** Called when the Settings value may have changed; redraws rows only if it did. */
    public void setAlertDays(int alertDays) {
        if (this.alertDays != alertDays) {
            this.alertDays = alertDays;
            notifyDataSetChanged();
        }
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

        private final MaterialCardView card;
        private final TextView tvName;
        private final TextView tvQuantity;
        private final TextView tvExpiry;
        private final ImageButton btnDelete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            card = (MaterialCardView) itemView;
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

            String date = item.getExpiryDate();
            Integer days = ExpiryUtils.daysUntilExpiry(date);

            // Rows are recycled, so every state must set every property
            int color = ContextCompat.getColor(context, R.color.text_secondary);
            boolean highlighted = false;
            String text;

            if (days == null) {
                text = context.getString(R.string.no_expiry);
            } else if (days < 0) {
                text = context.getString(R.string.expiry_line_expired, date);
                color = ContextCompat.getColor(context, R.color.expiry_expired);
                highlighted = true;
            } else if (days <= alertDays) {
                text = context.getString(R.string.expiry_line_soon, date, describeDays(context, days));
                color = ContextCompat.getColor(context, R.color.expiry_soon);
                highlighted = true;
            } else {
                text = context.getString(R.string.expires_on, date);
            }

            tvExpiry.setText(text);
            tvExpiry.setTextColor(color);
            card.setStrokeColor(color);
            card.setStrokeWidth(highlighted ? dp(context, 2) : 0);
        }

        private String describeDays(Context context, int days) {
            if (days == 0) return context.getString(R.string.when_today);
            if (days == 1) return context.getString(R.string.when_tomorrow);
            return context.getString(R.string.when_in_days, days);
        }

        private int dp(Context context, int value) {
            return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                    context.getResources().getDisplayMetrics()));
        }
    }
}