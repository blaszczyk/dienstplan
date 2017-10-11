package muettinghoven.dienstplan.app;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import muettinghoven.dienstplan.R;

public class CustomDrawer extends DrawerLayout {

    private final List<OnTouchListener> listeners = new ArrayList<>();

    public CustomDrawer(Context context) {
        super(context);
    }

    public CustomDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomDrawer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addOnTouchListener(final OnTouchListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean result = true;
        for(OnTouchListener listener : listeners)
            result &= listener.onTouch(this,ev);
        return result & super.onTouchEvent(ev);
    }
}
