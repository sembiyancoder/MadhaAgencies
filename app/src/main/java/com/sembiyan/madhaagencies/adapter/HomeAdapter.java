package com.sembiyan.madhaagencies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sembiyan.madhaagencies.R;

import org.json.JSONArray;
import org.json.JSONObject;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    onItemSelectedListener onItemSelectedListener;
    private Context context;
    private JSONArray jsonArray;

    public HomeAdapter(Context context, JSONArray jsonArray, onItemSelectedListener onItemSelectedListener) {
        this.context = context;
        this.jsonArray = jsonArray;
        this.onItemSelectedListener = onItemSelectedListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dashboard_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        JSONObject object = jsonArray.optJSONObject(position);
        holder.mMenuTextView.setText(object.optString("name"));
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }


    public interface onItemSelectedListener {
        void onItemSelected(JSONObject jsonObject);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mMenuTextView;

        public MyViewHolder(View itemView) {
            super(itemView);

            mMenuTextView = itemView.findViewById(R.id.txt_menu_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelectedListener.onItemSelected(jsonArray.optJSONObject(getAdapterPosition()));
                }
            });
        }
    }


}