package com.example.FishCoast.orders;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FishCoast.DBHelper;
import com.example.FishCoast.R;
import com.example.FishCoast.StringFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder> {

    private Context context;
    private DBHelper dbHelper;
    private Cursor cursor;
    private SQLiteDatabase db;
    private String[] orderTextList;
    private ArrayList<Integer> orderIdList;
    private int clickableID;
    private String clickableText;
    private String searchText = "";



    public OrdersAdapter(Context context){
        this.context = context;
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        cursor = db.query("orderstable", null, null, null,null , null,"orderdate DESC");
        orderIdList = getidList(cursor);
        orderTextList = new String[orderIdList.size()];
    }

    public String getClickableText() {
        return clickableText;
    }

    public void setClickableText(String clickableText) {
        this.clickableText = clickableText;
    }

    public int getClickableID() {
        return clickableID;
    }

    public void setClickableID(int clickableID) {
        this.clickableID = clickableID;
    }

    public int getOrderID(int position){
        return orderIdList.get(position);
    }

    public void swapCursor(Cursor newCursor, String str) {
        if (cursor != null){
            cursor.close();
        }
        cursor = newCursor;
        searchText = str;
        if (newCursor != null){
            orderIdList = getidList(newCursor);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new OrdersViewHolder(inflater.inflate(R.layout.item_orders_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        SpannableStringBuilder orderBuilder = new SpannableStringBuilder();
        StringBuilder costBuilder = new StringBuilder();
        double v = 0;
        double value = 0;
        Cursor bindCursor = db.query("orderstable", null, "orderid = " + orderIdList.get(position), null, null, null , "orderdate DESC");
        while (bindCursor.moveToNext()){
            orderBuilder.append(StringFormat.setSearchSpan(bindCursor.getString(bindCursor.getColumnIndex("name")), searchText, holder.itemView.getResources().getColor(R.color.colorAccent)));
            orderBuilder.append("  ");
            orderBuilder.append(StringFormat.doubleToString(bindCursor.getDouble(bindCursor.getColumnIndex("quantity")),
                    bindCursor.getInt(bindCursor.getColumnIndex("unit"))));
            orderBuilder.append("\n");
            v = bindCursor.getDouble(bindCursor.getColumnIndex("quantity"))*bindCursor.getDouble(bindCursor.getColumnIndex("cost"));
            costBuilder.append(StringFormat.doubleToString(v, -1));
            costBuilder.append("\n");
            value += v;
        }
        holder.order.setText(orderBuilder);
        holder.cost.setText(costBuilder.toString());
        holder.value.setText(StringFormat.doubleToString(value, -1));
        orderTextList[position] = orderBuilder.toString();
        bindCursor.moveToFirst();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(context.getString(R.string.dateTimeFormat), Locale.getDefault());
        try {
            Date currentDate = new Date();
            Date orderDate = dateTimeFormat.parse(bindCursor.getString(bindCursor.getColumnIndex("orderdate")));
            if (SimpleDateFormat.getDateInstance().format(orderDate).equals(SimpleDateFormat.getDateInstance().format(currentDate))) {
                String s = SimpleDateFormat.getTimeInstance().format(orderDate);
                if (Locale.getDefault().getLanguage().equals("ru"))
                    s = s.substring(0, s.length()-3);
                holder.date.setText(s);
            }
            else{
                String s = SimpleDateFormat.getDateInstance().format(orderDate);
                if (Locale.getDefault().getLanguage().equals("ru"))
                    s = s.substring(0, s.length()-8);
                holder.date.setText(s);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            holder.date.setText("");
        }
        int clientID = bindCursor.getInt(bindCursor.getColumnIndex("clientid"));
        bindCursor.close();
        bindCursor = db.query("clientstable", null, "id = " + clientID, null, null, null, null);
        bindCursor.moveToFirst();
        try {
            holder.client.setText(context.getString(R.string.streetCompany, bindCursor.getString(bindCursor.getColumnIndex("street")),
                    bindCursor.getString(bindCursor.getColumnIndex("company"))));
        }
        catch (Exception e) {
            holder.client.setText("Удаленный клиент");
        }
        bindCursor.close();

    }

    @Override
    public int getItemCount() {
        return orderIdList.size();
    }

    private ArrayList<Integer> getidList(Cursor cursor){
        int i;
        int id;
        ArrayList<Integer> list = new ArrayList<>();
        if (cursor.getCount() <= 0) return list;
        cursor.moveToFirst();
        id = cursor.getInt(cursor.getColumnIndex("orderid"));
        list.add(id);
        while (cursor.moveToNext()) {
            if (id != cursor.getInt(cursor.getColumnIndex("orderid"))){
                id = cursor.getInt(cursor.getColumnIndex("orderid"));
                list.add(id);
            }
        }
        return list;
    }

    class OrdersViewHolder extends RecyclerView.ViewHolder{

        private TextView client, date, value;
        private EditText order, cost;

        public OrdersViewHolder(@NonNull View itemView) {

            super(itemView);
            client = itemView.findViewById(R.id.itemOrdersClient);
            date = itemView.findViewById(R.id.itemOrdersDate);
            value = itemView.findViewById(R.id.itemOrdersValue);
            order = itemView.findViewById(R.id.itemOrdersText);
            cost = itemView.findViewById(R.id.itemOrdersCost);

            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    setClickableID(getAdapterPosition());
                    setClickableText(client.getText().toString() + "\n" + order.getText().toString());
                    menu.add(1, 1, 0, "Копировать");
                    menu.add(1, 2, 0, "Редактировать");
                    menu.add(1, 3, 0, "Удалить");
                }

            });
        }
    }

}
