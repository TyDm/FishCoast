package com.example.myapplication.clients;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DBHelper;
import com.example.myapplication.R;

public class ClientsRecyclerAdapter extends RecyclerView.Adapter<ClientsRecyclerAdapter.ClientsViewHolder> {

    private Context context;
    private DBHelper dbHelper;
    private ContentValues cv;
    private SQLiteDatabase db;
    private Cursor c;
    private boolean newOrder;

    public boolean isNewOrder() {
        return newOrder;
    }

    public void setNewOrder(boolean newOrder) {
        this.newOrder = newOrder;
    }

    public ClientsRecyclerAdapter(Context context){

        this.context = context;
        dbHelper = new DBHelper(context);
        cv = new ContentValues();
        db = dbHelper.getWritableDatabase();
        newOrder = false;
    }

    @NonNull
    @Override
    public ClientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_clients_list, parent, false);
        ClientsViewHolder viewHolder = new ClientsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClientsViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        int count = db.query("clientstable", null, null, null, null,null,null).getCount();
        return count;
    }

    class ClientsViewHolder extends RecyclerView.ViewHolder {

        private TextView clientsName;
        private TextView clientsCompany;

        View.OnClickListener clientsRecyclerListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionIndex = getAdapterPosition();

                //Toast.makeText(context, "Элемент " + positionIndex, Toast.LENGTH_SHORT).show();
                if (!newOrder) {
                    ClientsFragment.startClientInfoActivity(positionIndex);
                } else
                {

                }


            }
        };

         private ClientsViewHolder(View itemView) {
            super(itemView);
            clientsName = itemView.findViewById(R.id.itemClientName);
            clientsCompany = itemView.findViewById(R.id.itemClientCompany);

            itemView.setOnClickListener(clientsRecyclerListener);
        }

        void bind(int position) {
            c = db.query("clientstable", null,null, null, null, null, null);
            if (c.moveToPosition(position)) {
                this.clientsName.setText(c.getString(c.getColumnIndex("street")));
                this.clientsCompany.setText(c.getString(c.getColumnIndex("company")));
            }
            else {
                Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show();
            }

        }

    }

}
