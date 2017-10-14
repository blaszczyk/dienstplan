package muettinghoven.dienstplan.app.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.List;
import java.util.Map;

import muettinghoven.dienstplan.app.dto.Zeiteinheit;
import muettinghoven.dienstplan.app.model.DienstAusfuehrung;
import muettinghoven.dienstplan.app.model.DienstContainer;
import muettinghoven.dienstplan.app.model.Dienstplan;
import muettinghoven.dienstplan.app.service.DataCache;
import muettinghoven.dienstplan.app.service.DataProvider;
import muettinghoven.dienstplan.app.service.ServiceException;
import muettinghoven.dienstplan.app.tools.DienstTools;
import muettinghoven.dienstplan.app.view.ContainerAdapter;
import muettinghoven.dienstplan.app.view.ContainerView;
import muettinghoven.dienstplan.app.view.DienstAdapter;
import muettinghoven.dienstplan.app.view.DienstDetailActivity;
import muettinghoven.dienstplan.app.view.MainActivity;
import muettinghoven.dienstplan.app.view.R;

public class DienstplanViewController {

    private static final float SWIPE_SENSITIVITY = 50;

    private final MainActivity mainActivity;

    private final DataProvider dataProvider;

    private final DataCache dataCache;

    private ViewFlipper flipper;

    private Dienstplan plan;

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
            plan = dataProvider.getPlan(planId);
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

    public boolean flipView(final float direction)
    {
        if(flipper == null || Math.abs(direction) < SWIPE_SENSITIVITY)
            return false;
        if(direction < 0) {
            flipper.setInAnimation(mainActivity, R.anim.in_from_right);
            flipper.setOutAnimation(mainActivity,R.anim.out_to_left);
            flipper.showPrevious();
        }
        else {
            flipper.setInAnimation(mainActivity, R.anim.in_from_left);
            flipper.setOutAnimation(mainActivity,R.anim.out_to_right);
            flipper.showNext();
        }
        showContainerList();
        return true;
    }

    private View containerView(final String title, final List<DienstContainer> containers) {
        final LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ContainerView containerView = (ContainerView) inflater.inflate(R.layout.container_view,null);
        containerView.setContainers(containers);
        final TextView containerTypeTextView = (TextView) containerView.findViewById(R.id.containerTypeTextView);
        containerTypeTextView.setText(title);
        containerTypeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showContainerList();
            }
        });
        showContainerList(containerView);
        return containerView;
    }

    private void showContainerList(final ContainerView containerView) {
        final ListView listView = new ListViewCompat(mainActivity);
        final ContainerAdapter adapter = new ContainerAdapter(mainActivity, containerView.getContainers());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final DienstContainer container = (DienstContainer) adapter.getItem(position);
                showSingleContainer(container);
            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mainActivity.flipView(event);
            }
        });
        containerView.addView(listView);

        final int aktueller = DienstTools.aktueller(containerView.getContainers());
        listView.setSelection(aktueller);
    }

    private void showContainerList() {
        showContainerList(getContainerView());
    }

    private void showSingleContainer(final DienstContainer container) {
        final String title = container.getName();

        final LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout singleView = (LinearLayout) inflater.inflate(R.layout.single_view,null);
        final TextView containerTypeTextView = (TextView) singleView.findViewById(R.id.containerNameTextView);
        containerTypeTextView.setText(title);
        containerTypeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<DienstContainer> containers = plan.getContainingList(container);
                showContainerList();
            }
        });

        final ListView listView = (ListView) singleView.findViewById(R.id.dienstAusfuehrungListView);
        final List<DienstAusfuehrung> dienste = container.getAusfuehrungen();
        final DienstAdapter adapter = new DienstAdapter(mainActivity, dienste);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startDienstAusfuehrungActivity((DienstAusfuehrung) adapter.getItem(position));
            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mainActivity.flipView(event);
            }
        });

        final ContainerView containerView = getContainerView();
        containerView.addView(singleView);

        final int ersterAktueller = DienstTools.findErsterAktueller(dienste);
        listView.setSelection(ersterAktueller);
    }

    private ContainerView getContainerView() {
        return (ContainerView) flipper.getCurrentView().findViewById(R.id.containerView);
    }

    public void showMeineDiensteView() {
        final LinearLayout container = (LinearLayout) mainActivity.findViewById(R.id.main_content_wrapper);
        final LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View meineDiensteView = inflater.inflate(R.layout.meine_dienste_view,null,false);
        flipper = null;
        plan = null;
        try
        {
            final DienstContainer bewohner = dataProvider.getBewohner(bewohnerId);

            final TextView bewohnerNameTextView = (TextView) meineDiensteView.findViewById(R.id.bewohnerNameTextView);
            bewohnerNameTextView.setText(bewohner.getName());

            final ListView planListView = (ListView) meineDiensteView.findViewById(R.id.dienstAusfuehrungListView);
            final DienstAdapter adapter = new DienstAdapter(mainActivity);
            planListView.setAdapter(adapter);
            planListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startDienstAusfuehrungActivity((DienstAusfuehrung) adapter.getItem(position));
                }
            });
            final List<DienstAusfuehrung> dienste = bewohner.getAusfuehrungen();
            adapter.setDienste(dienste);

            final int ersterAktueller = DienstTools.findErsterAktueller(dienste);
            planListView.setSelection(ersterAktueller);
        }
        catch (ServiceException e)
        {
            e.printStackTrace();
        }
        if(container.getChildCount() > 1)
            container.removeViewAt(1);
        container.addView(meineDiensteView);
    }

    public boolean onBackPressed() {
        if(getContainerView().showsSingleContainer())
        {
            showContainerList();
            return true;
        }
        return false;
    }

    private void startDienstAusfuehrungActivity(final DienstAusfuehrung ausfuehrung) {
        final Intent dienstDetailView = new Intent(mainActivity,DienstDetailActivity.class);
        dienstDetailView.putExtra(DienstDetailActivity.DIENST_AUSFUEHRUNG,ausfuehrung);
        mainActivity.startActivity(dienstDetailView);
    }

    public void openWebUi(){
        final String url = baseURL + "/web";
        final Intent gotoWeb = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if(gotoWeb.resolveActivity(mainActivity.getPackageManager()) != null){
            mainActivity.startActivity(gotoWeb);
        }
    }
}
