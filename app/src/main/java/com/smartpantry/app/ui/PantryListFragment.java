package com.smartpantry.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smartpantry.app.R;
import com.smartpantry.app.db.DatabaseHelper;
import com.smartpantry.app.model.PantryItem;
import com.smartpantry.app.ui.adapter.PantryAdapter;

import java.util.List;

/**
 * Pantry List screen. Shows every ingredient the user has, in a RecyclerView
 * bound to SQLite via a custom adapter. Tapping an item opens the edit form;
 * the FAB opens the add form; each row has a delete button.
 */
public class PantryListFragment extends Fragment {

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private RecyclerView recycler;
    private View emptyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pantry_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DatabaseHelper(requireContext());

        recycler = view.findViewById(R.id.recycler_pantry);
        emptyView = view.findViewById(R.id.text_empty_pantry);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new PantryAdapter(
                this::openEdit,     // tap row -> edit
                this::deleteItem);  // tap delete -> remove
        recycler.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab_add);
        fab.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AddEditIngredientActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh(); // reload after returning from add/edit so changes show immediately
    }

    private void refresh() {
        List<PantryItem> items = db.getAllPantryItems();
        adapter.setItems(items);
        boolean empty = items.isEmpty();
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void openEdit(PantryItem item) {
        Intent i = new Intent(requireContext(), AddEditIngredientActivity.class);
        i.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(i);
    }

    private void deleteItem(PantryItem item) {
        db.deletePantryItem(item.getId());
        refresh();
    }
}
