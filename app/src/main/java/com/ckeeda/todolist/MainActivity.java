package com.ckeeda.todolist;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    private ListView listview;
    private Toolbar tb;
    private FirebaseDatabase mFirebaseDB;
    private DatabaseReference mDBref;
    private String userId;
    Tasklist_Adapter mAdapter;
    private static ArrayList<String> Task_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tb = (Toolbar) findViewById(R.id.toolbar);
        recyclerview = (RecyclerView) findViewById(R.id.recycler_view);
        Task_list = new ArrayList<>();
        setSupportActionBar(tb);

        //getTasklist();
        new Load().execute();

         mAdapter = new Tasklist_Adapter(Task_list,this);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getApplicationContext());
        recyclerview.setLayoutManager(lm);
        recyclerview.setAdapter(mAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              showDialog();
            }
        });

    }

   private void addTask(final String task){
       if(TextUtils.isEmpty(task)){
           Toast.makeText(getApplicationContext(),"Task Description is blank!!!",Toast.LENGTH_LONG).show();
           return;
       }

       mFirebaseDB = FirebaseDatabase.getInstance();
       mDBref = mFirebaseDB.getReference("Tasks");
       mDBref.keepSynced(true);
       Query q = mDBref.orderByValue().equalTo(task);
       q.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                   Toast.makeText(getApplicationContext(),"Task " +appleSnapshot.getValue(String.class) + " Already Exists..!!"
                           ,Toast.LENGTH_LONG).show();
                    return;
               }
               userId = mDBref.push().getKey();
               mDBref.child(userId).setValue(task);

               MainActivity.Task_list.add(task);
               mAdapter.notifyDataSetChanged();

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

   }

   private void getTasklist() {
       mFirebaseDB = FirebaseDatabase.getInstance();
       mDBref = mFirebaseDB.getReference("Tasks");
       mDBref.keepSynced(true);

       ValueEventListener listener = new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {

               for(DataSnapshot tasksnapshot : dataSnapshot.getChildren()){
                   Log.d("TASKS", tasksnapshot.getValue(String.class).toString());
                   if(!Task_list.contains(tasksnapshot.getValue(String.class))) {
                       MainActivity.Task_list.add(tasksnapshot.getValue(String.class));
                       mAdapter.notifyDataSetChanged();

                   }

               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       };
       mDBref.addValueEventListener(listener);
       mDBref.addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(DataSnapshot dataSnapshot, String s) {

          //     for(DataSnapshot tasksnapshot : dataSnapshot.getChildren()) {
          //         MainActivity.Task_list.add(dataSnapshot.getValue(String.class));
          //        Task_list.
          //         mAdapter.notifyDataSetChanged();
         //      }
           }

           @Override
           public void onChildChanged(DataSnapshot dataSnapshot, String s) {

           }

           @Override
           public void onChildRemoved(DataSnapshot dataSnapshot) {
               Log.d("Child removed",dataSnapshot.getValue().toString());
               Task_list.remove(dataSnapshot.getValue(String.class));
                       mAdapter.notifyDataSetChanged();
           }

           @Override
           public void onChildMoved(DataSnapshot dataSnapshot, String s) {

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });



   }

   void showDialog(){
       final EditText input = new EditText(getApplicationContext());
       input.setTextColor(Color.BLACK);

       input.setWidth(80);
       AlertDialog.Builder alertdialog = new AlertDialog.Builder(MainActivity.this);
       alertdialog.setTitle("Add Task");
       alertdialog.setMessage("Task Description:");
       alertdialog.setView(input);
       alertdialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               String input_text = input.getText().toString();
               addTask(input_text);
           }
       });
       alertdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               Log.v("ToDo List","Negative Button Pressed");
           }
       });
       alertdialog.create();
       alertdialog.show();

   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add_task ){
                 showDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    class Load extends AsyncTask<Void,Void, Void> {

        ProgressDialog progDailog;

        @Override
        protected Void doInBackground(Void... voids) {
            getTasklist();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = new ProgressDialog(MainActivity.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progDailog.dismiss();
        }
    }
}
