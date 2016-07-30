package com.sbtn.androidtv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sbtn.androidtv.R;
import com.sbtn.androidtv.datamodels.SBTNPackage;
import com.sbtn.androidtv.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by hoanguyen on 6/3/16.
 */
public class ListAdapterPackage extends BaseAdapter {
    private ArrayList<SBTNPackage> listDatas;
    private LayoutInflater layoutInflater;

    public ListAdapterPackage(Context context, ArrayList<SBTNPackage> listPackage) {
        layoutInflater = LayoutInflater.from(context);
        if (listPackage != null)
            listDatas = listPackage;
        else
            listDatas = new ArrayList<>();
    }

    public void updateListPackage(ArrayList<SBTNPackage> listPackage) {
        if (listPackage != null)
            listDatas = listPackage;
        else
            listDatas = new ArrayList<>();

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return listDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.dialog_buy_package, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.bindingData((SBTNPackage) getItem(position));

        return convertView;
    }

    private class MyViewHolder {
        TextView name, price, description, duration;

        public MyViewHolder(View item) {
            price = (TextView) item.findViewById(R.id.price);
            name = (TextView) item.findViewById(R.id.name);
            description = (TextView) item.findViewById(R.id.description);
            duration = (TextView) item.findViewById(R.id.duration);
        }

        public void bindingData(SBTNPackage data) {
            price.setText(data.getPrice());
            if (StringUtils.isNotEmpty(data.getName())) {
                name.setVisibility(View.VISIBLE);
                name.setText("Name: " + data.getName());
            } else {
                name.setVisibility(View.GONE);
            }

            if (StringUtils.isNotEmpty(data.getDescription())) {
                description.setVisibility(View.VISIBLE);
                description.setText("Description: " + data.getDescription());
            } else {
                description.setVisibility(View.GONE);
            }

            if (StringUtils.isNotEmpty(data.getDuration())) {
                duration.setVisibility(View.VISIBLE);
                duration.setText("Duration: " + data.getDuration());
            } else {
                duration.setVisibility(View.GONE);
            }

            price.getParent().requestLayout();
        }
    }

}
