package muettinghoven.dienstplan.app.view.wrapper;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import muettinghoven.dienstplan.app.model.DienstContainer;

public class AbstractWrapper extends LinearLayout {

    private View currentView = null;

    public AbstractWrapper(Context context) {
        super(context);
    }

    public AbstractWrapper(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractWrapper(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AbstractWrapper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public View getView() {
        return currentView;
    }

    public void setView(final View view) {
        if(currentView != null)
            removeView(currentView);
        currentView = view;
        super.addView(view);
    }
}
