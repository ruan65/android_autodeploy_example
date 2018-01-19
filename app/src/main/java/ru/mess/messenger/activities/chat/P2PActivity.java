package ru.mess.messenger.activities.chat;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.techdew.stomplibrary.Stomp;
import com.techdew.stomplibrary.StompClient;

import org.java_websocket.WebSocket;

import java.util.ArrayList;

import ru.mess.messenger.R;
import ru.mess.messenger.adapters.ChatAdapter;
import ru.mess.messenger.listeners.PayloadListener;
import ru.mess.messenger.models.Message;
import ru.mess.messenger.models.User;
import ru.mess.messenger.tools.Utils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * This activity is not functional yet
 */

public class P2PActivity extends AppCompatActivity implements View.OnClickListener{

    TextView recipientId;
    EditText message;
    RecyclerView chats;
    ImageView sendMessage;

    private RecyclerView.LayoutManager mLayoutManager;
    private ChatAdapter adapter;

    User currentUser;
    User endUser;


    ArrayList<Message> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);

        initChildView();
        bindValues();
    }


    private void initChildView() {
        message = (EditText) findViewById(R.id.messagebox);
        sendMessage = (ImageView) findViewById(R.id.send_message);
        chats = (RecyclerView) findViewById(R.id.chat_log);
        recipientId = (TextView) findViewById(R.id.recipient_id);

        chats.setHasFixedSize(true);
        //recyclerView.addItemDecoration(new DividerItemDecoration(context));
        mLayoutManager = new LinearLayoutManager(this);
        chats.setLayoutManager(mLayoutManager);

        ChatAdapter adapter = new ChatAdapter(messages);
        chats.setAdapter(adapter);




        currentUser =(User) getIntent().getSerializableExtra("currentUser");
        endUser =(User) getIntent().getSerializableExtra("selectedUser");


    }

    private void bindValues()
    {
        recipientId.setText(Utils.capitalizeFirst(endUser.getLogin()));
        sendMessage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view == sendMessage)
        {

            sendMessage();
        }

    }


    void sendMessage()
    {


        String text = message.getText().toString();



        P2PThread p2PThread = new P2PThread(this,currentUser,adapter);
        p2PThread.execute(text);

        message.getText().clear();

    }

    private void toast(String text) {
        Log.i("status", text);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    class P2PThread extends AsyncTask<String,String,User> implements PayloadListener {


        private StompClient mStompClient;
        PayloadListener listener;
        P2PActivity activity;
        User currentUser;
        ChatAdapter adapter;


        public P2PThread(P2PActivity activity,User currentUser,ChatAdapter adapter) {

            this.activity = activity;
            this.currentUser = currentUser;
            mStompClient = Stomp.over(WebSocket.class, "ws://10.0.2.2:8080/p2p");
            this.adapter = adapter;

            setListener(this);
        }


        @Override
        protected User doInBackground(String... paramss) {



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


            sendUserData(paramss[0]);


            return null;
        }


        private void sendUserData(String rawMessage) {

            Message message = new Message();
            message.setContent(rawMessage);
            message.setId(0);
            message.setSenderId(currentUser.getId() + "");
            message.setSenderName(currentUser.getLogin());


            Gson gson = new Gson();
            String payload = gson.toJson(message);
            mStompClient.send("app/p2p", payload);
            //mStompClient.se
        }


        public void setListener(PayloadListener listener) {
            this.listener = listener;
        }

        @Override
        public void onSuccess(String payload) {

            Gson gson = new Gson();

            Message message = gson.fromJson(payload, Message.class);

            messages.add(message);
            ChatAdapter adapter = new ChatAdapter(messages);
            chats.setAdapter(adapter);
            chats.getAdapter().notifyDataSetChanged();


        }


        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);

            mStompClient.topic("/topic/p2pchat/"+endUser.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(topicMessage -> {

                        String response = topicMessage.getPayload();

                        publishProgress(response);

                        listener.onSuccess(response);

                    });
        }

        private void toast(String text) {
            Log.i("status", text);
            Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
        }
    }
}
