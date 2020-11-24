package com.example.FishCoast.orders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.FishCoast.DBHelper;
import com.example.FishCoast.R;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewOrderActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private Toolbar toolbar;
    private SQLiteDatabase db;
    private Cursor c;
    private int clientId;
    private TextView clientText;
    private RecyclerView newOrderItemListRecycler;
    private RecyclerView newOrderPriceListRecycler;
    private NewOrderItemAdapter newOrderItemAdapter;
    private NewOrderPriceAdapter newOrderPriceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        clientId = Integer.parseInt(getIntent().getStringExtra("clientId"));
        c = db.query("clientstable", null, "id = " + clientId, null, null, null, null);
        c.moveToFirst();
        getSupportActionBar().setTitle(c.getString(c.getColumnIndex("street")));

        initPriceListRecycler();
        initItemListRecycler();
    }

    public void initPriceListRecycler() {
        newOrderPriceListRecycler = findViewById(R.id.recyclerNewOrderPrice);
        LinearLayoutManager newOrderPriceLayoutManager = new LinearLayoutManager(this);
        newOrderPriceListRecycler.setLayoutManager(newOrderPriceLayoutManager);
        newOrderPriceListRecycler.setHasFixedSize(false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                newOrderPriceListRecycler.getContext(), newOrderPriceLayoutManager.getOrientation());
        newOrderPriceListRecycler.addItemDecoration(dividerItemDecoration);

        /*priceItems.clear();
        Cursor cursor = getAllItems(c.getInt(c.getColumnIndex("price")));
        OrderPriceItems item;
        int i = 0;
        while (cursor.moveToNext()){
            //cursor.moveToPosition(i);
            item = new OrderPriceItems(cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getDouble(cursor.getColumnIndex("cost")), cursor.getInt(cursor.getColumnIndex("unit")));
            priceItems.add(item);
           // i++;
        }*/
        newOrderPriceAdapter = new NewOrderPriceAdapter(this, getAllItems(c.getInt(c.getColumnIndex("price"))), this);
        newOrderPriceListRecycler.setAdapter(newOrderPriceAdapter);

    }

    public void initItemListRecycler() {
        newOrderItemListRecycler = findViewById(R.id.recyclerNewOrderItems);
        LinearLayoutManager newOrderItemsLayoutManager = new LinearLayoutManager(this);
        newOrderItemListRecycler.setLayoutManager(newOrderItemsLayoutManager);
        newOrderItemListRecycler.setHasFixedSize(false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                newOrderItemListRecycler.getContext(), newOrderItemsLayoutManager.getOrientation());
        newOrderItemListRecycler.addItemDecoration(dividerItemDecoration);
        newOrderItemAdapter = new NewOrderItemAdapter(this, this, newOrderItemsLayoutManager);
        newOrderItemListRecycler.setAdapter(newOrderItemAdapter);
    }


    private Cursor getAllItems(int priceType) {
        return db.query("pricetable", null, "type = " + priceType, null, null, null, null);
    }


    public void filter(String str, int editablePosition) {
        newOrderPriceAdapter.swapCursor(db.query("pricetable", null,
                "name" + " LIKE '%" + str + "%'" + " AND " + "type = " + c.getInt(c.getColumnIndex("price")), null, null,
                null, null), editablePosition);
    }

    public void applyItem(String name, String cost, String unit, int editablePosition) {

        newOrderItemAdapter.viewHolder.applyItem(name, cost, unit, editablePosition);
    }

    public void setEditablePosition(int editablePosition) {
        newOrderPriceAdapter.setEditablePosition(editablePosition);
    }

    public void setnewOrderPriceAdapterClickable(boolean clickable){
        newOrderPriceAdapter.setClickable(clickable);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_neworder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_neworder_apply){
            ArrayList<OrderPositionItems> items = newOrderItemAdapter.getItems();
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String orderDateTime = SimpleDateFormat.getDateTimeInstance().format(date);
            ContentValues cv = new ContentValues();
            int orderid = 1;
            Cursor orderCursor = db.query("orderstable", new String[] {"MAX(orderid)"}, null, null,null,null,null);
            if (orderCursor.getCount() > 0){
                orderCursor.moveToFirst();
                String max_id = orderCursor.getString(0);
                if (max_id != null)
                orderid = Integer.parseInt(max_id)+1;
            }
            orderCursor.close();

            //Toast.makeText(this, "" + orderid, Toast.LENGTH_SHORT).show();*/

            int i = 0;
            while (i < items.size()){
                if (!items.get(i).getName().equals("") && (items.get(i).getQuantity() > 0)) {
                    cv.put("orderid", orderid);
                    cv.put("clientid", clientId);
                    cv.put("name", items.get(i).getName());
                    cv.put("cost", items.get(i).getCost());
                    cv.put("unit", items.get(i).getUnit());
                    cv.put("quantity", items.get(i).getQuantity());
                    cv.put("datetime", orderDateTime);
                    db.insert("orderstable", null, cv);
                }
                i++;
            }
            setResult(RESULT_OK);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}