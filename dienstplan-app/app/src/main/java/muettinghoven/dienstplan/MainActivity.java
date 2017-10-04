package muettinghoven.dienstplan;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;
import java.util.Map;

import muettinghoven.dienstplan.app.model.DienstAusfuehrung;
import muettinghoven.dienstplan.app.service.DienstplanProvider;
import muettinghoven.dienstplan.app.service.DataProvider;
import muettinghoven.dienstplan.app.service.ServiceException;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DataProvider dataProvider;
    private int bewohnerId = 1;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataProvider = new DataProvider(new DienstplanProvider());

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

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
        try {

            final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            final Map<Integer,String> plaene = dataProvider.getDienstplaene(bewohnerId);
            for(final Map.Entry<Integer,String> e : plaene.entrySet())
            {
                navigationView.getMenu().add(0,Menu.FIRST + e.getKey(), Menu.NONE,e.getValue()).setIcon(R.drawable.ic_menu_send);
            }

            final ListView planListView = (ListView) findViewById(R.id.planListView);
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
        catch (ServiceException e) {
            e.printStackTrace();
        }

    };


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
        menu.add(0,Menu.FIRST,Menu.NONE,"hallöchen1");
        menu.add(0,Menu.FIRST + 1,Menu.NONE,"hallöchen2");
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.action_settings) {
            //TODO: open settings activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.nav_meine_dienste) {
            // TODO: create view for meine dienste

        }
        else {
            final int planId = id - Menu.FIRST;
            findViewById(R.id.include);
            // TODO: create view for plan
        }

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
