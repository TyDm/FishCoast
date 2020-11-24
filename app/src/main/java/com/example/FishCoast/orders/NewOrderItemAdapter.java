package com.example.FishCoast.orders;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FishCoast.DBHelper;
import com.example.FishCoast.R;
import com.example.FishCoast.StringFormat;

import java.util.ArrayList;

public class NewOrderItemAdapter extends RecyclerView.Adapter<NewOrderItemAdapter.NewOrderItemViewHolder>  {
    private final Context context;
    private final NewOrderActivity newOrderActivity;
    private ArrayList<OrderPositionItems> items = new ArrayList<>();
    private SQLiteDatabase db;
    private Cursor c;
    private DBHelper dbHelper;
    private ContentValues cv;
    private Boolean quantityBind = false;
    private LinearLayoutManager linearLayoutManager;
    protected NewOrderItemViewHolder viewHolder;




    public NewOrderItemAdapter(Context context, NewOrderActivity newOrderActivity, LinearLayoutManager linearLayoutManager) {
        this.context = context;
        this.newOrderActivity = newOrderActivity;
        this.linearLayoutManager = linearLayoutManager;
        OrderPositionItems item = new OrderPositionItems("", 0.0, 0, -1.0);
        items.add(item);

    }



    @NonNull
    @Override
    public NewOrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_neworder_item, parent, false);
        viewHolder = new NewOrderItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewOrderItemViewHolder holder, int position) {
            holder.nameText.setText(StringFormat.ItemName( items.get(position).getName()));
            holder.quantityText.setEnabled(false);
            if (!items.get(position).getName().equals("")) {

                if (items.get(position).getQuantity() == -1.0) {
                    holder.quantityText.setEnabled(true);
                    holder.quantityText.setText("");
                    quantityBind = true;
                    holder.quantityText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if ((!hasFocus) && (quantityBind)){
                                items.get(position).setName("");
                                items.get(position).setCost(0.0);
                                items.get(position).setUnit(0);
                                items.get(position).setQuantity(-1.0);
                            }
                            if (hasFocus){
                                newOrderActivity.setnewOrderPriceAdapterClickable(false);
                            }
                        }
                    });
                    holder.quantityText.requestFocus();
                    holder.quantityText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if ((actionId == EditorInfo.IME_ACTION_DONE) && (v == holder.quantityText)){
                                String s = holder.quantityText.getText().toString();
                                if ((s.length() > 2) && (s.endsWith("шт"))){
                                    s = s.substring(0, s.length()-2);
                                }
                                try{
                                    double d;
                                    d = (Double.parseDouble(s));
                                    if (d <= 0){
                                        Toast.makeText(context, "Введите корректное значение", Toast.LENGTH_SHORT).show();
                                        return true;
                                    }
                                    if (((items.get(position).getUnit() == 1) || (!holder.quantityText.getText().toString().equals(s))) && (d % 1 != 0)) {
                                        Toast.makeText(context, "Введите корректное значение", Toast.LENGTH_SHORT).show();
                                        return true;
                                    }
                                    items.get(position).setQuantity(d);
                                    if (!holder.quantityText.getText().toString().equals(s)){
                                        items.get(position).setUnit(1);
                                    }
                                    if (!items.get(items.size()-1).getName().equals("")){
                                        OrderPositionItems item = new OrderPositionItems("", 0.0, 0, -1.0);
                                        items.add(item);
                                    }
                                    quantityBind = false;
                                    linearLayoutManager.scrollToPosition(items.size()-1);
                                    notifyDataSetChanged();
                                    return true;


                                }
                                catch (NumberFormatException | NullPointerException nfe) {
                                    Toast.makeText(context, "Введите корректное значение", Toast.LENGTH_SHORT).show();
                                }
                            }
                            return true;
                        }
                    });
                }
                else
                {
                    holder.quantityText.setText(StringFormat.DoubleToString(items.get(position).getQuantity(), items.get(position).getUnit()));
                }

            }
            else
            {
                holder.quantityText.setText("");
            }

            if ((!quantityBind) && ((holder.getAdapterPosition() + 1) == items.size())) {

                holder.nameText.requestFocus();

            }
    }



    @Override
    public int getItemCount()
    {
        return items.size();
    }

    public ArrayList<OrderPositionItems> getItems(){
        return items;
    }


    class NewOrderItemViewHolder extends RecyclerView.ViewHolder {
        private final EditText nameText;
        private final EditText quantityText;

        private NewOrderItemViewHolder(View itemView)
        {
            super(itemView);
            nameText = itemView.findViewById(R.id.itemNewOrderItemName);
            quantityText = itemView.findViewById(R.id.itemNewOrderItemQuantity);
            nameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        newOrderActivity.filter(nameText.getText().toString(), getAdapterPosition());
                    }
                }
            });
            nameText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (nameText.isFocused())
                        newOrderActivity.filter(s.toString(), getAdapterPosition());

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }

        public void applyItem(String name, String cost, String unit, int position){
            items.get(position).setName(name);
            items.get(position).setCost(Double.parseDouble(cost));
            items.get(position).setUnit(StringFormat.UnitStringtoInteger(unit));
            items.get(position).setQuantity(-1.0);
            quantityBind = true;
            linearLayoutManager.scrollToPositionWithOffset(items.size()-1, 100);
            notifyDataSetChanged();
        }

    }




}
