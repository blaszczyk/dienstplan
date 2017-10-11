package muettinghoven.dienstplan;

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


public class ContainerAdapter extends BaseAdapter {

    public enum Type {
        DIENST,
        ZEITRAUM;
    }

    private List<Map.Entry<Integer,String>> items;
    private Type type;

    private final Context context;

    public ContainerAdapter(final Context context, final Map<Integer,String> items, final Type type)
    {
        this.context = context;
        this.type = type;
        this.items = new ArrayList<>();
        for(final Map.Entry<Integer,String> e : items.entrySet())
            this.items.add(e);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position).getValue();
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getKey();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TextView view = new TextView(context);
        view.setText(items.get(position).getValue());
        view.setTextSize(26);
        view.setTextColor(Color.BLACK);
        view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return view;
    }
}
