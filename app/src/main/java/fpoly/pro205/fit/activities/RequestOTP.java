package fpoly.pro205.fit.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import fpoly.pro205.fit.activities.PostGetRequest;
import fpoly.pro205.fit.activities.R;
import fpoly.pro205.fit.activities.VerifyOTPActivity;
import fpoly.pro205.fit.utils.Utils;

public class RequestOTP extends AppCompatActivity {

    EditText txtCountryCode, txtPhoneNumber;
    Button btnRequest;
    SharedPreferences pre;
    SharedPreferences.Editor edit;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_otp);
        progressDialog = new ProgressDialog(RequestOTP.this);
        progressDialog.setMessage("Loading...");
        pre=getSharedPreferences("app_dta", MODE_PRIVATE);
        edit=pre.edit();

        txtCountryCode = (EditText) findViewById(R.id.txtCountryCode);
        txtPhoneNumber = (EditText) findViewById(R.id.txtPhoneNumber);
        btnRequest = (Button) findViewById(R.id.btnRequestOTP);

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtCode = txtCountryCode.getText().toString();
                String txtPhone = txtPhoneNumber.getText().toString();
                if(txtCode.equals("")){
                    Toast.makeText(RequestOTP.this, "You must enter country code!", Toast.LENGTH_LONG).show();
                }else if(txtPhone.equals("")){
                    Toast.makeText(RequestOTP.this, "You must enter phone number!", Toast.LENGTH_LONG).show();
                }else{
                    int code = Integer.parseInt(txtCode);
                    int phone = Integer.parseInt(txtPhone);
                    int userID = pre.getInt("userID", 0);
                    String body = "";
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("userId", userID);
                        obj.put("countryMobileCode", code);
                        obj.put("mobileNumber", phone);
                        body = obj.toString();
                    } catch (JSONException e) {
                        Log.e("Error", e.toString());
                    }
                    PostGetRequest request = new PostGetRequest(Utils.POST_REQUEST, "http://dev.api.tidykart.com:8080/tidykart-ws/requestotp", body){
                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            progressDialog.dismiss();
                            Log.d("Response", s);
                            Toast.makeText(RequestOTP.this, "Response"+ s, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RequestOTP.this, VerifyOTPActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    };
                    request.execute();
                }
            }
        });
    }
}
