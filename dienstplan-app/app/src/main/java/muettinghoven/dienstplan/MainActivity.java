package muettinghoven.dienstplan;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.List;
import java.util.Map;

import muettinghoven.dienstplan.app.CustomDrawer;
import muettinghoven.dienstplan.app.dto.BewohnerDto;
import muettinghoven.dienstplan.app.dto.DienstplanDto;
import muettinghoven.dienstplan.app.dto.Zeiteinheit;
import muettinghoven.dienstplan.app.model.DienstAusfuehrung;
import muettinghoven.dienstplan.app.service.DataCache;
import muettinghoven.dienstplan.app.service.DataProvider;
import muettinghoven.dienstplan.app.service.ServiceException;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

    private DataCache dataCache;
    private DataProvider dataProvider;

    private float initialX;
    private ViewFlipper flipper;

    //TODO: load from preferences
    private int bewohnerId = 1;
    private String baseURL = "http://192.168.1.223:4053";

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataCache = new DataCache(baseURL, getFilesDir());
        dataProvider = new DataProvider(dataCache);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final CustomDrawer drawer = (CustomDrawer) findViewById(R.id.drawer_layout);
        drawer.addOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return flipView(event);
            }
        });


        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        initializeAsync();
    }

    private void initializeAsync(){
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
            final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            final Map<Integer,String> plaene = dataProvider.getDienstplaene(bewohnerId);
            for(final Map.Entry<Integer,String> e : plaene.entrySet())
            {
                navigationView.getMenu().add(0,Menu.FIRST + e.getKey(), Menu.NONE,e.getValue()).setIcon(R.drawable.ic_menu_send);
            }
            showMeineDiensteView();
        }
        catch (ServiceException e) {
            e.printStackTrace();
        }

    }


    private void openWebUi(){
        final String url = baseURL + "/web";
        final Intent gotoWeb = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if(gotoWeb.resolveActivity(getPackageManager()) != null){
            startActivity(gotoWeb);
        }

    }


    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();
        switch (id)
        {
            case R.id.action_settings:
                //TODO: open settings activity
                return true;
            case R.id.action_web:
                openWebUi();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean flipView(MotionEvent event) {
        if(flipper == null)
            return super.onTouchEvent(event);
        switch (event.getAction())
        {
            case  MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                final float direction = event.getX() - initialX;
                if(direction < 0)
                    flipper.showPrevious();
                else
                    flipper.showNext();
                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.nav_meine_dienste) {
            showMeineDiensteView();
        }
        else {
            final int planId = id - Menu.FIRST;
            showDienstPlanView(planId);
        }

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showDienstPlanView(int planId) {
        final LinearLayout container = (LinearLayout) findViewById(R.id.main_content_wrapper);
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dienstPlanView = inflater.inflate(R.layout.dienstplan_view,null);
        try {
            final DienstplanDto plan = dataCache.getDienstplan(planId);
            final TextView dienstplanNameTextView = (TextView) dienstPlanView.findViewById(R.id.dienstplanNameTextView);
            dienstplanNameTextView.setText(plan.getName());

            flipper = (ViewFlipper) dienstPlanView.findViewById(R.id.dienstCategoryViewFlipper);


            flipper.addView(containerView("Dienste",dataProvider.getDienstNamen(planId), ContainerAdapter.Type.DIENST));
            for(final Zeiteinheit e : Zeiteinheit.values())
            {
                final Map<Integer,String> names = dataProvider.getZeitraeume(planId,e);
                if(!names.isEmpty())
                    flipper.addView(containerView(e.name(),names, ContainerAdapter.Type.ZEITRAUM));
            }
            flipper.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    System.out.print(flipper.getChildCount());
                }
            });
        }
        catch (ServiceException e) {
            e.printStackTrace();
        }
        if(container.getChildCount() > 1)
            container.removeViewAt(1);
        container.addView(dienstPlanView);
    }

    private View containerView(final String title, final Map<Integer,String> names, final ContainerAdapter.Type type) {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout containerView = (LinearLayout) inflater.inflate(R.layout.dienste_container,null);
        final TextView containerTypeTextView = (TextView) containerView.findViewById(R.id.containerTypeTextView);
        containerTypeTextView.setText(title);
        final ListView listView = new ListViewCompat(getApplicationContext());
        final ContainerAdapter adapter = new ContainerAdapter(getApplicationContext(), names, type);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    final List<DienstAusfuehrung> dienste = dataProvider.getAusfuehrungenFor(type, (int)id);
                    final String title = adapter.getItem(position).toString();
                    containerView.removeView(listView);

                    final ListView listView = new ListViewCompat(getApplicationContext());
                    final DienstAdapter adapter = new DienstAdapter(MainActivity.this, dienste);
                    listView.setAdapter(adapter);
                    containerView.addView(listView);
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
        });
        containerView.addView(listView);
        return containerView;
    }


    private void showMeineDiensteView() {
        final LinearLayout container = (LinearLayout) findViewById(R.id.main_content_wrapper);
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View meineDiensteView = inflater.inflate(R.layout.meine_dienste_view,null,false);
        flipper = null;
        try
        {
            final BewohnerDto bewohner = dataCache.getBewohner(bewohnerId);

            final TextView bewohnerNameTextView = (TextView) meineDiensteView.findViewById(R.id.bewohnerNameTextView);
            bewohnerNameTextView.setText(bewohner.getName());

            final ListView planListView = (ListView) meineDiensteView.findViewById(R.id.dienstausfuehrungListView);
            final DienstAdapter adapter = new DienstAdapter(MainActivity.this);
            planListView.setAdapter(adapter);
            planListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final DienstAusfuehrung dienst = (DienstAusfuehrung) adapter.getItem(position);
                    final Intent dienstDetailView = new Intent(getApplicationContext(),DienstDetailActivity.class);
                    dienstDetailView.putExtra(DienstDetailActivity.DIENST_AUSFUEHRUNG,dienst);
                    startActivity(dienstDetailView);
                }
            });
            final List<DienstAusfuehrung> dienste = dataProvider.forBewohner(bewohnerId);
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
}
