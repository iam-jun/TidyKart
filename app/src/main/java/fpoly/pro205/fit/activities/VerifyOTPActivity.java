package fpoly.pro205.fit.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import fpoly.pro205.fit.activities.AppActivity;
import fpoly.pro205.fit.activities.R;
import fpoly.pro205.fit.utils.Utils;

public class VerifyOTPActivity extends AppCompatActivity {

    EditText txtOTP;
    Button btnVerify;
    ProgressDialog progressDialog;
    SharedPreferences pre;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        pre=getSharedPreferences("app_dta", MODE_PRIVATE);
        edit=pre.edit();
        progressDialog = new ProgressDialog(VerifyOTPActivity.this);
        progressDialog.setMessage("Loading...");

        txtOTP = (EditText) findViewById(R.id.txtOPTCode);
        btnVerify = (Button) findViewById(R.id.btnVerifyOTP);
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                String code = txtOTP.getText().toString();
                int userID = pre.getInt("userID", 0);

                if(code.equals("")){
                    Toast.makeText(VerifyOTPActivity.this, "You must enter OTP code!", Toast.LENGTH_LONG).show();
                }else{
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("userId", userID);
                        obj.put("otp", Integer.parseInt(code));
                        String body = obj.toString();
                        PostGetRequest request = new PostGetRequest(Utils.POST_REQUEST, "http://dev.api.tidykart.com:8080/tidykart-ws/verifyotp", body){
                            @Override
                            protected void onPostExecute(String s) {
                                super.onPostExecute(s);
                                progressDialog.dismiss();
                                Log.d("Response", s);
                                Toast.makeText(VerifyOTPActivity.this, "Response"+ s, Toast.LENGTH_LONG).show();
                                try {
                                    JSONObject obj = new JSONObject(s);
                                    Boolean mobileverified = obj.getBoolean("mobileverified");
                                    Boolean validpin = obj.getBoolean("validpin");
                                    if(!validpin){
                                        new AlertDialog.Builder(VerifyOTPActivity.this)
                                                .setTitle("Invalid OTP code")
                                                .setMessage("Your OTP code is invalid!")
                                                .setPositiveButton("Try again!", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                    }
                                    if(mobileverified){
                                        Intent intent = new Intent(VerifyOTPActivity.this, AppActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    Log.e("Error", e.toString());
                                }
                            }
                        };
                        request.execute();
                    } catch (JSONException e) {
                        Log.e("Error", e.toString());
                    }
                }

            }
        });

    }
}
