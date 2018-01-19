package ru.mess.messenger.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.techdew.stomplibrary.Stomp;
import com.techdew.stomplibrary.StompClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.java_websocket.WebSocket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import ru.mess.messenger.activities.LoginActivity;
import ru.mess.messenger.activities.MainActivity;
import ru.mess.messenger.listeners.PayloadListener;
import ru.mess.messenger.models.User;
import ru.mess.messenger.models.authentication.AuthorizationResponse;
import ru.mess.messenger.models.authentication.Credentials;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Shugig on 11/16/2017.
 */

public class FetchProfileThread extends AsyncTask<String,Void,User> implements PayloadListener{

    private LoginActivity activity;
    AlertDialog dialog;

    private static final char Q = '"';
    public static final String TAG="StompClient";
    private StompClient mStompClient;


    PayloadListener listener;


    public FetchProfileThread(LoginActivity context, AlertDialog dialog) {

        this.activity = context;
        this.dialog = dialog;

    }

    @Override
    protected void onPreExecute() {
        dialog = new AlertDialog.Builder(activity).create();
        dialog.setTitle("Login result");
        dialog.setMessage("Fetching Profile..");
        mStompClient = Stomp.over(WebSocket.class, "ws://10.0.2.2:8080/mobile-login");
        setListener(this);



    }

    @Override
    protected User doInBackground(String... paramss) {

        mStompClient.topic("/topic/mobile-login")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {

                    String response = topicMessage.getPayload();
                    Log.d("tatus",response);
                    toast(response);

                    listener.onSuccess(response);

                });

        mStompClient.connect();
        mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            toast("Stomp connection opened");

                            break;
                        case ERROR:
                            toast("Stomp connection error");
                            break;
                        case CLOSED:
                            toast("Stomp connection closed");
                    }
                });


        sendUserData(new Credentials(paramss[0],paramss[1]));


        return  null;
    }



    private void sendUserData(Credentials credentials) {


        Gson gson = new Gson();
        String payload = gson.toJson(credentials); //"{"+Q+"username"+Q+":"+Q+"nelio"+Q+", "+Q+"password"+Q+":"+Q+"testing"+Q+"}";


        mStompClient.send("/app/mobile-login", payload)
                .compose(tFlowable -> tFlowable
                        .unsubscribeOn(Schedulers.newThread())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()))
                .subscribe(aVoid -> {

                    Log.d("status", "success");
                }, throwable ->
                {
                    Log.e("status", "fail", throwable);
                });
    }


    private void toast(String text) {
        Log.i(TAG, text);
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

    public void setListener(PayloadListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSuccess(String payload) {

        Gson gson = new Gson();

        AuthorizationResponse authorization = gson.fromJson(payload,AuthorizationResponse.class);


            if (authorization.isAuthorized()) {

                Log.d("lookatt",authorization.toString());
                //mStompClient.disconnect();
                User user = authorization.getPayload();

                dialog.dismiss();
                Intent intent = new Intent(activity, MainActivity.class);

                Log.d("tatusss",user.toString());
                intent.putExtra("currentUser", user);
                activity.startActivity(intent);
                Log.d("statusss", user.toString());


               // mStompClient.disconnect();
                activity.finish();
            } else {
                dialog.setMessage("Email or password is incorrect");
                //dialog.show();
            }

    }

}
