package com.example.FishCoast.clients;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FishCoast.DBHelper;
import com.example.FishCoast.R;
import com.example.FishCoast.StringFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


class ClientInfoAdapter extends RecyclerView.Adapter<ClientInfoAdapter.ClientInfoViewHolder> {

    private Cursor c;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private ArrayList<Integer> idList;
    private final int clientID;
    private ClientInfoActivity clientInfoActivity;

    public ClientInfoAdapter(int id, ClientInfoActivity activity) {
        c = activity.getSortedCursor(id, 0);
        clientID = id;
        clientInfoActivity = activity;
        idList = new ArrayList<>(getidList(c));
        //
    }

    @NonNull
    @Override
    public ClientInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_clientinfo_list, parent, false);
        return new ClientInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientInfoViewHolder holder, int position) {
        Cursor bindCursor;
        bindCursor = clientInfoActivity.getSortedCursor(clientID, idList.get(position));
        StringBuilder str = new StringBuilder();
        while (bindCursor.moveToNext()){

            str.append(bindCursor.getString(bindCursor.getColumnIndex("name")));
            str.append("  ");
            str.append(StringFormat.itemQuantity(bindCursor.getDouble(bindCursor.getColumnIndex("quantity")),
                    bindCursor.getInt(bindCursor.getColumnIndex("unit"))));
            str.append("\n");
        }
        holder.listItemsText.setText(str.toString());

        bindCursor.moveToFirst();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(clientInfoActivity.getString(R.string.dateTimeFormat), Locale.getDefault());
        try {
            holder.dateText.setText(SimpleDateFormat.getDateInstance().format(dateTimeFormat.parse(bindCursor.getString(bindCursor.getColumnIndex("datetime")))));
        }
        catch (ParseException e){
            e.printStackTrace();
            holder.dateText.setText("");
        }

        holder.orderid = bindCursor.getInt(c.getColumnIndex("orderid"));

        bindCursor.close();

    }

    @Override
    public int getItemCount() {
        return idList.size();
    }

    public void swapCursor(Cursor newCursor) {
        if (c != null){
            c.close();
        }
        c = newCursor;
        if (newCursor != null){
            idList = getidList(newCursor);
            notifyDataSetChanged();
        }
    }

    public int getOrderid(int position){
        return idList.get(position);
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

    class ClientInfoViewHolder extends RecyclerView.ViewHolder{

        private EditText listItemsText;
        private TextView dateText;
        private int orderid;

        public EditText getListItemsText() {
            return listItemsText;
        }

        public ClientInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            listItemsText = itemView.findViewById(R.id.itemClientInfoText);
            dateText = itemView.findViewById(R.id.itemClientInfoDate);
            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.add(getAdapterPosition(), 1, 0, "Копировать");
                    menu.add(getAdapterPosition(), 2, 0, "Редактировать");
                    menu.add(getAdapterPosition(), 3, 0, "Удалить");
                }

            });
        }
    }
}
