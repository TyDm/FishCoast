package com.example.FishCoast.clients;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.FishCoast.DBHelper;
import com.example.FishCoast.R;

public class ClientEditActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    private EditText city, street, company, phone;
    private Button button_add;
    private ContentValues cv;
    private SQLiteDatabase db;
    private Cursor c;
    private Toolbar toolbar;
    private int launchType, positionIndex;
    private Spinner clientPriceSpinner;
    String clientid;


    private void onCreatePriceSpinner(){
        clientPriceSpinner = findViewById(R.id.clientPriceSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.prices_array ,
                R.layout.item_client_price_spinner );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clientPriceSpinner.setAdapter(spinnerAdapter);
        clientPriceSpinner.setSelection(0);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_client);
        company = findViewById(R.id.editTextCompany);
        city = findViewById(R.id.editTextCity);
        street = findViewById(R.id.editTextStreet);
        phone = findViewById(R.id.editTextPhone);
        button_add = findViewById(R.id.buttonNewClientAdd);
        dbHelper = new DBHelper(this);
        cv = new ContentValues();
        db = dbHelper.getWritableDatabase();
        launchType = getIntent().getExtras().getInt("type");
        positionIndex = getIntent().getExtras().getInt("positionIndex");
        onCreatePriceSpinner();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null ){
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        if (launchType == 1) {
            button_add.setText("Сохранить");
            setTitle("Редактировать");
            c = db.query("clientstable", null, null, null, null, null, null);
            c.moveToPosition(getIntent().getExtras().getInt("positionIndex"));
            company.setText(c.getString(c.getColumnIndex("company")));
            city.setText(c.getString(c.getColumnIndex("city")));
            street.setText(c.getString(c.getColumnIndex("street")));
            phone.setText(c.getString(c.getColumnIndex("phone")));
            clientPriceSpinner.setSelection(c.getInt(c.getColumnIndex("price")));
            clientid = c.getString(c.getColumnIndex("id"));

        }





    }

    public void onClickButtonAdd(View v){
        if ((!city.getText().toString().equals("")) &&  (!street.getText().toString().equals("")) && (!company.getText().toString().equals(""))
        && !phone.getText().toString().equals("")) {
            cv.put("company", company.getText().toString());
            cv.put("city", city.getText().toString());
            cv.put("street", street.getText().toString());
            cv.put("phone", phone.getText().toString());
            cv.put("price", clientPriceSpinner.getSelectedItemPosition());




            if (launchType == 0) {
                long rowId = db.insert("clientstable", null, cv);
                dbHelper.close();
                //Toast.makeText(this, "Добавлено, id: " + rowId + ", street: " + city.getText().toString() , Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            if (launchType == 1) {
                db = dbHelper.getWritableDatabase();
                long rowId = db.update("clientstable", cv, "id = " + clientid, null);
                //Toast.makeText(this, "Сохранено, id: " + rowId, Toast.LENGTH_SHORT).show();
                dbHelper.close();
                setResult(RESULT_OK);
                finish();
            }


            }


        else {
            Toast.makeText(this, "Все поля должны быть заполнены",
                   Toast.LENGTH_SHORT).show();

        }

    }



    @Override
    public boolean onSupportNavigateUp() {
       onBackPressed();
       return true;
    }



}
