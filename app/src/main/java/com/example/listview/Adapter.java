package com.example.listview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>  {



    ArrayList<Model> models;
    Context mContext;
    int selectCount = 0;
    boolean isAscending=false;


    public Adapter(ArrayList<Model> models, Context mContext) {
        this.models =models;
        this.mContext = mContext;;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row,viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        Model model=models.get(i);
        if(model!=null){
            viewHolder.title.setText(model.getTitle());
            viewHolder.url.setText(model.getUrl());
            viewHolder.position=i;

            viewHolder.image.setImageBitmap(model.getImage());
            if(model.isSelect()){
                viewHolder.view.setBackgroundColor(mContext.getResources().getColor(R.color.lightBlue));
            }
            else{
                viewHolder.view.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            }
        }
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model model1=models.get(i);
                if(model1.isSelect()){
                    model1.setSelect(false);
                    selectCount--;


                }else{
                    model1.setSelect(true);
                    selectCount++;
                }
                Toast.makeText(mContext, selectCount+ " Selected", Toast.LENGTH_SHORT).show();
                models.set(viewHolder.position,model1);
                notifyItemChanged(viewHolder.position);

            }
        });



    }

    @Override
    public int getItemCount() {
        if(models!=null){
            return models.size();
        }
        return 0;
    }

    public void removeItem(int position) {
        final Model model = models.remove(position);
        notifyItemRemoved(position);

    }

    //Add an item at position and notify changes.
    public void addItem(Model model) {
        ArrayList<Model> temp = new ArrayList<>(models);

        temp.add(model);
        if(isAscending) {
            Collections.sort(temp, Comparator.comparing(Model::getTitle));

        }
        else
        {
            Collections.sort(temp, Comparator.comparing(Model::getTitle));
            Collections.reverse(temp);

        }
        int position=temp.indexOf(model);
         models.add(position, model);
        notifyItemInserted(position);
      //  models.add(model);
        //notifyItemRangeChanged(0, models.size());

    }



    public void sortData() {
        if(!isAscending) {
            Collections.sort(models, Comparator.comparing(Model::getTitle));
            isAscending=true;
        }
        else
        {
            Collections.sort(models, Comparator.comparing(Model::getTitle));
            Collections.reverse(models);
            isAscending=false;
        }

        notifyItemRangeChanged(0, models.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView image;
        public TextView title;
        public TextView url;
        public View view;
        public int position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            image=itemView.findViewById(R.id.icon);
            title=itemView.findViewById(R.id.txtTitle);
            url=itemView.findViewById(R.id.txtURL);
        }
    }
}
