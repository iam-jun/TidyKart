package fpoly.pro205.fit.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import fpoly.pro205.fit.activities.AppActivity;
import fpoly.pro205.fit.activities.PostGetRequest;
import fpoly.pro205.fit.activities.R;
import fpoly.pro205.fit.activities.RequestOTP;
import fpoly.pro205.fit.utils.Utils;

import static com.google.android.gms.analytics.internal.zzy.e;

public class SettingsActivity extends AppCompatActivity {

    private final StringBuffer mPlaceTypeDisplayBuffer = new StringBuffer();

    String TAG = "Maps";


    /**
     * Request code passed to the PlacePicker intent to identify its result when it returns.
     */
    private static final int REQUEST_PLACE_PICKER = 1;

    SharedPreferences pre;
    SharedPreferences.Editor edit;
    TextView txtNotify;
    EditText txtAdd1, txtAdd2, txtPinCode;
    Button btnSave;
    String latlng = "";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        progressDialog = new ProgressDialog(SettingsActivity.this);
        progressDialog.setMessage("Loading...");
        pre=getSharedPreferences("app_dta", MODE_PRIVATE);
        edit=pre.edit();
        txtNotify = (TextView) findViewById(R.id.txtNotify);
        txtAdd1 = (EditText) findViewById(R.id.txtAdd1);
        txtAdd2 = (EditText) findViewById(R.id.txtAdd2);
        txtPinCode = (EditText) findViewById(R.id.txtPinCode);
        btnSave = (Button) findViewById(R.id.btnSaveAdd);

        //setActionBar();
        setMap();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    setAddress();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void setAddress() throws JSONException {
        progressDialog.show();
        String addressLine1 = txtAdd1.getText().toString();
        String addressLine2 = txtAdd2.getText().toString();
        String tmpPin = txtPinCode.getText().toString();
        if(addressLine1.equals("")){
            Toast.makeText(this, "You must enter your addressLine1!", Toast.LENGTH_LONG).show();
        }else if(tmpPin.equals("")){
            Toast.makeText(this, "You must enter your PinCode!", Toast.LENGTH_LONG).show();
        }else{
            long pin = Long.parseLong(tmpPin);
            int userID = pre.getInt("userID", 0);
            JSONObject object = new JSONObject();
            object.put("userId", userID);
            object.put("addressLine1", addressLine1);
            object.put("addressLine2", addressLine2);
            object.put("pincode", pin);
            object.put("latlong", latlng);
            String body = object.toString();
            PostGetRequest request = new PostGetRequest(Utils.POST_REQUEST, "http://dev.api.tidykart.com:8080/tidykart-ws/updateaddress", body){
                @Override
                protected void onPostExecute(String s) {
                   super.onPostExecute(s);
                    Log.d("Response", s);
                    progressDialog.dismiss();
                    Toast.makeText(SettingsActivity.this, "Response"+ s, Toast.LENGTH_LONG).show();
                    try {
                        JSONObject obj = new JSONObject(s);
                        Boolean isServiceAvailable = obj.getBoolean("iSServiceAvailable");
                        int addressId = obj.getJSONArray("userAddressInfo").getJSONObject(0).getInt("addressId");
                        edit.putInt("addressID", addressId);
                        edit.commit();
                        if(isServiceAvailable){
                            new AlertDialog.Builder(SettingsActivity.this)
                                    .setTitle("Success!")
                                    .setMessage("We are available at your area. Request for Pickup or schedule your pickups")
                                    .setPositiveButton("Next Step", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            Boolean mobileVerified = pre.getBoolean("mobileVerified", false);
                                            if(mobileVerified){
                                                Intent intent = new Intent(SettingsActivity.this, RequestOTP.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            Intent intent = new Intent(SettingsActivity.this, AppActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }else{
                            new AlertDialog.Builder(SettingsActivity.this)
                                    .setTitle("Place not supported")
                                    .setMessage("Sorry, we are not serving at your area currently . Soon We will be there")
                                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                            Boolean mobileVerified = pre.getBoolean("mobileVerified", false);
                                            if(mobileVerified){
                                                Intent intent = new Intent(SettingsActivity.this, RequestOTP.class);
                                                startActivity(intent);
                                                finish();
                                            }else{
                                                Intent intent = new Intent(SettingsActivity.this, AppActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    })
                                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                            finish();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    } catch (JSONException e) {
                        Log.e("Error", e.toString());
                    }
                }
            };
            request.execute();
        }
    }

    private void setMap(){
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            // Start the Intent by requesting a result, identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);

            // Hide the pick option in the UI to prevent users from starting the picker
            // multiple times.
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // BEGIN_INCLUDE(activity_result)
        if (requestCode == REQUEST_PLACE_PICKER) {
            // This result is from the PlacePicker dialog.

            if (resultCode == Activity.RESULT_OK) {
                /* User has picked a place, extract data.
                   Data is extracted from the returned intent by retrieving a Place object from
                   the PlacePicker.
                 */
                final Place place = PlacePicker.getPlace(data, this);

                /* A Place object contains details about that place, such as its name, address
                and phone number. Extract the name, address, phone number, place ID and place types.
                 */
                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                final LatLng ll = place.getLatLng();
                final CharSequence phone = place.getPhoneNumber();
                final String placeId = place.getId();
                String attribution = PlacePicker.getAttributions(data);

                if(attribution == null){
                    attribution = "";
                }
                txtAdd1.setText(address.toString());
                latlng = ll.toString();

                // Print data to debug log
                Log.d(TAG, "Place selected: " + placeId + " (" + name.toString() + address.toString() +")" + "LongLat: "+ll.toString());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        // END_INCLUDE(activity_result)
    }

    public void setActionBar(){
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View view = mInflater.inflate(R.layout.custom_actionbar, null);
        ImageView img = (ImageView) view.findViewById(R.id.imgAvatar);
        TextView txt = (TextView) view.findViewById(R.id.txtName);
        String name = pre.getString("firstName", "");
        String url = pre.getString("urlAvatar", "");
        Log.d("app", name+" "+url);
        Picasso.with(SettingsActivity.this).load(url).error(R.drawable.avatar).into(img);
        txt.setText(name);
        mActionBar.setCustomView(view);
        mActionBar.setDisplayShowCustomEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            LoginManager.getInstance().logOut();
            return true;
        }else if(id == R.id.action_setting){
            Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
