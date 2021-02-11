package com.example.FishCoast.orders;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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

        setHasOptionsMenu(true);
        dbHelper = new DBHelper(root.getContext());
        db = dbHelper.getWritableDatabase();
        initOrdersListRecycler();

        return root;
    }

    

    public void newOrder () {

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int orderID = ordersAdapter.getOrderID(ordersAdapter.getClickableID());
        if (item.getItemId() == 1){
            ClipData clipData = ClipData.newPlainText("text", ordersAdapter.getClickableText());
            ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
            Objects.requireNonNull(clipboardManager).setPrimaryClip(clipData);
            Toast.makeText(root.getContext(), "Скопировано в буфер обмена", Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == 2){
            try {
                Intent newOrderintent = new Intent(root.getContext(), NewOrderActivity.class);
                Cursor cursor = db.query("orderstable", null, "orderid = " + orderID, null, null, null, null);
                cursor.moveToFirst();
                int clID = cursor.getInt(cursor.getColumnIndex("clientid"));
                newOrderintent.putExtra("clientId", clID);
                newOrderintent.putExtra("isEdit", 1);
                newOrderintent.putExtra("orderid", orderID);
                cursor.close();
                cursor = db.query("clientstable", null, "id = " + clID, null, null, null, null);
                if (cursor.getCount() == 0) {
                    Toast.makeText(root.getContext(), "Невозможно редактировать данный заказ", Toast.LENGTH_SHORT).show();
                }
                else {
                    startActivityForResult(newOrderintent, REQUEST_CODE.EDITORDER);
                }
                cursor.close();
            }
            catch (Exception e){
                Toast.makeText(root.getContext(), "Невозможно редактировать данный заказ", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
        if (item.getItemId() == 3){
            db.delete("orderstable", "orderid = " + orderID, null);
            ordersAdapter.swapCursor(db.query("orderstable", null, null, null,null , null,"datetime DESC"), "");
        }
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.findItem(R.id.action_add).setVisible(true);
        if (getActivity().findViewById(R.id.pricespinner) != null)
            getActivity().findViewById(R.id.pricespinner).setVisibility(View.GONE);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
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
                ordersAdapter.swapCursor(db.query("orderstable", null,
                        "name" + " LIKE '%" + newText + "%'", null, null,
                        null, "datetime DESC"), newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE.EDITORDER && resultCode == RESULT_OK){
            ordersAdapter.swapCursor(db.query("orderstable", null, null, null,null , null,"datetime DESC"), "");
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