package ru.mess.messenger.activities.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.techdew.stomplibrary.Stomp;
import com.techdew.stomplibrary.StompClient;

import org.java_websocket.WebSocket;

import ru.mess.messenger.R;
import ru.mess.messenger.listeners.PayloadListener;
import ru.mess.messenger.models.Group;
import ru.mess.messenger.models.Message;
import ru.mess.messenger.models.User;
import ru.mess.messenger.tools.Utils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GroupChatActivity extends AppCompatActivity implements View.OnClickListener, PayloadListener {

    TextView currentUsername;
    EditText message;
    LinearLayout chats;
    ImageView sendMessage;
    User currentUser;
    private StompClient mStompClient;
    PayloadListener listener;
    Group group;

    public static final String URL = "ws://10.0.2.2:8080/groupchat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        init();
        bindValues();
    }


    private void init() {



        message = (EditText) findViewById(R.id.messagebox);
        currentUsername = (TextView) findViewById(R.id.current_user_group_chat_name);
        sendMessage = (ImageView) findViewById(R.id.send_message);
        chats = (LinearLayout) findViewById(R.id.group_chat_log);

        setListener(this);


        currentUser =(User) getIntent().getSerializableExtra("currentUser");
        group = (Group) getIntent().getSerializableExtra("group");
        currentUser.setLogin(Utils.capitalizeFirst(currentUser.getLogin()));

        connect();


    }

    private void bindValues()
    {
        sendMessage.setOnClickListener(this);
        currentUsername.setText(group.getName());
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

        if (text.length() > 0) {

            Message imessage = new Message();
            imessage.setContent(text);
            imessage.setId(currentUser.getId());
            imessage.setSenderId(currentUser.getId() + "");
            imessage.setSenderName(Utils.capitalizeFirst(currentUser.getLogin()));



            Gson gson = new Gson();
            String payload = gson.toJson(imessage);


            mStompClient.send("/app/groupchat/"+group.getId()+"/", payload)
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


            message.getText().clear();


        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        mStompClient.disconnect();
    }



    @Override
    public void onSuccess(String payload) {

        Gson gson = new Gson();

        Message message = gson.fromJson(payload, Message.class);


        messageContainer(message);


        }


    /**
     * Connects to a specific websocket and subscribes to a dynamic group id
     */
    private void connect()
        {
            mStompClient =Stomp.over(WebSocket.class, URL);

            mStompClient.connect();

            mStompClient.topic("/topic/group/"+group.getId()+"/")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(topicMessage -> {

                        String response = topicMessage.getPayload();
                        listener.onSuccess(response);

                    });



            mStompClient.lifecycle()
                    .subscribe(lifecycleEvent -> {
                        switch (lifecycleEvent.getType()) {
                            case OPENED:
                               //Do something when connection successfully opens

                                break;
                            case ERROR:
                                //Do something when connection fails to open

                                break;
                            case CLOSED:
                                //Do something when connection closes
                        }
                    });
        }


    public void setListener(PayloadListener listener) {
        this.listener = listener;
    }


    /**
     * Programmatically adjusts margins of the message view
     * @param view
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    private void setMargins(View view, int left, int top, int right , int bottom)
    {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams)
        {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left,top,right,bottom);
            view.requestLayout();
        }
    }


    /**
     * Formats and handles incoming and outgoing messages
     * @param message
     */
    private void messageContainer(Message message)
    {
        Log.d("status", "payload received -> " + message.getContent());

        View itemView = getLayoutInflater().inflate(R.layout.message_item, null);

        CardView itemFrame = (CardView) itemView.findViewById(R.id.textframe);

        if (message.getSenderName().equals(currentUser.getLogin())) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT;
            itemFrame.setLayoutParams(params);
            itemFrame.setCardBackgroundColor(getResources().getColor(R.color.green));
            setMargins(itemFrame,20,20,20,20);
        }

        TextView content = (TextView) itemView.findViewById(R.id.content);
        TextView name = (TextView) itemView.findViewById(R.id.sender_name);
        TextView time = (TextView) itemView.findViewById(R.id.sender_time);


        content.setText(message.getContent());
        name.setText(message.getSenderName());
        time.setText(message.getSendtime());


        chats.addView(itemView);
    }


}
