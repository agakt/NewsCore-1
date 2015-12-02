package com.example.admin.webcrawler;

import android.app.Activity;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.snu.ids.ha.ma.MExpression;
import org.snu.ids.ha.ma.MorphemeAnalyzer;
import org.snu.ids.ha.ma.Sentence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {
    CallbackManager callbackManager;
    LoginButton loginButton;
    ArrayList<String> likes =  new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.enableDefaults();

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.setReadPermissions(Arrays.asList("user_likes"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                System.out.println("onSuccess");
                GraphRequest request = GraphRequest.newMeRequest
                        (loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback()
                        {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response)
                            {
                                try {
                                    Toast.makeText(MainActivity.this, "completed call", Toast.LENGTH_SHORT).show();
                                    JSONObject result = response.getJSONObject();
                                    JSONObject datas = result.getJSONObject("likes");
                                    JSONArray likeLists = datas.getJSONArray("data");
                                    for(int i=0;i<likeLists.length();i++) {
                                        String name=likeLists.getJSONObject(i).getString("name");
                                        likes.add(name);
                                    }
                                    Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                                    intent.putStringArrayListExtra("likes",likes);
                                    startActivity(intent);

                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "likes");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel()
            {
                System.out.println("onCancel");
            }

            @Override
            public void onError(FacebookException exception)
            {
                System.out.println("onError");
            }
        });
    }

    public ArrayList<String> getOnlyNameList(String str) {
        ArrayList<String> arr = new ArrayList<String>();

        try {
            MorphemeAnalyzer ma = new MorphemeAnalyzer();
            List<MExpression> ret = ma.analyze(str);
            ret = ma.postProcess(ret);
            ret = ma.leaveJustBest(ret);

            List<Sentence> stl = ma.divideToSentences(ret);

            for (int i = 0; i < stl.size(); i++) {
                Sentence st = stl.get(i);
                for (int j = 0; j < st.size(); j++) {
                    if (st.get(j).containsTagOf(1)) {
                        arr.add(st.get(j).getFirstMorp().getString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
