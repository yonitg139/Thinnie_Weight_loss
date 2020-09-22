package com.example.thinnie_weight_loss;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.MyViewHolder> {

    private Context context;
    private JSONArray jsonArray;

    WeightAdapter(Context context, JSONArray jsonArray){
        this.context = context;
        this.jsonArray = jsonArray;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.weight_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            JSONArray row = jsonArray.getJSONArray(position);
            holder.weight_ts_txt.setText(row.getString(2));

            int imgResource;
            switch(row.getString(1)){
                case "red":
                    imgResource = R.drawable.red;
                    break;
                case "yellow":
                    imgResource = R.drawable.yellow;
                    break;
//                case "null":
                default:
                    imgResource = R.drawable.green;
            }

            holder.range_img.setImageResource(imgResource);
            holder.weight_txt.setText(row.getString(0));
        } catch (JSONException e) {
            Toast.makeText(context, "Failed to parse row " + position, Toast.LENGTH_SHORT). show();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView weight_txt, weight_ts_txt;
        ImageView range_img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            weight_txt = itemView.findViewById(R.id.weight);
            range_img = itemView.findViewById(R.id.range);
            weight_ts_txt = itemView.findViewById(R.id.date);
        }
    }
}
