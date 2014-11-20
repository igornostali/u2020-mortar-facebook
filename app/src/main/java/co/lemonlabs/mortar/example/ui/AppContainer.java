package co.lemonlabs.mortar.example.ui;

import android.app.Activity;
import android.view.ViewGroup;

import co.lemonlabs.mortar.example.U2020App;

import static butterknife.ButterKnife.findById;

/**
 * An indirection which allows controlling the root container used for each activity.
 */
public interface AppContainer {

    /**
     * An {@link AppContainer} which returns the normal activity content view.
     */
    AppContainer DEFAULT = new AppContainer() {
        @Override
        public ViewGroup get(Activity activity, U2020App app) {
            return findById(activity, android.R.id.content);
        }
    };

    /**
     * The root {@link android.view.ViewGroup} into which the activity should place its contents.
     */
    ViewGroup get(Activity activity, U2020App app);
}
