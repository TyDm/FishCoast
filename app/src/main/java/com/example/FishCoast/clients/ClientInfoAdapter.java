package com.example.FishCoast.clients;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FishCoast.DBHelper;
import com.example.FishCoast.R;


class ClientInfoAdapter extends RecyclerView.Adapter<ClientInfoAdapter.ClientInfoViewHolder> {

    private Cursor c;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public ClientInfoAdapter(Cursor c) {
        this.c = c;
    }

    @NonNull
    @Override
    public ClientInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_clientinfo_list, parent, false);
        ClientInfoViewHolder viewHolder = new ClientInfoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClientInfoViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        //c = db.query("orderstable", null, "SELECT orderid,COUNT(orderid)", null, "orderid", null, null);
        return c.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (c != null){
            c.close();
        }
        c = newCursor;
        if (newCursor != null){
            notifyDataSetChanged();
        }
    }

    class ClientInfoViewHolder extends RecyclerView.ViewHolder{

        private EditText listItemsText;
        private TextView dateText;

        public ClientInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            listItemsText = itemView.findViewById(R.id.itemClientInfoText);
            dateText = itemView.findViewById(R.id.itemClientInfoDate);
        }
    }
}
