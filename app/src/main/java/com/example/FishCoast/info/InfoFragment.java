package com.example.FishCoast.info;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.FishCoast.DBHelper;
import com.example.FishCoast.R;

import java.util.Locale;

public class InfoFragment extends Fragment {

    private View root;
    private final int procentOpt = 2;
    private final int procentErsh = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_info, container, false);

        setHasOptionsMenu(true);
        TextView saleInfo = root.findViewById(R.id.info_sale);

        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("orderstable", null, null, null, null, null, null);
        double sum = 0;
        double profit = 0;
        double profitopt = 0;
        double profitersh = 0;
        while (c.moveToNext()){
            int id = c.getInt(c.getColumnIndex("clientid"));
            Cursor clientCursor = db.query("clientstable", null, "id = " + id, null, null, null, null);
            clientCursor.moveToFirst();
            int pricetype = clientCursor.getInt(clientCursor.getColumnIndex("price"));
            double s = c.getDouble(c.getColumnIndex("cost"))*c.getDouble(c.getColumnIndex("quantity"));
            sum+= s;
            if (pricetype == 1){
                profitersh+= s/100*procentErsh;
            }
            else {
                profitopt+= s/100*procentOpt;
            }
        }
        profit = profitersh+profitopt;
        saleInfo.setText("Сумма: " + String.format(Locale.getDefault(), "%1$1.1f", sum)
                + "  Профит: " + String.format(Locale.getDefault(), "%1$1.1f", profit)
                + "\nЁрши: " + String.format(Locale.getDefault(), "%1$1.1f", profitersh)
                + "\nСклад-опт: " + String.format(Locale.getDefault(), "%1$1.1f", profitopt));
        c.close();
        db.close();
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}