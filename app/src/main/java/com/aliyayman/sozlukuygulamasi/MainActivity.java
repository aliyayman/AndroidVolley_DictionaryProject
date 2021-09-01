package com.aliyayman.sozlukuygulamasi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private Toolbar toolbar;
    private RecyclerView rv;
    private ArrayList<Kelimeler> kelimelerList;
    private KelimelerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=findViewById(R.id.toolbar);
        rv=findViewById(R.id.rv);

        toolbar.setTitle("Sözlük Uygulaması");
        setSupportActionBar(toolbar);



        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        tumKelimeler();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        MenuItem item=menu.findItem(R.id.action_ara);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.e("Gönderilen arama",query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.e("harf girdikçe",newText);
        kelimeAra(newText);
        return false;
    }

    public void tumKelimeler(){
        String url="http://www.byrmkus.tk/kelimeler/tum_kelimeler.php";
        StringRequest istek=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                kelimelerList=new ArrayList<>();
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray kelimeler=jsonObject.getJSONArray("kelimeler");

                    for(int i=0;i<kelimeler.length();i++){
                        JSONObject k=kelimeler.getJSONObject(i);

                        int kelime_id=k.getInt("kelime_id");
                        String ingilizce=k.getString("ingilizce");
                        String turkce=k.getString("turkce");

                        Kelimeler kelime=new Kelimeler(kelime_id,ingilizce,turkce);
                        kelimelerList.add(kelime);

                    }

                    adapter=new KelimelerAdapter(MainActivity.this,kelimelerList);
                    rv.setAdapter(adapter);



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(istek);
    }
    public void kelimeAra(final String aramaKelime){

        String url="http://www.byrmkus.tk/kelimeler/kelime_ara.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("cevap",response);
                kelimelerList=new ArrayList<>();
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray kelimeler=jsonObject.getJSONArray("kelimeler");
                    for(int i=0;i<kelimeler.length();i++){
                        JSONObject k=kelimeler.getJSONObject(i);
                        int kelime_id=k.getInt("kelime_id");
                        String ingilizce=k.getString("ingilizce");
                        String turkce=k.getString("turkce");

                        Log.e("****","****");
                        Log.e("ingilizce",ingilizce);
                        Log.e("türkce",turkce);
                        Log.e("****","****");

                        Kelimeler kelime=new Kelimeler(kelime_id,ingilizce,turkce);
                        kelimelerList.add(kelime);

                    }
                    adapter=new KelimelerAdapter(MainActivity.this,kelimelerList);
                    rv.setAdapter(adapter);



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params=new HashMap<>();
                params.put("ingilizce",aramaKelime);

                return params;
            }
        };

        Volley.newRequestQueue(MainActivity.this).add(stringRequest);

    }

}