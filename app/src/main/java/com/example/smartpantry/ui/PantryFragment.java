package com.example.smartpantry.ui;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.Add_Edit_Ingredient_Activity;
import com.example.smartpantry.R;
import com.example.smartpantry.adapter.PantryAdapter;
import com.example.smartpantry.data.Repository;
import com.example.smartpantry.data.RepositoryImpl;
import com.example.smartpantry.data.ResultCallback;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.util.IntentKeys;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * The Pantry List screen: shows every ingredient, lets the user add (FAB),
 * edit (tap a row) and delete (swipe a row or tap its bin icon, with Undo).
 */
public class PantryFragment extends Fragment implements PantryAdapter.OnPantryItemListener {

    private final Repository repository = RepositoryImpl.getInstance();

    /** Local copy of what the list currently shows. */
    private final List<PantryItem> items = new ArrayList<>();

    private PantryAdapter adapter;
    private RecyclerView recycler;
    private View progress;
    private View emptyLayout;
    private View errorLayout;
    private TextView errorMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pantry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recycler = view.findViewById(R.id.recycler_pantry);
        progress = view.findViewById(R.id.progress_pantry);
        emptyLayout = view.findViewById(R.id.layout_empty_pantry);
        errorLayout = view.findViewById(R.id.layout_error_pantry);
        errorMessage = view.findViewById(R.id.tv_error_message);
        FloatingActionButton fab = view.findViewById(R.id.fab_add_pantry);

        adapter = new PantryAdapter(this);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);
        attachSwipeToDelete();

        // Add mode: an Intent with no extras
        fab.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), Add_Edit_Ingredient_Activity.class)));

        view.findViewById(R.id.btn_retry).setOnClickListener(v -> loadItems());
    }

    /** Reload whenever the screen becomes visible, so edits made elsewhere show up. */
    @Override
    public void onResume() {
        super.onResume();
        loadItems();
    }

    // loading

    private void loadItems() {
        if (items.isEmpty()) {
            showLoading();
        }
        repository.getPantryItems(new ResultCallback<List<PantryItem>>() {
            @Override
            public void onSuccess(List<PantryItem> result) {
                if (!isViewAlive()) return;
                items.clear();
                items.addAll(result);
                showItems();
            }

            @Override
            public void onError(String message) {
                if (!isViewAlive()) return;
                if (items.isEmpty()) {
                    showError(message);
                } else {
                    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    /** Callbacks can arrive after the user left the screen; never touch dead views. */
    private boolean isViewAlive() {
        return isAdded() && getView() != null;
    }

    // screen state

    private void showLoading() {
        progress.setVisibility(View.VISIBLE);
        recycler.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
    }

    private void showItems() {
        progress.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        boolean empty = items.isEmpty();
        emptyLayout.setVisibility(empty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        adapter.submitList(new ArrayList<>(items));
    }

    private void showError(String message) {
        progress.setVisibility(View.GONE);
        recycler.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);
        errorMessage.setText(message);
        errorLayout.setVisibility(View.VISIBLE);
    }

    // delete

    /**
     * Removes the row immediately (optimistic update), then asks the server.
     * If the server call fails the list is reloaded so the row comes back.
     */
    private void deleteItem(PantryItem item) {
        items.remove(item);
        showItems();

        repository.deletePantryItem(item.getId(), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (!isViewAlive()) return;
                Snackbar.make(requireView(),
                                getString(R.string.item_deleted, item.getName()),
                                Snackbar.LENGTH_LONG)
                        .setAction(R.string.action_undo, v -> restoreItem(item))
                        .show();
            }

            @Override
            public void onError(String message) {
                if (!isViewAlive()) return;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
                loadItems();
            }
        });
    }

    /** Undo: the item is created again (it receives a new id) */
    private void restoreItem(PantryItem item) {
        repository.addPantryItem(item, new ResultCallback<PantryItem>() {
            @Override
            public void onSuccess(PantryItem result) {
                if (!isViewAlive()) return;
                loadItems();
            }

            @Override
            public void onError(String message) {
                if (!isViewAlive()) return;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void attachSwipeToDelete() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            private final ColorDrawable background = new ColorDrawable(
                    ContextCompat.getColor(requireContext(), R.color.expiry_expired));

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false; // drag-and-drop reordering is not supported
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    deleteItem(adapter.getItemAt(position));
                }
            }

            /** Paints a red strip behind the row while it is being swiped. */
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View row = viewHolder.itemView;
                if (dX > 0) {
                    background.setBounds(row.getLeft(), row.getTop(),
                            row.getLeft() + (int) dX, row.getBottom());
                } else if (dX < 0) {
                    background.setBounds(row.getRight() + (int) dX, row.getTop(),
                            row.getRight(), row.getBottom());
                } else {
                    background.setBounds(0, 0, 0, 0);
                }
                background.draw(c);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(recycler);
    }

    // PantryAdapter.OnPantryItemListener

    /** Edit mode: the tapped item travels to the form as an Intent extra. */
    @Override
    public void onItemClick(PantryItem item) {
        Intent intent = new Intent(requireContext(), Add_Edit_Ingredient_Activity.class);
        intent.putExtra(IntentKeys.EXTRA_PANTRY_ITEM, item);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(PantryItem item) {
        deleteItem(item);
    }
}