package muettinghoven.dienstplan.app.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.List;
import java.util.Map;

import muettinghoven.dienstplan.R;
import muettinghoven.dienstplan.app.dto.Zeiteinheit;
import muettinghoven.dienstplan.app.model.DienstAusfuehrung;
import muettinghoven.dienstplan.app.model.DienstContainer;
import muettinghoven.dienstplan.app.model.Dienstplan;
import muettinghoven.dienstplan.app.service.DataCache;
import muettinghoven.dienstplan.app.service.DataProvider;
import muettinghoven.dienstplan.app.service.ServiceException;
import muettinghoven.dienstplan.app.view.ContainerAdapter;
import muettinghoven.dienstplan.app.view.DienstAdapter;
import muettinghoven.dienstplan.app.view.DienstDetailActivity;
import muettinghoven.dienstplan.app.view.MainActivity;

public class DienstplanViewController {

    private static final float SWIPE_SENSITIVITY = 50;

    private final MainActivity mainActivity;

    private final DataProvider dataProvider;

    private final DataCache dataCache;

    private ViewFlipper flipper;

    //TODO: load from preferences
    private int bewohnerId = 1;
    private String baseURL = "http://192.168.1.223:4053";

    public DienstplanViewController(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        dataCache = new DataCache(baseURL, mainActivity.getFilesDir());
        dataProvider = new DataProvider(dataCache);
    }

    public void initializeAsync(){
        final Thread thread = new Thread(){
            @Override
            public void run() {
                initialize();
            }
        };
        thread.start();
        try {
            thread.join();
        }
        catch (InterruptedException e)
        {

        }
    }

    private void initialize(){
        try{
            if(dataCache.isConnected()) {
                dataCache.loadDataForBewohner(bewohnerId);
                dataCache.saveToFiles();
            }
            else
            {
                dataCache.loadFromFiles();
            }
            final NavigationView navigationView = (NavigationView) mainActivity.findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(mainActivity);
            final Map<Integer,String> plaene = dataProvider.getDienstplaene(bewohnerId);
            for(final Map.Entry<Integer,String> e : plaene.entrySet())
            {
                navigationView.getMenu().add(0, Menu.FIRST + e.getKey(), Menu.NONE,e.getValue()).setIcon(R.drawable.ic_menu_send);
            }
            showMeineDiensteView();
        }
        catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    public void showDienstPlanView(int planId) {
        final LinearLayout container = (LinearLayout) mainActivity.findViewById(R.id.main_content_wrapper);
        final LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dienstPlanView = inflater.inflate(R.layout.dienstplan_view,null);
        try {
            final Dienstplan plan = dataProvider.getPlan(planId);
            final TextView dienstplanNameTextView = (TextView) dienstPlanView.findViewById(R.id.dienstplanNameTextView);
            dienstplanNameTextView.setText(plan.getName());

            flipper = (ViewFlipper) dienstPlanView.findViewById(R.id.dienstCategoryViewFlipper);


            flipper.addView(containerView("Dienste",plan.getDienste()));
            for(final Zeiteinheit e : Zeiteinheit.values())
            {
                final List<DienstContainer> zeitraeume = plan.getZeitraeume(e);
                if(!zeitraeume.isEmpty())
                    flipper.addView(containerView(e.name(),zeitraeume));
            }
        }
        catch (ServiceException e) {
            e.printStackTrace();
        }
        if(container.getChildCount() > 1)
            container.removeViewAt(1);
        container.addView(dienstPlanView);
    }

    public void flipView(final float direction)
    {
        if(flipper == null || Math.abs(direction) < SWIPE_SENSITIVITY)
            return;
        if(direction < 0)
            flipper.showPrevious();
        else
            flipper.showNext();
    }

    private View containerView(final String title, final List<DienstContainer> containers) {
        final LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout containerView = (LinearLayout) inflater.inflate(R.layout.dienste_container,null);
        final TextView containerTypeTextView = (TextView) containerView.findViewById(R.id.containerTypeTextView);
        containerTypeTextView.setText(title);
        final ListView listView = new ListViewCompat(mainActivity);
        final ContainerAdapter adapter = new ContainerAdapter(mainActivity, containers);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final DienstContainer container = (DienstContainer) adapter.getItem(position);
                final List<DienstAusfuehrung> dienste = container.getAusfuehrungen();
                final String title = container.getName();
                containerView.removeView(listView);

                final ListView listView = new ListViewCompat(mainActivity);
                final DienstAdapter adapter = new DienstAdapter(mainActivity, dienste);
                listView.setAdapter(adapter);
                containerView.addView(listView);
            }
        });
        containerView.addView(listView);
        return containerView;
    }

    public void showMeineDiensteView() {
        final LinearLayout container = (LinearLayout) mainActivity.findViewById(R.id.main_content_wrapper);
        final LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View meineDiensteView = inflater.inflate(R.layout.meine_dienste_view,null,false);
        flipper = null;
        try
        {
            final DienstContainer bewohner = dataProvider.getBewohner(bewohnerId);

            final TextView bewohnerNameTextView = (TextView) meineDiensteView.findViewById(R.id.bewohnerNameTextView);
            bewohnerNameTextView.setText(bewohner.getName());

            final ListView planListView = (ListView) meineDiensteView.findViewById(R.id.dienstausfuehrungListView);
            final DienstAdapter adapter = new DienstAdapter(mainActivity);
            planListView.setAdapter(adapter);
            planListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final DienstAusfuehrung dienst = (DienstAusfuehrung) adapter.getItem(position);
                    final Intent dienstDetailView = new Intent(mainActivity,DienstDetailActivity.class);
                    dienstDetailView.putExtra(DienstDetailActivity.DIENST_AUSFUEHRUNG,dienst);
                    mainActivity.startActivity(dienstDetailView);
                }
            });
            final List<DienstAusfuehrung> dienste = bewohner.getAusfuehrungen();
            adapter.setDienste(dienste);
        }
        catch (ServiceException e)
        {
            e.printStackTrace();
        }
        if(container.getChildCount() > 1)
            container.removeViewAt(1);
        container.addView(meineDiensteView);
    }

    public void openWebUi(){
        final String url = baseURL + "/web";
        final Intent gotoWeb = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if(gotoWeb.resolveActivity(mainActivity.getPackageManager()) != null){
            mainActivity.startActivity(gotoWeb);
        }
    }
}
