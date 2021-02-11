package com.example.FishCoast.price;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;


import static android.app.Activity.RESULT_OK;

public class PriceFragment extends Fragment {

    private View root;
    private PriceViewModel priceViewModel;
    private RecyclerView pricelistRecycler;
    private PriceAdapter priceAdapter;
    private ActionBar actionbar;
    private Spinner priceSpinner;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private ContentValues cv;
    private static final String TAG = "MyApp";

    static {
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl"
        );
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        priceViewModel =
                ViewModelProviders.of(this).get(PriceViewModel.class);

        root = inflater.inflate(R.layout.fragment_price, container, false);
        actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);

        setHasOptionsMenu(true);
        dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();
        initPricelistRecycler();

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (getActivity().findViewById(R.id.pricespinner) != null ){
            getActivity().findViewById(R.id.pricespinner).setVisibility(View.VISIBLE);
            onCreatePriceSpinner();
        }
        menu.findItem(R.id.action_import).setVisible(true);
        menu.findItem(R.id.app_bar_search).setVisible(true);
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
                priceAdapter.swapCursor(db.query("pricetable", null,
                        "name" + " LIKE '%" + newText + "%'" + " AND " + "type = " + priceSpinner.getSelectedItemPosition(), null, null,
                        null, null), newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add){
            Intent intent = new Intent(getContext(), NewPositionActivity.class);
            intent.putExtra("pricetype", priceSpinner.getSelectedItemPosition());
            startActivityForResult(intent, REQUEST_CODE.NEWPOSITION);
        }
        if (item.getItemId() == R.id.action_import){
            Intent xlsxPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            xlsxPickerIntent.setType("*/*");
            startActivityForResult(xlsxPickerIntent, REQUEST_CODE.OPENEXTERNAL);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE.NEWPOSITION) {
            if (resultCode == RESULT_OK){
                priceAdapter.swapCursor(getAllItems(priceSpinner.getSelectedItemPosition()), "");
            }
        }
        if (requestCode == REQUEST_CODE.OPENEXTERNAL){
            if (resultCode == RESULT_OK){
                Uri xlsxFileUri = data.getData();
                    importXlsx(xlsxFileUri, priceSpinner.getSelectedItemPosition());
            }
        }
    }

    private void initPricelistRecycler(){
        pricelistRecycler = root.findViewById(R.id.recyclerPrices);
        LinearLayoutManager priceLayoutManager = new LinearLayoutManager(getContext());
        pricelistRecycler.setLayoutManager(priceLayoutManager);
        pricelistRecycler.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                pricelistRecycler.getContext(), priceLayoutManager.getOrientation());
        pricelistRecycler.addItemDecoration(dividerItemDecoration);
        priceAdapter = new PriceAdapter(getContext(),
                getAllItems(0));
        pricelistRecycler.setAdapter(priceAdapter);
    }

    private void onCreatePriceSpinner(){

        priceSpinner = (Spinner)getActivity().findViewById(R.id.pricespinner);
        String[] data = {"Склад-опт", "Ёршъ", "Безнал", "Цех", "Розница"};
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),R.array.prices_array ,
                R.layout.item_price_spinner );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceSpinner.setAdapter(spinnerAdapter);

        priceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                priceAdapter.swapCursor(getAllItems(position), "");

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private Cursor getAllItems(int priceType){
        return db.query("pricetable", null,"type = " + priceType,null,null,null,null);
    }

    private void importXlsx(Uri uri, int priceSpinnerPosition) {
        try {
            InputStream is = getActivity().getContentResolver().openInputStream(uri);

            XSSFWorkbook wb = new XSSFWorkbook(is);
            is.close();
            int i = -1;
            XSSFRow row;
            XSSFCell cell;

            XSSFSheet sheet = wb.getSheetAt(0);

            //Toast.makeText(getContext(), "" + sheet.getLastRowNum(), Toast.LENGTH_SHORT).show();


            do {
                i++;
                row = sheet.getRow(i);
                cell = row.getCell(1);
                Log.i(TAG, "find, i= " + i);


            }
            while ( ((!cell.getStringCellValue().equals("Наименование")) &&
                    (!cell.getStringCellValue().equals("наименование")) && (!cell.getStringCellValue().equals("НАИМЕНОВАНИЕ")) ) && (i < sheet.getLastRowNum()));



            if ((cell.getStringCellValue().equals("Наименование")) || (cell.getStringCellValue().equals("наименование")) || (cell.getStringCellValue().equals("НАИМЕНОВАНИЕ")) ) {
                String positionCategory = "";
                cv = new ContentValues();

                db.delete("pricetable", "type = " + priceSpinnerPosition, null);
                Log.i(TAG, "Начало импорта");

                do {
                    i++;
                    Log.i(TAG, "i=" + i);
                    // if ( (sheet.getRow(i).getCell(0).getCellTypeEnum() == CellType.STRING)
                    //                & (!sheet.getRow(i).getCell(1).getStringCellValue().equals("")) )
                    if ( (sheet.getRow(i).getCell(0).getRawValue() == null)
                            & (!sheet.getRow(i).getCell(1).getStringCellValue().equals("")) )
                    {
                        positionCategory = sheet.getRow(i).getCell(1).getStringCellValue();
                        Log.i(TAG, "Категория изменена");
                    }

                    Log.i(TAG, "След условие");

                    if  (sheet.getRow(i).getCell(0).getCellTypeEnum() == CellType.NUMERIC){
                        row = sheet.getRow(i);
                        cv.put("type", priceSpinnerPosition);
                        cv.put("name", row.getCell(1).getStringCellValue());
                        cv.put("category", positionCategory);
                        Log.i(TAG, "Категория добавлена");

                        if (row.getCell(3).getCellTypeEnum() == CellType.NUMERIC) {
                            cv.put("cost", row.getCell(3).getNumericCellValue());
                        }
                        else {
                            cv.put("cost", 0);
                        }

                        Log.i(TAG, "Цена добавлена");

                        if (row.getCell(2).getStringCellValue().equals("кг")) {
                            cv.put("unit", 0);
                        } else
                        {
                            cv.put("unit", 1);
                        }

                        db.insert("pricetable", null, cv);

                    }


                }
                while (i < sheet.getLastRowNum());

                wb.close();

            }

            else
            {
                Toast.makeText(getContext(), "Ошибка, не найдено", Toast.LENGTH_SHORT).show();
            }

            //initPricelistRecycler();
            priceAdapter.swapCursor(getAllItems(priceSpinner.getSelectedItemPosition()), "");


        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка", Toast.LENGTH_SHORT).show();
        }
    }


}