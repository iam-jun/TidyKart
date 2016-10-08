package fpoly.pro205.fit.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import fpoly.pro205.fit.adapter.BooksAdapter;
import fpoly.pro205.fit.items.itemBook;
import fpoly.pro205.fit.utils.Utils;

import static android.content.Context.MODE_PRIVATE;


public class BooksFragment extends Fragment {

    ListView lv;
    TextView txtEmpty;
    ArrayList<itemBook> listBook;
    ProgressDialog progressDialog;
    SharedPreferences pre;
    SharedPreferences.Editor edit;

    public BooksFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");

        pre = getActivity().getSharedPreferences("app_dta", MODE_PRIVATE);
        edit = pre.edit();

        View rootView = inflater.inflate(R.layout.fragment_books, container, false);
        lv = (ListView)rootView.findViewById(R.id.lvBooks);
        txtEmpty = (TextView) rootView.findViewById(R.id.txtEmptyBooking);
        listBook = new ArrayList<>();
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
                    JSONArray tmpbls = obj.getJSONArray("bookingInfo");
                    for(int i=0; i<tmpbls.length();i++){
                        itemBook m = new itemBook();
                        m.setDate(tmpbls.getJSONObject(i).getString("bookingDate"));
                        m.setSlot(tmpbls.getJSONObject(i).getInt("pickupSlotId"));
                        m.setStatus(tmpbls.getJSONObject(i).getInt("bookingStatusId"));
                        listBook.add(m);
                        Log.d("Test", m.getDate()+"slot "+m.getSlot());
                    }
                    setupBookTab();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        request.execute();
    }

    public void setupBookTab(){
        if(listBook.isEmpty()){
            lv.setVisibility(View.GONE);
            txtEmpty.setVisibility(View.VISIBLE);
        }else{
            lv.setVisibility(View.VISIBLE);
            BooksAdapter adapter = new BooksAdapter(getActivity(), listBook);
            lv.setAdapter(adapter);
        }
    }
}
