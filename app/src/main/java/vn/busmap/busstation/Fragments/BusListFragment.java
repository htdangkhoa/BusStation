package vn.busmap.busstation.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.widget.PullRefreshLayout;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import vn.busmap.busstation.Adapters.RecyclerView.BusAdapter;
import vn.busmap.busstation.MainActivity;
import vn.busmap.busstation.Models.BusModel;
import vn.busmap.busstation.R;
import vn.busmap.busstation.SuperClasses.DividerItemDecoration;
import vn.busmap.busstation.Utils.AsyncHttp;
import vn.busmap.busstation.Utils.Services;

/**
 * Created by dangkhoa on 12/8/17.
 */

public class BusListFragment extends Fragment {
    @BindView(R.id.pullRefreshLayout) PullRefreshLayout pullRefreshLayout;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    ArrayList<BusModel> busModels = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bus_list, container, false);
        
        initialize(view);
        
        return view;
    }

    private void initialize(View view) {
        MainActivity.actionBar.setVisibility(View.VISIBLE);
        MainActivity.actionTitle.setText("Detail");

        ButterKnife.bind(this, view);

//        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (busModels.size() > 0) busModels.clear();

                Bundle bundle = getArguments();
                if (bundle != null) {
                    if (bundle.getString("STATION_ID") != null) {
                        final String stationId = bundle.getString("STATION_ID");

                        requestToServer(stationId);
                    }
                }
            }
        });

        setupRecyclerView(this.recyclerView);
    }

    private void setupRecyclerView(final RecyclerView recyclerView) {
        if (busModels.size() > 0) busModels.clear();

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(decoration);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getString("STATION_ID") != null) {
                final String stationId = bundle.getString("STATION_ID");

                requestToServer(stationId);
            }
        }
    }

    private void requestToServer(final String stationId) {
        final ProgressDialog dialog = ProgressDialog.show(getContext(), "", "Loading. Please wait...", true);
        dialog.show();

        AsyncHttp.GET("/goto/" + stationId + "/normalized", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (response.length() == 0) {
                    Services.ShowDialog(getContext(), "Error", "Sorry, we cannot get data with route " + stationId + ". Please, try later.", null, null, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getFragmentManager().popBackStack();
                        }
                    });
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            Log.i("LIST_DATA", response.getJSONObject(i).toString());
                            JSONObject objectBus = response.getJSONObject(i).getJSONObject("bus");
                            JSONObject objectRoute = response.getJSONObject(i).getJSONObject("route");

                            busModels.add(new BusModel(objectBus.getString("VehicleNumber"),
                                    objectBus.getString("Time"),
                                    objectBus.getDouble("Lat"),
                                    objectBus.getDouble("Lng"),
                                    objectBus.getDouble("Deg"),
                                    objectBus.getDouble("Speed"),
                                    objectBus.getDouble("Distance"),

                                    objectRoute.getInt("RouteId"),
                                    objectRoute.getInt("Direction"),
                                    objectRoute.getString("RouteNo"),
                                    objectRoute.getString("RouteName")
                            ));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    BusAdapter adapter = new BusAdapter(busModels);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }

                dialog.dismiss();
                pullRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                dialog.dismiss();
                pullRefreshLayout.setRefreshing(false);
            }
        });
    }
}
