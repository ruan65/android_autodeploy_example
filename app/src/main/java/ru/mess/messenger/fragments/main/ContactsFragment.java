package ru.mess.messenger.fragments.main;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.techdew.stomplibrary.Stomp;
import com.techdew.stomplibrary.StompClient;

import org.java_websocket.WebSocket;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ru.mess.messenger.R;
import ru.mess.messenger.adapters.ContactsAdapter;
import ru.mess.messenger.listeners.PayloadListener;
import ru.mess.messenger.models.User;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ContactsFragment extends Fragment implements PayloadListener{


    ArrayList<User> people = new ArrayList<>();
    StompClient mStompClient;

    PayloadListener listener;

    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ContactsAdapter adapter;
    ProgressBar bar;
    View view;
    User currentUser;
    public static final String URL = "ws://10.0.2.2:8080/users";


    public ContactsFragment() {
        // Required empty public constructor
    }

    public static ContactsFragment newInstance(User currentUser) {

        Bundle args = new Bundle();
        args.putSerializable("currentUser",currentUser);
        ContactsFragment fragment = new ContactsFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() !=null)
        {
            currentUser = (User) getArguments().getSerializable("currentUser");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;


        initChildView();
        bindValues();
    }


    private void initChildView()
    {
        recyclerView = (RecyclerView) view.findViewById(R.id.contact_list);
        bar = (ProgressBar) view.findViewById(R.id.contacts_loading_bar);
        setListener(this);
    }

    private void bindValues()
    {



        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new ContactsAdapter(people,getContext(),currentUser);

        adapter.getContacts().clear();

        recyclerView.setAdapter(adapter);


        //Wait  2 seconds before connecting to websocket
        CountDownTimer countDownTimer = new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long l) {


            }

            @Override
            public void onFinish() {

                connect();
                send();

            }
        };


        countDownTimer.start();

    }


    /**
     * Initiate connection to web service using web sockets
     */
    private void connect()
        {
            mStompClient =Stomp.over(WebSocket.class, URL);



            mStompClient.topic("/topic/user_list")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(topicMessage -> {



                        String response = topicMessage.getPayload();

                        Log.d("status", "payload invoked times" + response);
                        listener.onSuccess(response);





                    });


            mStompClient.connect();
            mStompClient.lifecycle()
                    .subscribe(lifecycleEvent -> {
                        switch (lifecycleEvent.getType()) {
                            case OPENED:
                                //toast("Stomp connection opened");

                                break;
                            case ERROR:
                                //toast("Stomp connection error");
                                break;
                            case CLOSED:
                                //toast("Stomp connection closed");
                        }
                    });
        }


    /**
     * Initiate and subscribe to users channel
     */
    private void send()
        {
            mStompClient.send("/app/users")
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

        @Override
        public void onSuccess(String payload) {



            bar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);


            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<User>>(){}.getType();

            ArrayList<User> contactList = gson.fromJson(payload, type);

            for (User user : contactList)
            {

                adapter.addContact(user);
            }

            adapter.notifyDataSetChanged();


            mStompClient.disconnect();






    }

    public void setListener(PayloadListener listener) {
        this.listener = listener;
    }
}
