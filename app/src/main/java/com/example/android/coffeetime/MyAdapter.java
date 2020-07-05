package com.example.android.coffeetime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<userHelper> userHelpers;

    public MyAdapter(Context c, ArrayList<userHelper> userData){
        context = c;
        userHelpers = userData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.users, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(userHelpers.get(position).getName());
        holder.username.setText(userHelpers.get(position).getUsername());
        holder.email.setText(userHelpers.get(position).getEmail());
        holder.phone.setText(userHelpers.get(position).getPhone());
    }

    @Override
    public int getItemCount() {
        return userHelpers.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, username, email, phone;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.show_fullName);
            username = (TextView) itemView.findViewById(R.id.show_username);
            email = (TextView) itemView.findViewById(R.id.show_email);
            phone = (TextView) itemView.findViewById(R.id.show_phone);

        }
    }
}
