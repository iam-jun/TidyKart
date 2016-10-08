package fpoly.pro205.fit.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fpoly.pro205.fit.activities.PostGetRequest;
import fpoly.pro205.fit.activities.R;
import fpoly.pro205.fit.adapter.ScheduleAdapter;
import fpoly.pro205.fit.items.itemScheduled;
import fpoly.pro205.fit.utils.Utils;

import static android.content.Context.MODE_PRIVATE;

public class ScheduledFragment extends Fragment {

    ListView lv;
    TextView txt;
    ProgressDialog progressDialog;
    SharedPreferences pre;
    SharedPreferences.Editor edit;
    ArrayList<itemScheduled> list;

    public ScheduledFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_scheduled, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");

        pre = getActivity().getSharedPreferences("app_dta", MODE_PRIVATE);
        edit = pre.edit();

        list = new ArrayList<>();

        lv = (ListView) rootView.findViewById(R.id.lvScheduled);
        txt = (TextView) rootView.findViewById(R.id.txtEmptyScheduled);
        getData();

        return rootView;
    }
    private void getData() {
        String userID = String.valueOf(pre.getInt("userID", 1));
        progressDialog.show();
        PostGetRequest request = new PostGetRequest(Utils.GET_REQUEST, "http://dev.api.tidykart.com:8080/tidykart-ws/home/" + userID) {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
                Log.d("Home", s);
                try {
                    JSONObject obj = new JSONObject(s);
                    JSONArray tmpbls = obj.getJSONArray("userSchedulePickupInfo");
                    for(int i=0; i<tmpbls.length();i++){
                        itemScheduled m = new itemScheduled();
                        m.setSlot(tmpbls.getJSONObject(i).getInt("pickupSlotId"));
                        m.setSun(tmpbls.getJSONObject(i).getBoolean("sunday"));
                        m.setMon(tmpbls.getJSONObject(i).getBoolean("monday"));
                        m.setTue(tmpbls.getJSONObject(i).getBoolean("tuesday"));
                        m.setWed(tmpbls.getJSONObject(i).getBoolean("wednesday"));
                        m.setThu(tmpbls.getJSONObject(i).getBoolean("thursday"));
                        m.setFri(tmpbls.getJSONObject(i).getBoolean("friday"));
                        m.setSat(tmpbls.getJSONObject(i).getBoolean("saturday"));
                        list.add(m);
                    }
                    setupBookTab(list);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        request.execute();
    }

    public void setupBookTab(ArrayList<itemScheduled> list){
        for(int i=0;i<list.size();i++)Log.d("test", "content "+list.get(i).getSlot()+" sun: "+list.get(i).isSun());
        if(list.isEmpty()){
            lv.setVisibility(View.GONE);
            txt.setVisibility(View.VISIBLE);
        }else{
            lv.setVisibility(View.VISIBLE);
            ScheduleAdapter adapter = new ScheduleAdapter(getActivity(), list);
            lv.setAdapter(adapter);
        }
    }
}
