package muettinghoven.dienstplan.app.view.wrapper;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import muettinghoven.dienstplan.app.model.DienstContainer;

public class ContainerWrapper extends AbstractWrapper {

    private List<DienstContainer> containers;

    public ContainerWrapper(Context context) {
        super(context);
    }

    public ContainerWrapper(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContainerWrapper(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ContainerWrapper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public List<DienstContainer> getContainers() {
        return containers;
    }

    public void setContainers(List<DienstContainer> containers) {
        this.containers = containers;
    }

    public boolean showsSingleContainer()  {
        return getView() instanceof LinearLayout;
    }
}
