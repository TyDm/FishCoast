package com.example.FishCoast.clients;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FishCoast.DBHelper;
import com.example.FishCoast.orders.NewOrderActivity;
import com.example.FishCoast.R;
import com.example.FishCoast.REQUEST_CODE;

import java.util.Objects;

public class ClientInfoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DBHelper dbHelper;
    private ContentValues cv;
    private SQLiteDatabase db;
    private Cursor c;
    private TextView clientCity, clientCompany, clientPhone, clientStreet, clientTextId;
    private ClientInfoAdapter clientInfoAdapter;
    private RecyclerView clientInfoRecycler;
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
                    null,null , null,"orderdate DESC");
        }
        else return db.query("orderstable", null, "clientid = " + id, null,null , null,"orderdate DESC");


    }

    public Cursor getSortedCursor(int id){
        return db.query("orderstable", null, "clientid = " + id, null,null , null,"orderdate DESC");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_client_info, menu);
        MenuItem searchItem = menu.findItem(R.id.action_client_app_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.replace(" ", "%");
                clientInfoAdapter.swapCursor(db.query("orderstable", null,
                        "name" + " LIKE '%" + newText + "%'" + " AND " + "clientid = " + clientId, null, null,
                        null, "orderdate DESC"), newText);
                return false;
            }
        });
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
            db.delete("orderstable", "clientid = " + clientId, null);
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
        int orderID = clientInfoAdapter.getOrderid(clientInfoAdapter.getClickableID());
        if (item.getItemId() == 1){
            ClipData clipData = ClipData.newPlainText("text", clientInfoAdapter.getOrderText(clientInfoAdapter.getClickableID()));
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            Objects.requireNonNull(clipboardManager).setPrimaryClip(clipData);
            Toast.makeText(this, "Скопировано в буфер обмена", Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == 2){
            Intent newOrderintent = new Intent(this, NewOrderActivity.class);
            newOrderintent.putExtra("clientId", clientId);
            newOrderintent.putExtra("isEdit", 1);
            newOrderintent.putExtra("orderid", orderID);
            startActivityForResult(newOrderintent, REQUEST_CODE.EDITORDER);
        }
        if (item.getItemId() == 3){
            db.delete("orderstable", "orderid = " + orderID, null);
            clientInfoAdapter.swapCursor(getSortedCursor(clientId), "");
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
        clientInfoAdapter.swapCursor(getSortedCursor(clientId), "");
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
        clientInfoRecycler = findViewById(R.id.clientInfoRecycler);
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