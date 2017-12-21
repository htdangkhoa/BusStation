package vn.busmap.busstation.Adapters.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import vn.busmap.busstation.Models.BusModel;
import vn.busmap.busstation.R;

/**
 * Created by dangkhoa on 12/8/17.
 */

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.ViewHolder> {
    Context context;
    ArrayList<BusModel> busModels;
    Random r = new Random();

    public BusAdapter(ArrayList<BusModel> busModels) {
        this.busModels = busModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.model_bus, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String vehicleNumber = busModels.get(position).getVehicleNumber();
        holder.textViews.get(0).setText(vehicleNumber.substring(0, 3) + "\n" + vehicleNumber.substring(3, vehicleNumber.length()));

        int intDistance = busModels.get(position).getDistance().intValue();
        holder.textViews.get(1).setText(String.valueOf(busModels.get(position).getSpeed()) + "km/h");
        if (intDistance >= 1000) {
            holder.textViews.get(2).setText("No " + busModels.get(position).getRouteNo() + " - " + String.format("%4.1f", busModels.get(position).getDistance()/1000) + "km");
        } else {
            holder.textViews.get(2).setText("No " + busModels.get(position).getRouteNo() + " - " + String.format("%3.1f", busModels.get(position).getDistance()) + "m");
        }

        String strTime = busModels.get(position).getTime();
//        int time = Integer.parseInt(strTime.substring(strTime.lastIndexOf(" "), strTime.length()));
//        holder.textViews.get(3).setText(strTime + ((time > 2) ? " minutes" : " minute"));
        holder.textViews.get(3).setText(strTime + " min");
        holder.textViews.get(4).setText(busModels.get(position).getRouteName());

        int direction = busModels.get(position).getDirection();
        holder.textViews.get(5).setText(((direction == 0) ? "(forward)" : "(backward)"));
    }

    @Override
    public int getItemCount() {
        return busModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindViews({R.id.txtVehicleNumber, R.id.txtSpeed, R.id.txtRouteNoAndDisstance, R.id.txtTime, R.id.txtRouteName, R.id.txtDirection})
        List<TextView> textViews;
        @BindView(R.id.icon) RelativeLayout icon;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
