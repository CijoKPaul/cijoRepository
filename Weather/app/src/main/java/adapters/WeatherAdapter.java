package adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.weather.weatherforecast.R;

import java.util.ArrayList;
import java.util.HashMap;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<HashMap<String,String>> list = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtTemp,txtWind,txtDesc;

        public MyViewHolder(View view) {
            super(view);

            txtTemp = view.findViewById(R.id.textView10);
            txtWind = view.findViewById(R.id.textView11);
            txtDesc = view.findViewById(R.id.textView12);
        }
    }


    public WeatherAdapter(Context mContext, ArrayList<HashMap<String,String>> list) {
        this.mContext = mContext;
        this.list = list;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weather, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.txtTemp.setText(list.get(position).get("temp")+" Kelvin");
        holder.txtDesc.setText(list.get(position).get("desc"));
        holder.txtWind.setText(list.get(position).get("wind"));

    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
