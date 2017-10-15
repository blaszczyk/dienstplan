package muettinghoven.dienstplan.app.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

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
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout containerItemView = (LinearLayout) inflater.inflate(R.layout.container_item_view,null);
        final TextView containerNameTextView = (TextView) containerItemView.findViewById(R.id.containerNameTextView);
        final DienstContainer container = containers.get(position);
        containerNameTextView.setText(container.getName());
        if(container.isAktuell() && container.getTyp() == DienstContainer.Typ.ZEITRAUM)
            containerNameTextView.setTextColor(context.getResources().getColor(R.color.aktuell));
        return containerItemView;
    }
}
