package muettinghoven.dienstplan.app.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import muettinghoven.dienstplan.app.model.DienstContainer;

public class ContainerView extends LinearLayout {

    private List<DienstContainer> containers;

    private View currentView = null;

    public ContainerView(Context context) {
        super(context);
    }

    public ContainerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContainerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ContainerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public List<DienstContainer> getContainers() {
        return containers;
    }

    public void setContainers(List<DienstContainer> containers) {
        this.containers = containers;
    }

    public boolean showsSingleContainer()  {
        return currentView instanceof LinearLayout;
    }


    @Override
    public void addView(View child) {
        if(currentView != null)
            removeView(currentView);
        currentView = child;
        super.addView(child);
    }
}
