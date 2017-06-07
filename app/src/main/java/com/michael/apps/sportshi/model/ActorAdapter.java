package com.michael.apps.sportshi.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.michael.apps.sportshi.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ActorAdapter extends ArrayAdapter<Actors> {
    private ArrayList<Actors> actorList;
    private LayoutInflater vi;
    private int Resource;
    private ViewHolder holder;

    public ActorAdapter(Context context, int resource, ArrayList<Actors> objects) {
        super(context, resource, objects);
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        actorList = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         View v = convertView;
        if (v == null) {
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);
            holder.imageview = (ImageView) v.findViewById(R.id.imgIcon);
            holder.tvName = (TextView) v.findViewById(R.id.nama);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.tvName.setText(actorList.get(position).getUsername());
        Picasso.with(this.getContext()).load(actorList.get(position).getImage()).placeholder(R.layout.progress).resize(110,110).error(R.drawable.error).into(holder.imageview);
        return v;
    }

    static class ViewHolder {
        public ImageView imageview;
        public TextView tvName;
    }
}