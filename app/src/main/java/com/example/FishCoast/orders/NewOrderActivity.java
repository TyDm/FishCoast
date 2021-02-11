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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.FishCoast.DBHelper;
import com.example.FishCoast.R;


import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NewOrderActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private Toolbar toolbar;
    private SQLiteDatabase db;
    private Cursor c;
    private int clientId;
    private int isEdit;
    private int priceType;
    private TextView clientText;
    private ArrayList<Integer> favoritePosition = new ArrayList<>();
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
        clientId = getIntent().getIntExtra("clientId", 0);
        isEdit = getIntent().getIntExtra("isEdit", 0);
        c = db.query("clientstable", null, "id = " + clientId, null, null, null, null);
        c.moveToFirst();
        priceType = c.getInt(c.getColumnIndex("price"));
        getSupportActionBar().setTitle(c.getString(c.getColumnIndex("street")));
        c = db.query(true, "orderstable", null, "clientid = " + clientId, null, "name", null, null, null);
        while (c.moveToNext()){
            String name = c.getString(c.getColumnIndex("name"));
            Cursor priceCursor = db.query("pricetable", null, "type = " + priceType + " AND " + "name = '" + name + "'", null, null ,null, null);
            if (priceCursor.moveToNext()){
                favoritePosition.add(priceCursor.getInt(priceCursor.getColumnIndex("id")));
            }
            priceCursor.close();
        }

        initPriceListRecycler();
        initItemListRecycler();
    }

    public void filter(String str, int editablePosition) {
        str = str.replace(" ", "%");
        newOrderPriceAdapter.swapCursor(db.query("pricetable", null,
                "name" + " LIKE '%" + str + "%'" + " AND " + "type = " + priceType, null, null,
                null, null), editablePosition, str);

    }

    public void applyItem(String name, String cost, String unit, int editablePosition) {
        newOrderItemAdapter.applyItem(name, cost, unit, editablePosition);
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
            if (saveOrder() > 0){
                setResult(RESULT_OK);
            }
            dbHelper.close();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private int saveOrder(){
        int itemsSavecount = 0;
        if (newOrderItemAdapter.getItems().equals(newOrderItemAdapter.getLastItems()) ){
            return itemsSavecount;
        }
        ArrayList<OrderPositionItems> items = newOrderItemAdapter.getItems();
        String orderDateTime;
        int orderid;
        if (isEdit == 1){
            orderid = getIntent().getIntExtra("orderid", 0);
            Cursor cursor = db.query("orderstable", null, "orderid = " + orderid, null, null, null, null);
            cursor.moveToFirst();
            orderDateTime = cursor.getString(cursor.getColumnIndex("datetime"));
            db.delete("orderstable", "orderid = " + orderid, null);
            cursor.close();
        }
        else
        {
            orderid = 1;
            Cursor orderCursor = db.query("orderstable", new String[] {"MAX(orderid)"}, null, null,null,null,null);
            if (orderCursor.getCount() > 0){
                orderCursor.moveToFirst();
                String max_id = orderCursor.getString(0);
                if (max_id != null)
                    orderid = Integer.parseInt(max_id)+1;
            }
            orderCursor.close();
            SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateTimeFormat), Locale.getDefault());
            Date date = new Date();
            orderDateTime = dateFormat.format(date);
        }
        ContentValues cv = new ContentValues();
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
                itemsSavecount++;
            }
            i++;
        }
        return itemsSavecount;
    }

    private void initPriceListRecycler() {
        RecyclerView newOrderPriceListRecycler = findViewById(R.id.recyclerNewOrderPrice);
        LinearLayoutManager newOrderPriceLayoutManager = new LinearLayoutManager(this);
        newOrderPriceListRecycler.setLayoutManager(newOrderPriceLayoutManager);
        newOrderPriceListRecycler.setHasFixedSize(true);
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
        newOrderPriceAdapter = new NewOrderPriceAdapter(this, getAllItems(priceType), this, favoritePosition);
        newOrderPriceListRecycler.setAdapter(newOrderPriceAdapter);

    }

    private void initItemListRecycler() {
        RecyclerView newOrderItemListRecycler = findViewById(R.id.recyclerNewOrderItems);
        LinearLayoutManager newOrderItemsLayoutManager = new LinearLayoutManager(this);
        newOrderItemListRecycler.setLayoutManager(newOrderItemsLayoutManager);
        newOrderItemListRecycler.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                newOrderItemListRecycler.getContext(), newOrderItemsLayoutManager.getOrientation());
        newOrderItemListRecycler.addItemDecoration(dividerItemDecoration);
        if (isEdit == 0){
            newOrderItemAdapter = new NewOrderItemAdapter(this, this, newOrderItemsLayoutManager);
        }
        else
        {
            int orderid = getIntent().getIntExtra("orderid", 0);
            if (orderid > 0){
                ArrayList<OrderPositionItems> items = new ArrayList<>();
                OrderPositionItems item;
                Cursor cursor = db.query("orderstable", null, "orderid = " + orderid, null, null, null, null);
                while (cursor.moveToNext()){
                    item = new OrderPositionItems(cursor.getString(cursor.getColumnIndex("name")), cursor.getDouble(cursor.getColumnIndex("cost")),
                            cursor.getInt(cursor.getColumnIndex("unit")), cursor.getDouble(cursor.getColumnIndex("quantity")));
                    items.add(item);
                }
                cursor.close();
                newOrderItemAdapter = new NewOrderItemAdapter(this, this, newOrderItemsLayoutManager, items);

            }
            else newOrderItemAdapter = new NewOrderItemAdapter(this, this, newOrderItemsLayoutManager);
        }

        newOrderItemListRecycler.setAdapter(newOrderItemAdapter);
    }

    private Cursor getAllItems(int priceType) {
        return db.query("pricetable", null, "type = " + priceType, null, null, null, null);
    }
}