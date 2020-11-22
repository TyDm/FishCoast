package com.example.myapplication.clients;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DBHelper;
import com.example.myapplication.R;
import com.example.myapplication.REQUEST_CODE;


import static android.app.Activity.RESULT_OK;

public class ClientsFragment extends Fragment {

    private ClientsViewModel clientsViewModel;
    private View root;
    private RecyclerView clientsListRecycler;
    private ClientsRecyclerAdapter clientsRecyclerAdapter;
    private DBHelper dbHelper;
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
        getActivity().findViewById(R.id.pricespinner).setVisibility(View.GONE);


        initClientsListRecycler();

        сontext = this;

        return root;

    }

    public static void startClientInfoActivity(int positionIndex) {
        Intent intent = new Intent(сontext.getContext(), ClientInfoActivity.class);
        intent.putExtra("positionIndex", positionIndex);
        сontext.startActivityForResult(intent, REQUEST_CODE.CLIENTINFO);
    }


    private void initClientsListRecycler() {
        //------------------Recycler-----------------------------
        clientsListRecycler = root.findViewById(R.id.recyclerClients);
        LinearLayoutManager clientsLayoutManager = new LinearLayoutManager(getContext());
        clientsListRecycler.setLayoutManager(clientsLayoutManager);
        clientsListRecycler.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                clientsListRecycler.getContext(), clientsLayoutManager.getOrientation());
        clientsListRecycler.addItemDecoration(dividerItemDecoration);
        clientsRecyclerAdapter = new ClientsRecyclerAdapter(getContext());
        clientsListRecycler.setAdapter(clientsRecyclerAdapter);
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
        }
        if (requestCode == REQUEST_CODE.CLIENTINFO) {
           initClientsListRecycler();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_import).setVisible(false);
    }
}