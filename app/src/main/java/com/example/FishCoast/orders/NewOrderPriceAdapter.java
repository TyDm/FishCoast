package com.example.FishCoast.orders;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.FishCoast.R;
import com.example.FishCoast.StringFormat;

import java.util.ArrayList;

class NewOrderPriceAdapter extends RecyclerView.Adapter<NewOrderPriceAdapter.NewOrderPriceViewHolder> {
    private Context context;
    private Cursor c;
    private NewOrderActivity newOrderActivity;
    private int editablePosition;
    private Boolean isClickable = true;
    private String searchText = "";
    private ArrayList<Integer> favoritePositions;

    public NewOrderPriceAdapter(Context context, Cursor cursor, NewOrderActivity newOrderActivity, ArrayList<Integer> favoritePositions ){
        this.context = context;
        c = cursor;
        this.newOrderActivity = newOrderActivity;
        this.favoritePositions = favoritePositions;

    }

    public Boolean getClickable() {
        return isClickable;
    }

    public void setClickable(Boolean clickable) {
        isClickable = clickable;
    }

    @NonNull
    @Override
    public NewOrderPriceAdapter.NewOrderPriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_neworder_price, parent, false);

        return new NewOrderPriceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewOrderPriceAdapter.NewOrderPriceViewHolder holder, int position) {
        holder.bind(position);
    }


    @Override
    public int getItemCount() {
        return c.getCount();
    }

    public void swapCursor(Cursor newCursor, int editablePosition, String str) {
        if (c != null){
            c.close();
        }
        c = newCursor;
        searchText = str;
        if (newCursor != null){
            isClickable = true;
            this.editablePosition = editablePosition;
            notifyDataSetChanged();
        }


    }

    public void setEditablePosition(int editablePosition){
        this.editablePosition = editablePosition;
    }

    class NewOrderPriceViewHolder extends RecyclerView.ViewHolder {

        private TextView positionName;
        private TextView positionCost;
        private TextView positionUnit;

        View.OnClickListener priceRecyclerListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickable){
                    int positionIndex = getAdapterPosition();
                    newOrderActivity.applyItem(getPositionNameText(), getPositionCostText(),
                            getPositionUnitText(), editablePosition);
                }
            }
        };

        public String getPositionNameText() {
            return positionName.getText().toString();
        }

        public String getPositionCostText() {
            return positionCost.getText().toString();
        }

        public String getPositionUnitText() {
            return positionUnit.getText().toString();
        }

        private NewOrderPriceViewHolder(View itemView) {
            super(itemView);
            positionName = itemView.findViewById(R.id.itemNewOrderPricePositionName);
            positionCost = itemView.findViewById(R.id.itemNewOrderPricePositionCost);
            positionUnit = itemView.findViewById(R.id.itemNewOrderPricePositionUnit);

            itemView.setOnClickListener(priceRecyclerListener);
        }

        private void bind(int position) {

            if (!c.moveToPosition(position)){
                return;
            }
            this.positionName.setText(StringFormat.setSearchSpan(c.getString(c.getColumnIndex("name")), searchText,
                    newOrderActivity.getResources().getColor(R.color.colorAccent)));
            this.positionCost.setText(c.getString(c.getColumnIndex("cost")));
            if (c.getInt(c.getColumnIndex("unit")) == 0) this.positionUnit.setText("кг");
            else this.positionUnit.setText("шт");

            if (favoritePositions.contains(c.getInt(c.getColumnIndex("id"))))
                itemView.setBackgroundColor(newOrderActivity.getResources().getColor(R.color.priceBackgroundFavorite));
            else
                itemView.setBackgroundColor(Color.TRANSPARENT);

        }


    }

}
