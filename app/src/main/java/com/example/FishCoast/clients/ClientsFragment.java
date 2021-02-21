package com.example.FishCoast.clients;

import android.content.Intent;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FishCoast.DBHelper;
import com.example.FishCoast.R;
import com.example.FishCoast.REQUEST_CODE;
import com.example.FishCoast.StringFormat;


import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ClientsFragment extends Fragment {

    private ClientsViewModel clientsViewModel;
    private View root;
    private RecyclerView clientsListRecycler;
    private ClientsRecyclerAdapter clientsRecyclerAdapter;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private static ClientsFragment сontext;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //-------------------------------------------------------------------------------
        clientsViewModel = ViewModelProviders.of(this).get(ClientsViewModel.class);
        root = inflater.inflate(R.layout.fragment_clients, container, false);
        /*final TextView textView = root.findViewById(R.id.text_clients);
        clientsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);

        });*/
        //---------------------------------------------------------------------------------
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();
        initClientsListRecycler();
        сontext = this;
        return root;

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.findItem(R.id.action_add).setVisible(true);
        if (getActivity().findViewById(R.id.pricespinner) != null)
            getActivity().findViewById(R.id.pricespinner).setVisibility(View.GONE);

        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.replace(" ", "%");
                clientsRecyclerAdapter.swapCursor(db.query("clientstable", null,
                        "street" + " LIKE '%" + newText + "%'" + " OR " + "company" + " LIKE '%" + newText + "%'", null, null,
                        null, null), newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public static void startClientInfoActivity(int positionIndex) {
        Intent intent = new Intent(сontext.getContext(), ClientInfoActivity.class);
        intent.putExtra("positionIndex", positionIndex);
        сontext.startActivityForResult(intent, REQUEST_CODE.CLIENTINFO);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add){
            Intent intent = new Intent(getContext(), ClientEditActivity.class);
            intent.putExtra("type", 0);
            startActivityForResult(intent, REQUEST_CODE.NEWCLIENT);

        }
        if (item.getItemId() == R.id.action_delete){

                dbHelper = new DBHelper(getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete("clientstable", null, null);
                initClientsListRecycler();
                Toast.makeText(getContext(), "Список клиентов удален", Toast.LENGTH_SHORT).show();
                dbHelper.close();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((resultCode == RESULT_OK) && (requestCode == REQUEST_CODE.NEWCLIENT)) {
           initClientsListRecycler();
           startClientInfoActivity(clientsRecyclerAdapter.getItemCount()-1);
        }
        if (requestCode == REQUEST_CODE.CLIENTINFO) {
           initClientsListRecycler();
        }
    }

    private void initClientsListRecycler() {
        //------------------Recycler-----------------------------
        clientsListRecycler = root.findViewById(R.id.recyclerClients);
        LinearLayoutManager clientsLayoutManager = new LinearLayoutManager(getContext());
        clientsListRecycler.setLayoutManager(clientsLayoutManager);
        clientsListRecycler.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                clientsListRecycler.getContext(), clientsLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(StringFormat.getCustomDivider(getContext(), getResources()));
        clientsListRecycler.addItemDecoration(dividerItemDecoration);
        clientsRecyclerAdapter = new ClientsRecyclerAdapter(getContext(), db.query("clientstable", null,null, null, null, null, null));
        clientsListRecycler.setAdapter(clientsRecyclerAdapter);
    }

}