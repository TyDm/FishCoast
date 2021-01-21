package com.example.FishCoast.orders;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FishCoast.DBHelper;
import com.example.FishCoast.R;
import com.example.FishCoast.REQUEST_CODE;


import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CLIPBOARD_SERVICE;

public class OrdersFragment extends Fragment {

    private OrdersViewModel ordersViewModel;
    private View root;
    private OrdersAdapter ordersAdapter;
    private DBHelper dbHelper;
    private SQLiteDatabase db;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ordersViewModel =
                ViewModelProviders.of(this).get(OrdersViewModel.class);
        root = inflater.inflate(R.layout.fragment_orders, container, false);
       /* final TextView textView = root.findViewById(R.id.text_orders);
        ordersViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        getActivity().findViewById(R.id.pricespinner).setVisibility(View.GONE);

        setHasOptionsMenu(true);


        initOrdersListRecycler();

        return root;
    }

    public void newOrder () {

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int orderID = ordersAdapter.getOrderID(ordersAdapter.getClickableID());
        dbHelper = new DBHelper(root.getContext());
        db = dbHelper.getWritableDatabase();
        if (item.getItemId() == 1){
            ClipData clipData = ClipData.newPlainText("text", ordersAdapter.getClickableText());
            ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
            Objects.requireNonNull(clipboardManager).setPrimaryClip(clipData);
            Toast.makeText(root.getContext(), "Скопировано в буфер обмена", Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == 2){
            Intent newOrderintent = new Intent(root.getContext(), NewOrderActivity.class);
            Cursor cursor = db.query("orderstable", null, "orderid = " + orderID, null, null, null, null);
            try {
                cursor.moveToFirst();
                newOrderintent.putExtra("clientId", cursor.getInt(cursor.getColumnIndex("clientid")));
                newOrderintent.putExtra("isEdit", 1);
                newOrderintent.putExtra("orderid", orderID);
                startActivityForResult(newOrderintent, REQUEST_CODE.EDITORDER);
            }
            catch (Exception e){
                Toast.makeText(root.getContext(), "Невозможно редактировать данный заказ", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            cursor.close();
        }
        if (item.getItemId() == 3){
            db.delete("orderstable", "orderid = " + orderID, null);
            ordersAdapter.swapCursor(db.query("orderstable", null, null, null,null , null,"datetime DESC"));
        }
        db.close();
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            newOrder();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_delete).setVisible(false);
        menu.findItem(R.id.action_import).setVisible(false);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE.EDITORDER && resultCode == RESULT_OK){
            db = dbHelper.getWritableDatabase();
            ordersAdapter.swapCursor(db.query("orderstable", null, null, null,null , null,"datetime DESC"));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initOrdersListRecycler(){
        RecyclerView ordersRecycler = root.findViewById(R.id.recyclerOrders);
        LinearLayoutManager ordersLayoutManager = new LinearLayoutManager(getContext());
        ordersRecycler.setLayoutManager(ordersLayoutManager);
        ordersRecycler.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ordersRecycler.getContext(), ordersLayoutManager.getOrientation());
        ordersRecycler.addItemDecoration(dividerItemDecoration);
        ordersAdapter = new OrdersAdapter(getContext());
        ordersRecycler.setAdapter(ordersAdapter);

    }

}