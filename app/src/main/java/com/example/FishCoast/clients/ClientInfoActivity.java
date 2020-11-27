package com.example.FishCoast.clients;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FishCoast.DBHelper;
import com.example.FishCoast.orders.NewOrderActivity;
import com.example.FishCoast.R;
import com.example.FishCoast.REQUEST_CODE;

public class ClientInfoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DBHelper dbHelper;
    private ContentValues cv;
    private SQLiteDatabase db;
    private Cursor c;
    private TextView clientCity, clientCompany, clientPhone, clientStreet, clientTextId;
    private ClientInfoAdapter clientInfoAdapter;
    private int clientId;
    private int positionIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_info);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        clientStreet = findViewById(R.id.textStreet);
        clientCompany = findViewById(R.id.textCompany);
        clientPhone = findViewById(R.id.textPhone);
        clientCity = findViewById(R.id.textCity);
        clientTextId = findViewById(R.id.textId);

        positionIndex = getIntent().getExtras().getInt("positionIndex");
        initClientInfo(positionIndex);
        initClientInfoAdapter(clientId);
    }

    public Cursor getSortedCursor(int id, int orderid){
        if (orderid > 0){
            return db.query("orderstable", null, "clientid = " + id + " AND " + "orderid = " + orderid,
                    null,null , null,"datetime DESC");
        }
        else return db.query("orderstable", null, "clientid = " + id, null,null , null,"datetime DESC");


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_client_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_client_edit) {
            Intent intent = new Intent(this, ClientEditActivity.class);
            intent.putExtra("type", 1);
            intent.putExtra("positionIndex", positionIndex);
            startActivityForResult(intent, REQUEST_CODE.EDITCLIENT);
        }
        if (item.getItemId() == R.id.action_client_delete) {

            db.delete("clientstable", "id = " + clientId, null);
            dbHelper.close();
            finish();
        }
        if (item.getItemId() == R.id.action_client_add) {
            Intent newOrderintent = new Intent(this, NewOrderActivity.class);
            newOrderintent.putExtra("clientId", clientId);
            newOrderintent.putExtra("isEdit", 0);
            startActivityForResult(newOrderintent, REQUEST_CODE.NEWORDER);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 1){

        }
        if (item.getItemId() == 2){
            Intent newOrderintent = new Intent(this, NewOrderActivity.class);
            newOrderintent.putExtra("clientId", clientId);
            newOrderintent.putExtra("isEdit", 1);
            newOrderintent.putExtra("orderid", clientInfoAdapter.getOrderid(item.getGroupId()));
            startActivityForResult(newOrderintent, REQUEST_CODE.EDITORDER);
        }
        if (item.getItemId() == 3){

        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE.NEWORDER){
            if (resultCode == RESULT_OK){
                finish();
            }
        }
        initClientInfo(positionIndex);
        clientInfoAdapter.swapCursor(getSortedCursor(clientId, 0));
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initClientInfo(int positionIndex) {
        c = db.query("clientstable", null, null, null, null, null, null);
        c.moveToPosition(positionIndex);
        clientStreet.setText(c.getString(c.getColumnIndex("street")));
        clientCompany.setText(c.getString(c.getColumnIndex("company")));
        clientCity.setText(c.getString(c.getColumnIndex("city")));
        clientPhone.setText(c.getString(c.getColumnIndex("phone")));
        clientId = c.getInt(c.getColumnIndex("id"));
        clientTextId.setText("id: " + clientId);
        c.close();
    }

    private void initClientInfoAdapter(int id){
        RecyclerView clientInfoRecycler = findViewById(R.id.clientInfoRecycler);
        LinearLayoutManager clientInfoLayoutManager = new LinearLayoutManager(this);
        clientInfoRecycler.setLayoutManager(clientInfoLayoutManager);
        clientInfoRecycler.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                clientInfoRecycler.getContext(), clientInfoLayoutManager.getOrientation());
        clientInfoRecycler.addItemDecoration(dividerItemDecoration);
        clientInfoAdapter = new ClientInfoAdapter(id, this);
        clientInfoRecycler.setAdapter(clientInfoAdapter);
    }

}