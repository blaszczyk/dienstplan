package muettinghoven.dienstplan.app.view;

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
import android.view.MotionEvent;
import android.view.View;

import muettinghoven.dienstplan.app.controller.DienstplanViewController;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnTouchListener
{

    private boolean visible = true;

    private DienstplanViewController controller;

    private Menu mainMenu;

    private float initialX;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controller = new DienstplanViewController(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final CustomDrawer drawer = (CustomDrawer) findViewById(R.id.drawer_layout);
        drawer.addOnTouchListener(this);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        controller.initializeAsync();
    }

    public void setConnectionStatusIcon(final int iconRes) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mainMenu == null)
                    return;
                final MenuItem refreshMenuItem = mainMenu.findItem(R.id.action_refresh);
                refreshMenuItem.setIcon(iconRes);
                refreshMenuItem.getIcon();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        visible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        visible = true;
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        this.mainMenu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(!controller.onBackPressed()) {
            super.onBackPressed();
        }
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
            case R.id.action_refresh:
                controller.refresh();
            case R.id.action_settings:
                //TODO: open settings activity
                return true;
            case R.id.action_web:
                controller.openWebUi();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.nav_meine_dienste) {
            controller.showMeineDiensteView();
        }
        else {
            final int planId = id - Menu.FIRST;
            controller.showDienstPlanView(planId);
        }

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            return false;
        switch (event.getAction())
        {
            case  MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                return true;
            case MotionEvent.ACTION_UP:
                final float direction = event.getX() - initialX;
                return controller.flipView(direction);
        }
        return false;
    }
}
