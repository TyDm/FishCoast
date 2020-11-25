package com.example.FishCoast.orders;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.FishCoast.R;

class NewOrderPriceAdapter extends RecyclerView.Adapter<NewOrderPriceAdapter.NewOrderPriceViewHolder> {
    private Context context;
    private Cursor c;
    private NewOrderActivity newOrderActivity;
    private int editablePosition;
    private Boolean isClickable = true;

    public NewOrderPriceAdapter(Context context, Cursor cursor, NewOrderActivity newOrderActivity ){

        this.context = context;
        c = cursor;
        this.newOrderActivity = newOrderActivity;

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

    public void swapCursor(Cursor newCursor, int editablePosition) {
        if (c != null){
            c.close();
        }
        c = newCursor;
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

            this.positionName.setText(c.getString(c.getColumnIndex("name")));
            this.positionCost.setText(c.getString(c.getColumnIndex("cost")));
            if (c.getInt(c.getColumnIndex("unit")) == 0) this.positionUnit.setText("кг");
            else this.positionUnit.setText("шт");
           //this.positionName.setText(items.get(position).getName());
          // this.positionCost.setText(StringFormat.DoubleToString(items.get(position).getCost()));
         //  if (items.get(position).getUnit() == 0) this.positionUnit.setText("кг");
          // else this.positionUnit.setText("шт");



        }


    }

}
