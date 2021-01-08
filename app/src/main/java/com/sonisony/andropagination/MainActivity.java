package com.sonisony.andropagination;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private NestedScrollView nestedScrollView;
    private MainAdp mainAdp;
    int page =1,limit =10;

    ArrayList<MainData> dataArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recy_view);
        nestedScrollView = findViewById(R.id.nest_scroll);

   mainAdp =new MainAdp(dataArrayList, MainActivity.this);
   recyclerView.setLayoutManager(new LinearLayoutManager(this));
   recyclerView.setAdapter(mainAdp);
getData(page,limit);
nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
            page++;
            progressBar.setVisibility(View.VISIBLE);
            getData(page,limit);
        }
    }
});
    }

    private void getData(int page, int limit) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://picsum.photos/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MainInterface mainInterface =retrofit.create(MainInterface.class);
        Call<String> call = mainInterface.STRING_CALL(page,limit);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body()!=null){
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONArray jsonArray = new JSONArray(response.body());
                        parseResult(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void parseResult(JSONArray jsonArray) {
                for (int i=0; i<jsonArray.length();i++){
                    try {
                        JSONObject object = jsonArray.getJSONObject(i);
                        MainData data = new MainData();
                        data.setImage(object.getString("download_url"));
                        data.setName(object.getString("author"));
                        dataArrayList.add(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mainAdp =new MainAdp(dataArrayList, MainActivity.this);
                    recyclerView.setAdapter(mainAdp);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}