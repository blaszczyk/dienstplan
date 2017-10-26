package muettinghoven.dienstplan.app.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import muettinghoven.dienstplan.app.model.DienstAusfuehrung;
import muettinghoven.dienstplan.app.tools.Preferences;
import muettinghoven.dienstplan.app.view.R;


public class MeineDiensteAdapter extends BaseAdapter {

    private List<DienstAusfuehrung> dienste;

    private final Context context;

    public MeineDiensteAdapter(final Context context, final List<DienstAusfuehrung> dienste)
    {
        this.context = context;
        this.dienste = dienste;
    }

    public MeineDiensteAdapter(final Context context)
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
        final FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.bewohner_dienst_item_view,null);
        final TextView dienstNameTextView = (TextView) frameLayout.findViewById(R.id.dienstNameTextView);
        final TextView zeitraumTextView = (TextView) frameLayout.findViewById(R.id.zeitraumTextView);

        final DienstAusfuehrung dienst = dienste.get(position);
        dienstNameTextView.setText(dienst.getDienst());
        zeitraumTextView.setText(dienst.getZeitraum());
        if(dienst.isAktuell())
        {
            final int color = context.getResources().getColor(R.color.aktuell);
            dienstNameTextView.setTextColor(color);
            zeitraumTextView.setTextColor(color);
        }

        final Preferences prefs = new Preferences(context);
        prefs.loadProperties();
        final Button erinnerungButton = (Button) frameLayout.findViewById(R.id.erinnerungButton);
        if(dienst.isAktuell()) {
            setHideErinnerung(erinnerungButton, prefs.isHideErinnerung(dienst.getId()));
            erinnerungButton.setOnClickListener(new ErinnerungToggle(dienst.getId()));
        }
        else {
            erinnerungButton.setBackgroundResource(R.drawable.reminder_off);
        }

        return frameLayout;
    }

    private class ErinnerungToggle implements View.OnClickListener {

        final int ausfuehrungId;

        public ErinnerungToggle(final int ausfuehrungId) {
            this.ausfuehrungId = ausfuehrungId;
        }

        @Override
        public void onClick(View v) {
            final boolean hide = toggleErinnerung(ausfuehrungId);
            setHideErinnerung(v, hide);
        }
    }

    private void setHideErinnerung(final View v, final boolean hide) {
        if(hide)
            v.setBackgroundResource(R.drawable.reminder_cross);
        else
            v.setBackgroundResource(R.drawable.reminder);
    }

    private boolean toggleErinnerung(final int ausfuhrungId) {
        final Preferences preferences = new Preferences(context);
        preferences.loadProperties();
        preferences.toggleErinnerung(ausfuhrungId);
        preferences.saveProperties();
        return preferences.isHideErinnerung(ausfuhrungId);
    }
}
