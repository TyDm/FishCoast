package com.example.FishCoast.clients;


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

import com.example.FishCoast.DBHelper;
import com.example.FishCoast.R;
import com.example.FishCoast.StringFormat;

public class ClientsRecyclerAdapter extends RecyclerView.Adapter<ClientsRecyclerAdapter.ClientsViewHolder> {

    private Context context;
    private DBHelper dbHelper;
    private ContentValues cv;
    private SQLiteDatabase db;
    private Cursor c;
    private String searchText = "";

    public ClientsRecyclerAdapter(Context context, Cursor cursor){
        this.context = context;
        c = cursor;
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
        return c.getCount();
    }

    public void swapCursor(Cursor newCursor, String str) {
        if (c != null){
            c.close();
        }
        c = newCursor;
        searchText = str;
        if (newCursor != null){
            notifyDataSetChanged();
        }

    }

    class ClientsViewHolder extends RecyclerView.ViewHolder {

        private TextView clientsName;
        private TextView clientsCompany;

        View.OnClickListener clientsRecyclerListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionIndex = getAdapterPosition();
                ClientsFragment.startClientInfoActivity(positionIndex);
            }
        };

         private ClientsViewHolder(View itemView) {
            super(itemView);
            clientsName = itemView.findViewById(R.id.itemClientName);
            clientsCompany = itemView.findViewById(R.id.itemClientCompany);

            itemView.setOnClickListener(clientsRecyclerListener);
        }

        void bind(int position) {
            if (!c.moveToPosition(position)){
                return;
            }
             this.clientsName.setText(StringFormat.setSearchSpan(c.getString(c.getColumnIndex("street")), searchText, itemView.getResources().getColor(R.color.colorAccent)));
             this.clientsCompany.setText(StringFormat.setSearchSpan(c.getString(c.getColumnIndex("company")), searchText, itemView.getResources().getColor(R.color.colorAccent)));
        }

    }

}
