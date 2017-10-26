package muettinghoven.dienstplan.app.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import muettinghoven.dienstplan.app.model.DienstAusfuehrung;
import muettinghoven.dienstplan.app.view.R;


public class DienstAdapter extends BaseAdapter {

    private List<DienstAusfuehrung> dienste;

    private final Context context;

    public DienstAdapter(final Context context, final List<DienstAusfuehrung> dienste)
    {
        this.context = context;
        this.dienste = dienste;
    }

    public DienstAdapter(final Context context)
    {
        this(context, Collections.<DienstAusfuehrung>emptyList());
    }

    public void setDienste(final List<DienstAusfuehrung> dienste)
    {
        this.dienste = dienste;
    }
    @Override
    public int getCount() {
        return dienste.size();
    }

    @Override
    public Object getItem(int position) {
        return dienste.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dienste.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dienst_item_view,null);
        final TextView dienstNameTextView = (TextView) view.findViewById(R.id.dienstNameTextView);
        final TextView bewohnerTextView = (TextView) view.findViewById(R.id.bewohnerNameTextView);
        final TextView zeitraumTextView = (TextView) view.findViewById(R.id.zeitraumTextView);

        final DienstAusfuehrung dienst = dienste.get(position);
        dienstNameTextView.setText(dienst.getDienst());
        bewohnerTextView.setText(dienst.getBewohner());
        zeitraumTextView.setText(dienst.getZeitraum());
        if(dienst.isAktuell())
        {
            final int color = context.getResources().getColor(R.color.aktuell);
            dienstNameTextView.setTextColor(color);
            bewohnerTextView.setTextColor(color);
            zeitraumTextView.setTextColor(color);
        }

        return view;
    }
}
