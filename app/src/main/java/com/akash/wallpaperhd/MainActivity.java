package com.akash.wallpaperhd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.wallpaperhd.adapters.SuggestedAdapter;
import com.akash.wallpaperhd.adapters.WallpaperAdapter;
import com.akash.wallpaperhd.interfaces.RecyclerViewClickListener;
import com.akash.wallpaperhd.models.SuggestedModel;
import com.akash.wallpaperhd.models.WallpaperModel;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RecyclerViewClickListener {
    static final float END_SCALE = 0.7f;
    ImageView menuIcon;
    LinearLayout contentView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    RecyclerView recyclerView,topMostRecyclerView;
    RecyclerView.Adapter adapter;
    WallpaperAdapter wallpaperAdapter;
    List<WallpaperModel> wallpaperModelList;
    ArrayList<SuggestedModel> suggestedModels = new ArrayList<>();
    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    ProgressBar progressBar;
    TextView replaceTitle;
    EditText searchEt;
    ImageView searchIv;
    int pageNumber = 1;
    String url = "https://api.pexels.com/v1/curated?page="+ pageNumber + "&per_page=80";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menuIcon =findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationDrawer();
        View headerView = navigationView.getHeaderView(0);
        ImageView appLogo = headerView.findViewById(R.id.app_name);

        recyclerView = findViewById(R.id.recyclerView);
        topMostRecyclerView=findViewById(R.id.suggestedRecyclerView);

        wallpaperModelList=new ArrayList<>();
        wallpaperAdapter = new WallpaperAdapter(this, wallpaperModelList);
        recyclerView.setAdapter(wallpaperAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = gridLayoutManager.getChildCount();
                totalItems = gridLayoutManager.getItemCount();
                scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    fetchWallpaper();
                }

            }
        });

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        replaceTitle = findViewById(R.id.topMostTitle);
        fetchWallpaper();
        suggestedItems();

        //search edittext and imageview
        searchEt = findViewById(R.id.searchEV);
        searchIv = findViewById(R.id.search_image);
        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Try again later !!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void navigationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        //animation in the drawer
        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                final float diffScaledOffset = slideOffset * (1-END_SCALE);
                final float offsetScale = 1-diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerVisible(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_home:
                Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_trending:
                Toast.makeText(this, "trending clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_most_viewed:
                Toast.makeText(this, "most view clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_about:
                Toast.makeText(this, "about clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "logout clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private void suggestedItems() {
        topMostRecyclerView.setHasFixedSize(true);
        topMostRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        suggestedModels.add(new SuggestedModel(R.drawable.trending, "Trending"));
        suggestedModels.add(new SuggestedModel(R.drawable.nature, "Nature"));
        suggestedModels.add(new SuggestedModel(R.drawable.travel, "Travel"));
        suggestedModels.add(new SuggestedModel(R.drawable.architecture, "Architecture"));
        suggestedModels.add(new SuggestedModel(R.drawable.people, "People"));
        suggestedModels.add(new SuggestedModel(R.drawable.music, "Music"));


        adapter = new SuggestedAdapter(suggestedModels, MainActivity.this);
        topMostRecyclerView.setAdapter(adapter);
    }

    private void fetchWallpaper() {
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("photos");

                            int length = jsonArray.length();

                            for (int i=0;i<length; i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                int id = object.getInt("id");
                                String photographerName = object.getString("photographer");
                                JSONObject objectImage = object.getJSONObject("src");
                                String originalUrl = objectImage.getString("original");
                                String mediumUrl = objectImage.getString("medium");

                                WallpaperModel wallpaperModel = new WallpaperModel(id, originalUrl, mediumUrl, photographerName);
                                wallpaperModelList.add(wallpaperModel);

                            }
                            wallpaperAdapter.notifyDataSetChanged();
                            pageNumber++;


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization","563492ad6f9170000100000139e34d040a2c417eb14ae0c1abf170dd");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onItemClick(int position) {
        progressBar.setVisibility(View.VISIBLE);
        if(position ==0){
            replaceTitle.setText("Trending");
            url="https://api.pexels.com/v1/search/?page=" + pageNumber + "&per_page=80&query=trending";
            wallpaperModelList.clear();
            fetchWallpaper();
            progressBar.setVisibility(View.GONE);
        }else if(position ==1){
            replaceTitle.setText("Nature");
            url="https://api.pexels.com/v1/search/?page=" + pageNumber + "&per_page=80&query=nature";
            wallpaperModelList.clear();
            fetchWallpaper();
            progressBar.setVisibility(View.GONE);
        }else if(position ==2) {
            replaceTitle.setText("Travel");
            url = "https://api.pexels.com/v1/search/?page=" + pageNumber + "&per_page=80&query=travel";
            wallpaperModelList.clear();
            fetchWallpaper();
            progressBar.setVisibility(View.GONE);
        }else if(position ==3) {
            replaceTitle.setText("Architecture");
            url = "https://api.pexels.com/v1/search/?page=" + pageNumber + "&per_page=80&query=architecture";
            wallpaperModelList.clear();
            fetchWallpaper();
            progressBar.setVisibility(View.GONE);
        }else if(position ==4) {
            replaceTitle.setText("People");
            url = "https://api.pexels.com/v1/search/?page=" + pageNumber + "&per_page=80&query=people";
            wallpaperModelList.clear();
            fetchWallpaper();
            progressBar.setVisibility(View.GONE);
        }else if(position ==5) {
            replaceTitle.setText("Music");
            url = "https://api.pexels.com/v1/search/?page=" + pageNumber + "&per_page=80&query=music";
            wallpaperModelList.clear();
            fetchWallpaper();
            progressBar.setVisibility(View.GONE);
        }
    }
}