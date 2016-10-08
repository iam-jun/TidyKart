package fpoly.pro205.fit.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import fpoly.pro205.fit.activities.AppActivity;
import fpoly.pro205.fit.activities.R;
import fpoly.pro205.fit.utils.Utils;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.google.android.gms.analytics.internal.zzy.e;

public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    LoginButton loginButton;
    String txtURL = "https://thesocietypages.org/socimages/files/2009/05/nopic_192.gif";
    String txtLastName = "No name";
    String txtFirstName = "";
    String txtEmail = "";
    int userID = 0;
    SharedPreferences pre;
    SharedPreferences.Editor edit;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        pre=getSharedPreferences("app_dta", MODE_PRIVATE);
        edit=pre.edit();
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        if(isLoggedIn()){
            Intent intent = new Intent(MainActivity.this, AppActivity.class);
            startActivity(intent);
            finish();
        }
        List<String> permissions = new ArrayList<String>();
        permissions.add("email");
        permissions.add("public_profile");
        loginButton.setReadPermissions(permissions);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject json, GraphResponse response) {
                                if (response.getError() != null) {
                                    // handle error
                                    Log.d("app", "ERROR");
                                } else {
                                    Log.d("app", "Success");
                                    try {

                                        String jsonresult = String.valueOf(json);
                                        Log.d("app", "JSON Result"+jsonresult);

                                        String str_id = json.getString("id");
                                        txtFirstName = json.getString("first_name");
                                        txtLastName = json.getString("last_name");
                                        txtEmail = json.getString("email");
                                        JSONObject data = response.getJSONObject();
                                        if (data.has("picture")) {
                                            txtURL = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                        }
                                        Log.d("app", txtURL);
                                        signUp(txtLastName, txtFirstName, txtEmail);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email, picture.type(large)"); // Parámetros que pedimos a facebook
                        request.setParameters(parameters);
                        request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(MainActivity.this, "Error: "+e, Toast.LENGTH_LONG).show();
            }
        });

    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }


     public void signUp(final String txtLastName, final String txtFirstName, final String txtEmail){
         Log.d("app", "sign up");
         progressDialog.show();
         try {
             JSONObject object = new JSONObject();
             object.put("lastName", txtLastName);
             object.put("firstName", txtFirstName);
             object.put("emailAddress", txtEmail);
             String body = object.toString();
             PostGetRequest request = new PostGetRequest(Utils.POST_REQUEST, "http://dev.api.tidykart.com:8080/tidykart-ws/login", body) {
                 @Override
                 protected void onPostExecute(String s) {
                     super.onPostExecute(s);
                     Log.d("Response", s);
                     Toast.makeText(MainActivity.this, "Response"+ s, Toast.LENGTH_LONG).show();
                     progressDialog.dismiss();
                     edit.putString("firstName", txtFirstName);
                     edit.putString("lastName", txtLastName);
                     edit.putString("email", txtEmail);
                     edit.putString("urlAvatar", txtURL);
                     edit.commit();
                     try {
                         JSONObject obj = new JSONObject(s);
                         userID = obj.getJSONArray("userInfo").getJSONObject(0).getInt("userId");
                         Boolean mobileVerified = obj.getJSONArray("userInfo").getJSONObject(0).getBoolean("mobileVerified");
                         edit.putBoolean("mobileVerified", mobileVerified);
                         edit.putInt("userID", userID);
                         boolean isFirstTimeUser = obj.getBoolean("isFirstTimeUser");
                         edit.putBoolean("isFirstTimeUser", isFirstTimeUser);
                         edit.commit();
                         if(isFirstTimeUser){
                             Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                             startActivity(intent);
                             finish();
                         }else if(!mobileVerified){
                             Intent intent = new Intent(MainActivity.this, RequestOTP.class);
                             startActivity(intent);
                             finish();
                         }else{
                             Intent intent = new Intent(MainActivity.this, AppActivity.class);
                             startActivity(intent);
                             Log.d("Result", s);
                             finish();
                         }
                     } catch (JSONException e) {
                         Log.e("Error Convert Json", e.toString());
                     }

                 }
             };
             request.execute();
         }
         catch (Exception e){
             Log.d("Error", e.toString());
         }
     }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}
