package ru.mess.messenger.fragments.main;


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
import java.util.HashSet;

import ru.mess.messenger.R;
import ru.mess.messenger.adapters.GroupAdapter;
import ru.mess.messenger.listeners.PayloadListener;
import ru.mess.messenger.models.Group;
import ru.mess.messenger.models.User;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class GroupsFragment extends Fragment implements PayloadListener {


    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private GroupAdapter adapter;
    View view;
    User current;
    ArrayList<Group> groups = new ArrayList<>();
    StompClient mStompClient;
    PayloadListener listener;
    ProgressBar bar;

    public static GroupsFragment newInstance(User user) {

        Bundle args = new Bundle();
        args.putSerializable("currentUser",user);
        GroupsFragment fragment = new GroupsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            current = (User) getArguments().getSerializable("currentUser");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;


        init();
        bindValues();
    }

    private void init()
    {
        recyclerView = (RecyclerView) view.findViewById(R.id.group_list);
        bar = (ProgressBar) view.findViewById(R.id.group_list_bar);
        setListener(this);
    }

    private void bindValues()
    {

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new GroupAdapter(groups,getContext(),current);

        adapter.getGroups().clear();
        recyclerView.setAdapter(adapter);


        //wait 2 seconds before starting the  websocket connection
        CountDownTimer countDownTimer = new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long l) {


            }

            @Override
            public void onFinish() {

                connect();
                send();

            }
        };;


        countDownTimer.start();
    }


    /**
     * Initiate connection to web service using web sockets
     */
    private void connect()
    {
        mStompClient = Stomp.over(WebSocket.class, "ws://10.0.2.2:8080/groups");



        mStompClient.topic("/topic/project_group_list")
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

    private void toast(String text) {
        Log.i("status", text);
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Connect to webservice using web sockets
     */
    private void send()
    {
        mStompClient.send("/app/groups")
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


        if (!payload.equals("")) {

            adapter.getGroups().clear();
            adapter.notifyDataSetChanged();

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Group>>() {
            }.getType();

            ArrayList<Group> groups = gson.fromJson(payload, type);


            HashSet<Group> set = new HashSet<>();

            set.addAll(groups);

            groups.clear();

            groups.addAll(set);


            for (Group group : groups) {
                adapter.addGroup(group);
            }


            adapter.notifyDataSetChanged();


            //mStompClient.disconnect();


        }
        else
        {
            toast("Failed creating group. Group name probably already exists");
        }




    }

    public void setListener(PayloadListener listener) {
        this.listener = listener;
    }
}
