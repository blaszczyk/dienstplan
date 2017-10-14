package muettinghoven.dienstplan.app.view.wrapper;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.List;

import muettinghoven.dienstplan.app.model.DienstContainer;

public class MainContentWrapper extends AbstractWrapper {

    private static final int BEWOHNER = 346236523;

    private int id = BEWOHNER;

    public MainContentWrapper(Context context) {
        super(context);
    }

    public MainContentWrapper(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MainContentWrapper(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MainContentWrapper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setPlan(int id) {
        this.id = id;
    }

    public void setBewohner() {
        id = BEWOHNER;
    }

    public boolean isBewohner() {
        return id == BEWOHNER;
    }

    public int getPlanId() {
        if(isBewohner())
            throw new IllegalStateException("is bewohner");
        return id;
    }
}
