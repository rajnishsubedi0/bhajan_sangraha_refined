package com.rkant.bhajanapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rkant.bhajanapp.FirstActivities.DB_Handler;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.rkant.bhajanapp.Favourites.FavouriteBookmarked;
import com.rkant.bhajanapp.FirstActivities.RecyclerAdapter;
import com.rkant.bhajanapp.secondActivities.DataHolder;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String url_version_code,url_app_link;
    public static int versionCodeOfApp;

    RecyclerView recyclerView;
    RecyclerAdapter recyclerCustomAdapter;
    Boolean backPressed=false;
    ArrayList<DataHolder> arrayList;
    ArrayList<com.rkant.bhajanapp.FirstActivities.DataHolder> nepaliNumbers;
    AdapterView.OnItemSelectedListener listener;

    android.view.View headerNormalLayout, headerSearchLayout;
    android.widget.ImageView btnSearchHeader, btnFavouriteHeader, btnSearchClear;
    android.widget.EditText etSearchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        


        recyclerView=findViewById(R.id.recyclerView);
        arrayList=new ArrayList<>();
        nepaliNumbers=new ArrayList<>();
        try {
            addData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        settingAdapter();


        // Initialize Custom Headers & Search interaction
        headerNormalLayout = findViewById(R.id.header_normal_layout);
        headerSearchLayout = findViewById(R.id.header_search_layout);
        btnSearchHeader = findViewById(R.id.btn_search_header);
        btnFavouriteHeader = findViewById(R.id.btn_favourite_header);
        etSearchInput = findViewById(R.id.et_search_input);
        btnSearchClear = findViewById(R.id.btn_search_clear);

        // Click search to expand with a smooth delayed transition
        btnSearchHeader.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    android.transition.TransitionManager.beginDelayedTransition((android.view.ViewGroup) findViewById(R.id.app_bar_container));
                }
                headerNormalLayout.setVisibility(android.view.View.GONE);
                headerSearchLayout.setVisibility(android.view.View.VISIBLE);
                
                etSearchInput.requestFocus();
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(etSearchInput, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        // Click favorite to open Favourite list
        btnFavouriteHeader.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(MainActivity.this, FavouriteBookmarked.class);
                startActivity(intent);
            }
        });

        // Search clear/close click
        btnSearchClear.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                if (etSearchInput.getText().length() > 0) {
                    etSearchInput.setText("");
                } else {
                    collapseSearch();
                }
            }
        });

        // Type to search instantly
        etSearchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                ArrayList<DataHolder> filteredList = new ArrayList<>();
                for(int a=0; a < arrayList.size(); a++){
                    DataHolder item = arrayList.get(a);
                    if (item.getBhajan_name_nepali().toLowerCase().contains(s.toString().toLowerCase()) || 
                        item.getBhajan_name_english().toLowerCase().contains(s.toString().toLowerCase()) ) {
                        filteredList.add(item);
                    }
                }
                recyclerCustomAdapter.filterList(filteredList);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Safeguard status/action bar
        ThemeHelper.styleActionBar(getSupportActionBar(), getWindow(), this);
    }




    private void settingAdapter() {
        recyclerCustomAdapter=new RecyclerAdapter(arrayList,listener,MainActivity.this,nepaliNumbers);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerCustomAdapter);
    }

    public void addData() throws IOException, JSONException {
        String jsonDataString=readDataFromFile(R.raw.bhajan_list);
        JSONArray jsonArray=new JSONArray(jsonDataString);
        for (int i=0;i<jsonArray.length();i++){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            String nepali_bhajan=jsonObject.getString("bhajan_nepali");
            String bhajan_english_for_search=jsonObject.getString("bhajan_english");
            String id=jsonObject.getString("id");
            arrayList.add( new DataHolder(nepali_bhajan,bhajan_english_for_search,id));
        }
        String jsonData=readDataFromFile(R.raw.nepali_numbers);
        JSONArray array=new JSONArray(jsonData);
        for (int j=0;j<array.length();j++){
            String strr=array.getString(j);
            nepaliNumbers.add(new com.rkant.bhajanapp.FirstActivities.DataHolder(strr));
           // Toast.makeText(this, ""+strr, Toast.LENGTH_SHORT).show();

        }


    }

    public String readDataFromFile(int i) throws IOException {

        InputStream inputStream=null;
        StringBuilder builder=new StringBuilder();
        try{
            String jsonString=null;
            inputStream=getResources().openRawResource(i);
            BufferedReader bufferedReader=new BufferedReader(
                    new InputStreamReader(inputStream,"UTF-8"));
            while ((jsonString=bufferedReader.readLine()) !=null){
                builder.append(jsonString);
            }
        }
        finally {
            if(inputStream != null){
                inputStream.close();
            }
        }
        return new String(builder);
    }

    private void collapseSearch() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            android.transition.TransitionManager.beginDelayedTransition((android.view.ViewGroup) findViewById(R.id.app_bar_container));
        }
        etSearchInput.setText("");
        etSearchInput.clearFocus();
        headerSearchLayout.setVisibility(android.view.View.GONE);
        headerNormalLayout.setVisibility(android.view.View.VISIBLE);

        // Hide keyboard smoothly
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etSearchInput.getWindowToken(), 0);
        }
        
        // Reset list filter to original full list
        recyclerCustomAdapter.filterList(arrayList);
    }

    @Override
    public void onBackPressed() {
        if (headerSearchLayout != null && headerSearchLayout.getVisibility() == android.view.View.VISIBLE) {
            collapseSearch();
            return;
        }

        if (backPressed) {
            super.onBackPressed();
        } else {
            backPressed = true;
            CustomToast.showInfo(this, "Press back again to exit");
        }
        new Handler(android.os.Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                backPressed = false;
            }
        }, 2500);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}




 