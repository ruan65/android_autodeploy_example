package ru.mess.messenger.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import ru.mess.messenger.R;
import ru.mess.messenger.tools.FetchProfileThread;
import ru.mess.messenger.tools.Utils;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener {


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    Button mEmailSignInButton;
    AlertDialog dialog;


    TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        initChildViews();
        bindValues();


    }

    /**
     * Initialize all views
     */
    private void initChildViews() {

        InputStream inputStream = getResources().openRawResource(R.raw.text);

        byte[] b = new byte[0];
        try {
            b = new byte[inputStream.available()];
            inputStream.read(b);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        dialog = new AlertDialog.Builder(this).create();

        title = findViewById(R.id.main_title);

        title.setText(new String(b));

    }


    private void bindValues() {


        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {

                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton.setOnClickListener(this);


    }


    /**
     * validate credentials
     *
     * @param username
     * @param password
     * @return
     */
    private boolean validated(String username, String password) {
        username.trim();
        password.trim();

        if (username.length() == 0 || password.length() == 0) {
            return false;
        } else {
            return true;
        }

    }


    @Override
    public void onClick(View view) {

        if (view == mEmailSignInButton) {

            String username = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();


            if (validated(username, password)) {
                doLogin(username, password);
            } else {
                dialog.setMessage("Username or password cannot be empty");
            }


        }

    }


    /**
     * Login using Volley.  This implementation makes a post request using username and
     * password parameters
     *
     * @param username
     * @param password
     */
    private void doLogin(String username, String password) {
        dialog.setTitle("Login");
        dialog.setMessage("Authenticating..");
        dialog.show();


        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest req = new StringRequest(Request.Method.POST, Utils.BASE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (response.contains("Invalid username and password")) {
                    dialog.setMessage("Invalid username and password");


                } else if (response.contains("Welcome to the project communicator!")) {


                    FetchProfileThread worker = new FetchProfileThread(LoginActivity.this, dialog);
                    worker.execute(username, password);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.setMessage("Server error");


            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }

        };

        queue.add(req);
    }


}

