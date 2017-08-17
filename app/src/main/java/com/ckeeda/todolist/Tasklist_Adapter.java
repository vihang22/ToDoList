package com.ckeeda.todolist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by HP on 16-Aug-17.
 */

class Tasklist_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int EMPTY_VIEW =  1;
    ArrayList<String> task_list;
    Context context;
    private FirebaseDatabase mFirebaseDB;
    private DatabaseReference mDBref;

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    class MyviewHolder extends RecyclerView.ViewHolder{

        TextView task_desc;
        Button done;
        public MyviewHolder(View itemView) {
            super(itemView);
            task_desc = (TextView)itemView.findViewById(R.id.task_desc);
            done = (Button) itemView.findViewById(R.id.done);
        }
    }

    public Tasklist_Adapter(ArrayList<String> task_list,Context context) {
        this.task_list = task_list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == EMPTY_VIEW) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_list, parent, false);
            EmptyViewHolder evh = new EmptyViewHolder(v);
            return evh;
        }


        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);
        MyviewHolder viewholder = new MyviewHolder(item);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyviewHolder) {

            ((MyviewHolder) holder).task_desc.setText(task_list.get(position));
            ((MyviewHolder) holder).done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFirebaseDB = FirebaseDatabase.getInstance();
                    mDBref = mFirebaseDB.getReference("Tasks");
                    Query q = mDBref.orderByValue().equalTo(task_list.get(position).toString());
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                Log.d("Delete", appleSnapshot.getValue().toString());
                                appleSnapshot.getRef().removeValue();
                                task_list.remove(position);
                                notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return task_list.size() > 0 ? task_list.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (task_list.size() == 0) {
            return EMPTY_VIEW;
        }
        return super.getItemViewType(position);
    
     
    }
}
