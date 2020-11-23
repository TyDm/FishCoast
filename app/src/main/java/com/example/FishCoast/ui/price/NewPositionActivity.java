package com.example.FishCoast.ui.price;

import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.FishCoast.DBHelper;
import com.example.FishCoast.R;


public class NewPositionActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ArrayAdapter<String> unitAdapter, categoryAdapter;
    private Spinner unitSpinner, categorySpinner;
    private String[] unitData = {"кг", "шт"};
    private String[] categoryData = {"Нет доступных категорий"};
    private EditText editTextName, editTextCost;
    private Button buttonAdd;
    private int nameFull = 0;
    private int costFull = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_position);
        setActionBar();

        unitSpinner = findViewById(R.id.spinnerUnit);
        categorySpinner = findViewById(R.id.spinnerCategory);
        editTextName = findViewById(R.id.editTextName);
        editTextCost = findViewById(R.id.editTextCost);



        unitAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                unitData);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitAdapter);

        categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                categoryData);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    buttonAddActive(true, 1);
                }
                else {
                    buttonAddActive(false, 1);
                }

            }
        });

        editTextCost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    buttonAddActive(true, 2);
                }
                else {
                    buttonAddActive(false, 2);
                }

            }
        });

    }

    public void setActionBar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void buttonAddActive (boolean bl, int a){
        buttonAdd = findViewById(R.id.buttonNewPositionAdd);
        if (a == 1){
            if (bl) nameFull = 1;
            else nameFull = 0;
        }
        if (a == 2){
            if (bl) costFull = 1;
            else costFull = 0;
        }

        if ((nameFull == 1) & (costFull == 1)) {
            buttonAdd.setEnabled(true);
        }
        else {
            buttonAdd.setEnabled(false);
        }
    }

    public void onClickButtonAdd(View view){
        DBHelper dbHelper = new DBHelper(this);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cv.put("type", getIntent().getIntExtra("pricetype", 0));
        cv.put("name", editTextName.getText().toString());
        if (categoryData.length < 2) cv.put("category", "Без категории");
        cv.put("cost", Float.parseFloat(editTextCost.getText().toString()));
        cv.put("unit", unitSpinner.getSelectedItemPosition());
        long rowId = db.insert("pricetable", null, cv);
        dbHelper.close();
        Toast.makeText(this, "Добавлено, id" + rowId, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }


}
