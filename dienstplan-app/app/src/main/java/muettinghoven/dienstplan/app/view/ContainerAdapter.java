package muettinghoven.dienstplan.app.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import muettinghoven.dienstplan.app.model.DienstAusfuehrung;
import muettinghoven.dienstplan.app.model.DienstContainer;


public class ContainerAdapter extends BaseAdapter {


    private List<DienstContainer> containers;

    private final Context context;

    public ContainerAdapter(final Context context, final List<DienstContainer> containers)
    {
        this.context = context;
        this.containers = containers;
    }

    @Override
    public int getCount() {
        return containers.size();
    }

    @Override
    public Object getItem(int position) {
        return containers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return containers.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TextView view = new TextView(context);
        view.setText(containers.get(position).getName());
        view.setTextSize(26);
        view.setTextColor(Color.BLACK);
        view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return view;
    }
}
