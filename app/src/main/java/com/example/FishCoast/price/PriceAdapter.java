package com.example.FishCoast.price;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FishCoast.R;
import com.example.FishCoast.StringFormat;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.PriceViewHolder> {
    private Context context;
    private Cursor c;
    private String searchText = "";

    public PriceAdapter(Context context, Cursor cursor){

        this.context = context;
        c = cursor;


    }

    @NonNull
    @Override
    public PriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_price_list, parent, false);
        PriceAdapter.PriceViewHolder viewHolder = new PriceAdapter.PriceViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PriceViewHolder holder, int position) {
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



    class PriceViewHolder extends RecyclerView.ViewHolder {

        private TextView positionName;
        private TextView positionCost;
        private TextView positionUnit;

        View.OnClickListener priceRecyclerListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionIndex = getAdapterPosition();
                Toast.makeText(context, "Элемент " + positionIndex, Toast.LENGTH_SHORT).show();

            }
        };

        private PriceViewHolder(View itemView) {
            super(itemView);
            positionName = itemView.findViewById(R.id.itemPositionName);
            positionCost = itemView.findViewById(R.id.itemPositionCost);
            positionUnit = itemView.findViewById(R.id.itemPositionUnit);

            itemView.setOnClickListener(priceRecyclerListener);
        }

        void bind(int position) {

            if (!c.moveToPosition(position)){
                return;
            }

            this.positionName.setText(StringFormat.setSearchSpan(c.getString(c.getColumnIndex("name")), searchText, itemView.getResources().getColor(R.color.colorAccent)));
            this.positionCost.setText(c.getString(c.getColumnIndex("cost")));
            if (c.getInt(c.getColumnIndex("unit")) == 0) this.positionUnit.setText("кг");
            else this.positionUnit.setText("шт");


        }


    }

}
