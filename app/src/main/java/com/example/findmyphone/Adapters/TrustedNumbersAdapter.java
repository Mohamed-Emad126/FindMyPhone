package com.example.findmyphone.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findmyphone.DB_VM.TrustedPerson;
import com.example.findmyphone.R;

import java.util.List;

public class TrustedNumbersAdapter extends RecyclerView.Adapter<TrustedNumbersAdapter.MyHolder> {


    private List<TrustedPerson> mList;


    public void setList(List<TrustedPerson> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trusted_item,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        TrustedPerson contact = mList.get(position);

        holder.name.setText(contact.getName());
        holder.number.setText(contact.getNumber());
    }

    @Override
    public int getItemCount() {
        if(mList == null){
            return 0;
        }
        return mList.size();
    }

    public TrustedPerson getPerson(int position){
        return mList.get(position);
    }




    public class MyHolder extends RecyclerView.ViewHolder{


        TextView name,number;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);
        }
    }
}
