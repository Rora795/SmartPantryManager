package com.smartpantry.app.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.smartpantry.app.R;
import com.smartpantry.app.db.DatabaseHelper;
import com.smartpantry.app.model.PantryItem;

/**
 * Add / Edit Ingredient form. Reached via an Intent from the pantry list.
 * If EXTRA_ITEM_ID is passed we are EDITING (pre-fill + update), otherwise we
 * are ADDING (insert). Demonstrates Intent data passing + input validation.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";

    private DatabaseHelper db;
    private long itemId = -1; // -1 => add mode

    private EditText nameInput, qtyInput, expiryInput;
    private AutoCompleteTextView unitInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DatabaseHelper(this);

        nameInput = findViewById(R.id.input_name);
        qtyInput = findViewById(R.id.input_quantity);
        unitInput = findViewById(R.id.input_unit);
        expiryInput = findViewById(R.id.input_expiry);
        Button save = findViewById(R.id.button_save);

        // Suggested units for the dropdown.
        unitInput.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                new String[]{"pcs", "g", "kg", "ml", "l", "tbsp", "tsp", "cup"}));

        // If an id was passed in the Intent, we are editing an existing item.
        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (itemId != -1) {
            setTitle(R.string.title_edit_item);
            PantryItem existing = db.getPantryItem(itemId);
            if (existing != null) {
                nameInput.setText(existing.getName());
                qtyInput.setText(String.valueOf(existing.getQuantity()));
                unitInput.setText(existing.getUnit(), false);
                expiryInput.setText(existing.getExpiryDate());
            }
        } else {
            setTitle(R.string.title_add_item);
        }

        save.setOnClickListener(v -> save());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /** Validate the form, then insert or update. */
    private void save() {
        String name = nameInput.getText().toString().trim();
        String qtyStr = qtyInput.getText().toString().trim();
        String unit = unitInput.getText().toString().trim();
        String expiry = expiryInput.getText().toString().trim();

        // --- Input validation (Section 3.1) ---
        if (TextUtils.isEmpty(name)) {
            nameInput.setError(getString(R.string.err_name_required));
            nameInput.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(qtyStr)) {
            qtyInput.setError(getString(R.string.err_qty_required));
            qtyInput.requestFocus();
            return;
        }
        double qty;
        try {
            qty = Double.parseDouble(qtyStr);
        } catch (NumberFormatException e) {
            qtyInput.setError(getString(R.string.err_qty_number));
            qtyInput.requestFocus();
            return;
        }
        if (qty <= 0) {
            qtyInput.setError(getString(R.string.err_qty_positive));
            qtyInput.requestFocus();
            return;
        }
        if (expiry.length() > 0 && !expiry.matches("\\d{4}-\\d{2}-\\d{2}")) {
            expiryInput.setError(getString(R.string.err_expiry_format));
            expiryInput.requestFocus();
            return;
        }

        PantryItem item = new PantryItem(itemId, name, qty, unit, expiry);
        if (itemId == -1) {
            db.addPantryItem(item);
            Toast.makeText(this, R.string.msg_added, Toast.LENGTH_SHORT).show();
        } else {
            db.updatePantryItem(item);
            Toast.makeText(this, R.string.msg_updated, Toast.LENGTH_SHORT).show();
        }
        finish(); // return to the pantry list, which reloads in onResume()
    }
}
