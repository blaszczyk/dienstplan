package muettinghoven.dienstplan.app.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import muettinghoven.dienstplan.app.model.DienstAusfuehrung;


public class DienstAdapter extends BaseAdapter {

    private List<DienstAusfuehrung> dienste;

    private final LayoutInflater inflater;

    public DienstAdapter(final Context context, final List<DienstAusfuehrung> dienste)
    {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        final View view = inflater.inflate(R.layout.dienst_item_view,null);
        final TextView dienstNameTextView = (TextView) view.findViewById(R.id.dienstNameTextView);
        final TextView bewohnerTextView = (TextView) view.findViewById(R.id.bewohnerNameTextView);
        final TextView zeitraumTextView = (TextView) view.findViewById(R.id.zeitraumTextView);

        final DienstAusfuehrung dienst = dienste.get(position);
        dienstNameTextView.setText(dienst.getDienst());
        bewohnerTextView.setText(dienst.getBewohner());
        zeitraumTextView.setText(dienst.getZeitraum());

        return view;
    }
}
