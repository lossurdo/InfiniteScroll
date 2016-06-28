package com.lossurdo.infinitescroll;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    static final int USERS_PER_PAGE = 5;
    static final String URL = "https://randomuser.me/api/?results=" + USERS_PER_PAGE;

    CustomAdapter adapter;
    List<JsonObject> allObjects = new ArrayList<>();

    @Bind(R.id.listView)
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        new Async().execute();
        adapter = new CustomAdapter(this, allObjects);
        listView.setAdapter(adapter);

        listView.setOnScrollListener(new InfiniteScrollListener(USERS_PER_PAGE) {
            @Override
            public void loadMore(int page, int totalItemsCount) {
                new Async().execute();
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Page #" + page, Toast.LENGTH_SHORT).show();
                Log.d("loadMore", "#" + allObjects.size());
            }
        });

    }

    /**
     * CLASSE PARA CARREGAR DADOS DO WEB SERVICE
     */
    class Async extends AsyncTask<Void, Void, JsonArray> {

        @Override
        protected JsonArray doInBackground(Void... params) {
            Log.d("doInBackground", "Chamando web service...");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(URL)
                    .get()
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage());
            }

            try {
                JsonElement elm = new JsonParser().parse(response.body().string());
                return elm.getAsJsonObject().get("results").getAsJsonArray();
            } catch (IOException e) {
                Log.e("ERROR", e.toString());
            }

            return new JsonArray();
        }

        @Override
        protected void onPostExecute(JsonArray jsonElements) {
            Log.d("onPostExecute", jsonElements.toString());
            for (int i = 0; i < jsonElements.size(); i++) {
                allObjects.add(jsonElements.get(i).getAsJsonObject());
            }
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * CLASSE PARA CONTROLE DO SCROLL INFINITO
     * Fonte: http://www.avocarrot.com/blog/implement-infinitely-scrolling-list-android/
     */
    abstract class InfiniteScrollListener implements AbsListView.OnScrollListener {
        private int bufferItemCount = 10;
        private int currentPage = 0;
        private int itemCount = 0;
        private boolean isLoading = true;

        public InfiniteScrollListener(int bufferItemCount) {
            this.bufferItemCount = bufferItemCount;
        }

        public abstract void loadMore(int page, int totalItemsCount);

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // Do Nothing
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (totalItemCount < itemCount) {
                this.itemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.isLoading = true;
                }
            }

            if (isLoading && (totalItemCount > itemCount)) {
                isLoading = false;
                itemCount = totalItemCount;
                currentPage++;
            }

            if (!isLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + bufferItemCount)) {
                loadMore(currentPage + 1, totalItemCount);
                isLoading = true;
            }
        }
    }

}
