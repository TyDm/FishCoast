package com.example.FishCoast.orders;

import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FishCoast.R;

public class OrdersFragment extends Fragment {

    private OrdersViewModel ordersViewModel;
    private View root;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ordersViewModel =
                ViewModelProviders.of(this).get(OrdersViewModel.class);
        root = inflater.inflate(R.layout.fragment_orders, container, false);
       /* final TextView textView = root.findViewById(R.id.text_orders);
        ordersViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        getActivity().findViewById(R.id.pricespinner).setVisibility(View.GONE);

        setHasOptionsMenu(true);


        initOrdersListRecycler();

        return root;
    }

    public void newOrder () {

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            newOrder();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_delete).setVisible(false);
        menu.findItem(R.id.action_import).setVisible(false);

    }

    private void initOrdersListRecycler(){
        RecyclerView ordersRecycler = root.findViewById(R.id.recyclerOrders);
        LinearLayoutManager ordersLayoutManager = new LinearLayoutManager(getContext());
        ordersRecycler.setLayoutManager(ordersLayoutManager);
        ordersRecycler.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ordersRecycler.getContext(), ordersLayoutManager.getOrientation());
        ordersRecycler.addItemDecoration(dividerItemDecoration);
        OrdersAdapter ordersAdapter = new OrdersAdapter(getContext());
        ordersRecycler.setAdapter(ordersAdapter);

    }

}