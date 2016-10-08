package fpoly.pro205.fit.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import fpoly.pro205.fit.activities.AppActivity;
import fpoly.pro205.fit.activities.PostGetRequest;
import fpoly.pro205.fit.activities.R;
import fpoly.pro205.fit.utils.Utils;

import static com.google.android.gms.analytics.internal.zzy.b;
import static com.google.android.gms.analytics.internal.zzy.o;
import static com.google.android.gms.analytics.internal.zzy.s;
import static com.google.android.gms.analytics.internal.zzy.z;

public class CreatescheduledActivity extends AppCompatActivity {

    Spinner spn;
    Button btnCreate;
    Switch sun, mon, tue, wed, thu, fri, sat;

    SharedPreferences pre;
    SharedPreferences.Editor edit;
    ProgressDialog progressDialog;

    String arr[] = {"Slot - 1 , 5:00 PM-7:00 PM", "Slot - 2 , 7:00 PM - 9:00 PM", "Slot - 3 , 10 AM - 1 PM"};
    int slot =1;
    int serviceTypeId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createscheduled);

        spn = (Spinner) findViewById(R.id.spinSlotScheduled);
        btnCreate = (Button) findViewById(R.id.btnCreateScheduled);
        sun = (Switch) findViewById(R.id.swSun);
        mon = (Switch) findViewById(R.id.swMon);
        tue = (Switch) findViewById(R.id.swTue);
        wed = (Switch) findViewById(R.id.swWed);
        thu = (Switch) findViewById(R.id.swThu);
        fri = (Switch) findViewById(R.id.swFri);
        sat = (Switch) findViewById(R.id.swSat);

        setupSpinner();

        progressDialog = new ProgressDialog(CreatescheduledActivity.this);
        progressDialog.setMessage("Loading...");

        pre = getSharedPreferences("app_dta", MODE_PRIVATE);
        edit = pre.edit();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createScheduled();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createScheduled() throws JSONException {
        Log.d("app", "Pickup");
        JSONObject object = new JSONObject();
        object.put("userId", pre.getInt("userID", 0));
        object.put("addressId", pre.getInt("addressID", 0));
        object.put("pickupSlotId", slot);
        object.put("serviceTypeId", serviceTypeId);
        if(sun.isChecked()) object.put("sunday", 1);
        if(mon.isChecked()) object.put("monday", 1);
        if(tue.isChecked()) object.put("tuesday", 1);
        if(wed.isChecked()) object.put("wednesday", 1);
        if(thu.isChecked()) object.put("thursday", 1);
        if(fri.isChecked()) object.put("friday", 1);
        if(sat.isChecked()) object.put("saturday", 1);

        String body = object.toString();
        PostGetRequest request = new PostGetRequest(Utils.POST_REQUEST, "http://dev.api.tidykart.com:8080/tidykart-ws/schedulepickup", body) {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("Response", s);
                try {
                    JSONObject obj = new JSONObject(s);
                    Toast.makeText(CreatescheduledActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(CreatescheduledActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    Log.e("Error", e.toString());
                }
                Intent intent = new Intent(CreatescheduledActivity.this, AppActivity.class);
                startActivity(intent);
            }
        };
        request.execute();
    }



    private void setupSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreatescheduledActivity.this, android.R.layout.simple_spinner_dropdown_item, arr);
        spn.setAdapter(adapter);
        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                slot = position+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void onRadioButtonClicked(View view) throws ParseException {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioIron: {
                if (checked) {
                    serviceTypeId = 1;
                }
            }
            break;
            case R.id.radioLaundry: {
                if (checked) {
                    serviceTypeId = 2;
                }
            }
            break;
        }
    }

}
