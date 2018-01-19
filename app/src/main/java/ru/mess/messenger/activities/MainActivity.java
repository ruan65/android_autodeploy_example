package ru.mess.messenger.activities;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.techdew.stomplibrary.Stomp;
import com.techdew.stomplibrary.StompClient;

import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import eu.long1.spacetablayout.SpaceTabLayout;
import ru.mess.messenger.R;
import ru.mess.messenger.fragments.main.ContactsFragment;
import ru.mess.messenger.fragments.main.GroupsFragment;
import ru.mess.messenger.fragments.main.ProjectsFragment;
import ru.mess.messenger.models.Group;
import ru.mess.messenger.models.Instance;
import ru.mess.messenger.models.Project;
import ru.mess.messenger.models.User;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private ViewPager viewPager;
    private SpaceTabLayout spaceTabLayout;
    private NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    User thisUser = null;
    StompClient mStompClient;
//    public static final String URL = "ws://10.0.2.2:8080/groups_add";
    public static final String URL = "ws://172.22.16.82:8080/groups_add";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initChildObjects();
        bindValues(savedInstanceState);
    }


    private void initChildObjects()
    {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        spaceTabLayout = (SpaceTabLayout) findViewById(R.id.tabs);
        navigationView = (NavigationView) findViewById(R.id.nav_v);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        thisUser = (User) getIntent().getSerializableExtra("currentUser");

        setSupportActionBar(toolbar);



    }


    private void bindValues(Bundle bundle)
    {

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(ContactsFragment.newInstance(thisUser));
        fragmentList.add(GroupsFragment.newInstance(thisUser));
        fragmentList.add(new ProjectsFragment());

        spaceTabLayout.initialize(viewPager, getSupportFragmentManager(),
                fragmentList, bundle);

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {



        if (item.getItemId() == R.id.add_group)
        {

        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {





        getMenuInflater().inflate(R.menu.action_bar_menu,menu);
        TextView user = (TextView) menu.findItem(R.id.user_name);
        //user.setText(thisUser.getLogin());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.add_group)
        {
            showAddGroup();

        }


        return (super.onOptionsItemSelected(item));
    }


    /**
     * Display add group dialog
     */
    private void showAddGroup()
    {
        LayoutInflater li = LayoutInflater.from(this);
        final View addView = li.inflate(R.layout.add_group,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Group");

        EditText groupName = (EditText) addView.findViewById(R.id.add_group_name);
        EditText groupDescription = (EditText) addView.findViewById(R.id.add_group_description);


        builder.setView(addView);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                sendValidatedGroupData((groupName.getText().toString()),groupDescription.getText().toString());

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /**
     * Validate and send new group data to web service via websocket
     * @param name
     * @param description
     */
    private void sendValidatedGroupData(String name, String description) {


        if(validated(name,description)) {

            Group group = new Group();
            group.setName(name);
            group.setDescription(description);
            group.setId(0L);
            group.setInstance(new Instance());
            group.setMessages(new HashSet<>());
            group.setProject(new Project());

            Gson gson = new Gson();
            String data = gson.toJson(group);

            send(data);
        }
        else
        {
            toast("Fields cannot be empty");
        }



    }

    /**
     * Connect to webservice using web sockets
     * @param data
     */
    private void send(String data)
    {
        mStompClient = Stomp.over(WebSocket.class, URL);
        mStompClient.connect();
        mStompClient.send("/app/groups_add",data)
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



    /**
     * Validate new user dettails
     * @param username
     * @param password
     * @return
     */
    private boolean validated(String username , String password)
    {
        username.trim();
        password.trim();

        if (username.length() == 0 || password.length() == 0)
        {

            return false;
        }
        else
        {
            return true;
        }

    }


    private void toast(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}
