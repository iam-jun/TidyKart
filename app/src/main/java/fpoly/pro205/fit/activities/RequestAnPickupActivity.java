package fpoly.pro205.fit.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


import java.sql.Timestamp;
import java.text.ParseException;
import java.util.GregorianCalendar;

import fpoly.pro205.fit.activities.AppActivity;
import fpoly.pro205.fit.activities.PostGetRequest;
import fpoly.pro205.fit.activities.R;
import fpoly.pro205.fit.utils.Utils;

import static com.google.android.gms.analytics.internal.zzy.c;
import static com.google.android.gms.analytics.internal.zzy.j;
import static com.google.android.gms.analytics.internal.zzy.p;

public class RequestAnPickupActivity extends AppCompatActivity {

    Spinner spn;
    EditText txtCoupon;
    Button btnRequest;
    SharedPreferences pre;
    SharedPreferences.Editor edit;
    ProgressDialog progressDialog;
    Timestamp date;
    int slotPickup=1;

    String arr[] = {"Slot - 1 , 5:00 PM-7:00 PM", "Slot - 2 , 7:00 PM - 9:00 PM", "Slot - 3 , 10 AM - 1 PM"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_an_pickup);
        java.util.Date day= new java.util.Date();
        date = new Timestamp(day.getTime());

        progressDialog = new ProgressDialog(RequestAnPickupActivity.this);
        progressDialog.setMessage("Loading...");

        pre = getSharedPreferences("app_dta", MODE_PRIVATE);
        edit = pre.edit();
        spn = (Spinner) findViewById(R.id.spnSlotPickup);
        txtCoupon = (EditText) findViewById(R.id.txtCouponCode);
        btnRequest = (Button) findViewById(R.id.btnRequestPickup);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    requestPickup();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        setupSpinner();
    }

    private void setupSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RequestAnPickupActivity.this, android.R.layout.simple_spinner_dropdown_item, arr);
        spn.setAdapter(adapter);
        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                slotPickup = position+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void requestPickup() throws JSONException {
        Log.d("app", "Pickup");
        JSONObject object = new JSONObject();
        object.put("userId", pre.getInt("userID", 0));
        object.put("addressId", pre.getInt("addressID", 0));
        object.put("couponCode", txtCoupon.getText().toString());
        object.put("pickupSlotId", slotPickup);
        object.put("bookingDate", date);
        String body = object.toString();
        PostGetRequest request = new PostGetRequest(Utils.POST_REQUEST, "http://dev.api.tidykart.com:8080/tidykart-ws/pickuprequest", body) {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("Response", s);
                try {
                    JSONObject obj = new JSONObject(s);
                    Toast.makeText(RequestAnPickupActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(RequestAnPickupActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    Log.e("Error", e.toString());
                }
                Intent intent = new Intent(RequestAnPickupActivity.this, AppActivity.class);
                startActivity(intent);
            }
        };
        request.execute();
    }

    public void onRadioButtonClicked(View view) throws ParseException {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioToday: {
                if (checked) {
                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.add(calendar.DAY_OF_MONTH, 1);
                    //date = new Timestamp(calendar.getTime());
                }
            }
                break;
            case R.id.radioTomorrow: {
                if (checked) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        Calendar c = Calendar.getInstance();
                        c.add(Calendar.DAY_OF_YEAR, 1);
                        java.util.Date str_date = c.getTime();
                        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(str_date.getTime());
                        date = currentTimestamp;
                        Log.d("date", date.toString());
                    }
                }
            }
                break;

            case R.id.radioDayAfter: {
                if (checked) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        Calendar c = Calendar.getInstance();
                        c.add(Calendar.DAY_OF_YEAR, 2);
                        java.util.Date str_date = c.getTime();
                        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(str_date.getTime());
                        date = currentTimestamp;
                        Log.d("date", date.toString());
                    }
                }
            }
                break;
        }
    }
}
