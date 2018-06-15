package com.demo.tavish.hemantapp;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.demo.tavish.hemantapp.DB.DBHelper;

import java.util.ArrayList;

public class ListDataActivity extends AppCompatActivity {

    private static final String TAG = "ListDataActivity";
    DBHelper dbHelper;

   /* RecyclerView rv_list;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;*/
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);
        listView = (ListView) findViewById(R.id.list_data);
        dbHelper = new DBHelper(this);
       /* rv_list = (RecyclerView) findViewById(R.id.rv_list_data);
        dbHelper = new DBHelper(this);

        rv_list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rv_list.setLayoutManager(layoutManager);*/
        populateRecyclerViewList();

    }

    private void populateRecyclerViewList(){

        Cursor data = dbHelper.getAllData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            listData.add(data.getString(1));
        }
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,listData);
        listView.setAdapter(adapter);
       /* while (data.moveToNext()){
            listData.add(data.getString(1));
        }
        adapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        }*/
    }
}
