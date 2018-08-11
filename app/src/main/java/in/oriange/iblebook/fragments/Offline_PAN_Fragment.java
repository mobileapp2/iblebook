package in.oriange.iblebook.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.oriange.iblebook.R;
import in.oriange.iblebook.activities.Add_PAN_Activity;
import in.oriange.iblebook.adapters.GetPANListAdapter;
import in.oriange.iblebook.models.GetTaxListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.DataBaseHelper;
import in.oriange.iblebook.utilities.UserSessionManager;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
public class Offline_PAN_Fragment extends Fragment{
    private static Context context;
    private FloatingActionButton fab_add_pan;
    private static RecyclerView rv_panlist;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserSessionManager session;
    public static FlowingDrawer ll_parent;
    private static String user_id;
    private static DataBaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_offline_pan, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
        getSessionData();
        setEventHandlers();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        dbHelper = new DataBaseHelper(context);
        ll_parent = getActivity().findViewById(R.id.drawerlayout);
        fab_add_pan = rootView.findViewById(R.id.fab_add_pan);
        rv_panlist = rootView.findViewById(R.id.rv_panlist);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        layoutManager = new LinearLayoutManager(context);
        rv_panlist.setLayoutManager(layoutManager);
    }

    private void getSessionData() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            user_id = json.getString("user_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setDefault() {
        ArrayList<GetTaxListPojo> panList = new ArrayList<GetTaxListPojo>();
        panList = dbHelper.getPANListFromDb();
        rv_panlist.setAdapter(new GetPANListAdapter(context, panList,"OFFLINE"));

    }

    private void setEventHandlers() {
        fab_add_pan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Add_PAN_Activity.class);
                intent.putExtra("STATUS", "OFFLINE");
                startActivity(intent);
            }
        });
    }

}
