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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.techdew.stomplibrary.Stomp;
import com.techdew.stomplibrary.StompClient;

import org.java_websocket.WebSocket;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ru.mess.messenger.R;
import ru.mess.messenger.adapters.ProjectAdapter;
import ru.mess.messenger.listeners.PayloadListener;
import ru.mess.messenger.models.Project;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * This fragment is not functional yet
 */
public class ProjectsFragment extends Fragment implements PayloadListener {

    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProjectAdapter adapter;
    View view;
    StompClient mStompClient;
    PayloadListener listener;
    ProgressBar bar;
    ArrayList<Project> projects = new ArrayList<>();

    public static final String URL = "ws://10.0.2.2:8080/proj";

    public ProjectsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_projects, container, false);
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
        recyclerView = (RecyclerView) view.findViewById(R.id.project_list);
        bar = (ProgressBar) view.findViewById(R.id.projects_loading_bar);
        setListener(this);
    }

    private void bindValues()
    {

        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        adapter = new ProjectAdapter(projects);

        recyclerView.setAdapter(adapter);

        adapter.getProjects().clear();

        adapter.addProject(new Project());



        CountDownTimer countDownTimer = new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long l) {


            }

            @Override
            public void onFinish() {

                //connect();
                //send();

            }
        };;


        countDownTimer.start();

    }




    /**
     * Connect to webservice using web sockets
     */
    private void connect()
    {
        mStompClient = Stomp.over(WebSocket.class, URL);



        mStompClient.topic("/topic/project_list")
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
                            //do something when a connection successfully opens

                            break;
                        case ERROR:
                            //do something when a connection fails to open
                            break;
                        case CLOSED:
                            //do something when a connection closes
                    }
                });
    }



    /**
     * Connect to webservice using web sockets
     */
    private void send()
    {
        mStompClient.send("/app/proj")
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
        Type type = new TypeToken<ArrayList<Project>>(){}.getType();

        ArrayList<Project> projects = gson.fromJson(payload, type);

        for (Project project : projects)
        {

            adapter.addProject(project);
        }

        adapter.notifyDataSetChanged();


        mStompClient.disconnect();






    }

    public void setListener(PayloadListener listener) {
        this.listener = listener;
    }



}

