package com.lossurdo.infinitescroll;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lossurdo on 28/06/16.
 */
public class CustomAdapter extends ArrayAdapter<JsonObject> {

    Bitmap bitmap;

    public CustomAdapter(Context context, List<JsonObject> listJsonObject) {
        super(context, R.layout.custom_row, listJsonObject);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inf = LayoutInflater.from(getContext());
        View customView = inf.inflate(R.layout.custom_row, parent, false);

        JsonObject obj = getItem(position);
        TextView tvNome = ( TextView ) customView.findViewById( R.id.tvNome );
        TextView tvEmail = ( TextView ) customView.findViewById( R.id.tvEmail );
        TextView tvID = ( TextView ) customView.findViewById( R.id.tvID );

        tvID.setText(String.valueOf(position + 1));
        tvNome.setText(obj.get("name").getAsJsonObject().get("first").getAsString().toUpperCase()
                + " " + obj.get("name").getAsJsonObject().get("last").getAsString().toUpperCase());
        tvEmail.setText(obj.get("email").getAsString());

        return customView;
    }

    /**
     * COLETANDO IMAGENS COM BASE NO WEB SERVICE
     * NÃO ESTÁ SENDO UTILIZADO NO MOMENTO...
     */
    class LoadImage extends AsyncTask<String, Void, Bitmap> {
        private ImageView iv;

        public LoadImage(ImageView iv) {
            super();
            this.iv = iv;
        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
                Log.d("doInBackground", "Imagem lida com sucesso: " + args[0]);
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage());
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            if(image != null){
                iv.setImageBitmap(image);
            }
        }
    }

}
