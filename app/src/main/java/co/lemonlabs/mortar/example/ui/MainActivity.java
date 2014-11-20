package co.lemonlabs.mortar.example.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.ButterKnife;
import co.lemonlabs.mortar.example.R;
import co.lemonlabs.mortar.example.U2020App;
import co.lemonlabs.mortar.example.core.CorePresenter;
import co.lemonlabs.mortar.example.core.CoreView;
import co.lemonlabs.mortar.example.core.android.ActionBarPresenter;
import co.lemonlabs.mortar.example.core.android.ActivityResultPresenter;
import co.lemonlabs.mortar.example.core.android.DrawerPresenter;
import flow.Flow;
import mortar.Mortar;
import mortar.MortarActivityScope;
import mortar.MortarScope;
import timber.log.Timber;

import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.CATEGORY_LAUNCHER;
import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;

public class MainActivity extends Activity implements
        ActionBarPresenter.View, DrawerPresenter.View, ActivityResultPresenter.View {

    @Inject
    ActionBarPresenter      actionBarPresenter;
    @Inject
    DrawerPresenter         drawerPresenter;
    @Inject
    ActivityResultPresenter activityResultPresenter;
    @Inject
    AppContainer            appContainer;

    private ActionBarPresenter.MenuAction actionBarMenuAction;
    private MenuItem                      menuItem;
    private MortarActivityScope           activityScope;
    private CoreView                      coreView;
    private Flow                          flow;
    private ActionBarDrawerToggle         drawerToggle;

    private String  scopeName;
    private boolean configurationChangeIncoming;

    @Override
    public MortarScope getMortarScope() {
        return activityScope;
    }

    @Override
    public void setShowHomeEnabled(boolean enabled) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(enabled);
        }
    }

    @Override
    public void setUpButtonEnabled(boolean enabled) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(enabled);
            actionBar.setHomeButtonEnabled(enabled);
        }
    }

    @Override
    public void setMenu(ActionBarPresenter.MenuAction action) {
        if (action != actionBarMenuAction) {
            actionBarMenuAction = action;
            invalidateOptionsMenu();
        }
    }

    @Override
    public void setDrawerIndicatorEnabled(boolean enabled) {
        drawerToggle.setDrawerIndicatorEnabled(enabled);
    }

    @Override
    public void setDrawerLockMode(int lockMode) {
        coreView.setDrawerLockMode(lockMode);
    }

    @Override
    public void startActivityForResult(int requestCode, Intent intent) {
        if (canHandleIntent(intent)) {
            startActivityForResult(intent, requestCode);
        } else {
            Timber.e("Could not handle intent %s... ignoring", intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isWrongInstance()) {
            finish();
            return;
        }

        MortarScope parentScope = Mortar.getScope(getApplication());
        activityScope = Mortar.requireActivityScope(parentScope, new CorePresenter(getScopeName()));
        activityScope.onCreate(savedInstanceState);

        Mortar.inject(this, this);

        actionBarPresenter.takeView(this);

        ViewGroup container = appContainer.get(this, U2020App.get(this));

        getLayoutInflater().inflate(R.layout.core, container);
        coreView = ButterKnife.findById(this, R.id.drawer_layout);

        flow = coreView.getFlow();

        drawerToggle = coreView.getDrawerToggle();
        drawerPresenter.takeView(this);
        activityResultPresenter.takeView(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        activityScope.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        actionBarPresenter.dropView(this);
        drawerPresenter.dropView(this);
        activityResultPresenter.dropView(this);
        if (!configurationChangeIncoming) {
            if (!activityScope.isDestroyed()) {
                MortarScope parentScope = Mortar.getScope(getApplication());
                parentScope.destroyChild(activityScope);
            }
            activityScope = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        configurationChangeIncoming = true;
        return activityScope.getName();
    }

    @Override
    public void onBackPressed() {
        if (flow.goBack()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (actionBarMenuAction != null) {
            menuItem = menu.add(actionBarMenuAction.title)
                           .setShowAsActionFlags(SHOW_AS_ACTION_ALWAYS)
                           .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                               @Override
                               public boolean onMenuItemClick(MenuItem menuItem) {
                                   actionBarMenuAction.action.call();
                                   return true;
                               }
                           });
        } else if (menu.hasVisibleItems() && menuItem != null) {
            menu.removeItem(menuItem.getItemId());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            return flow.goBack();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void startActivity(Intent intent) {
        if (canHandleIntent(intent)) {
            startActivity(intent);
        } else {
            Timber.e("Could not handle intent %s... ignoring", intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        activityResultPresenter.onActivityResultReceived(requestCode, resultCode, data);
    }

    @Override
    public Object getSystemService(String name) {
        if (Mortar.isScopeSystemService(name)) {
            return activityScope;
        }
        return super.getSystemService(name);
    }

    private boolean canHandleIntent(Intent intent) {
        PackageManager manager = getPackageManager();
        List<ResolveInfo> info = manager.queryIntentActivities(intent, 0);
        return info.size() > 0;
    }

    private String getScopeName() {
        if (scopeName == null) scopeName = (String) getLastNonConfigurationInstance();
        if (scopeName == null) {
            scopeName = getClass().getName() + "-" + UUID.randomUUID().toString();
        }
        return scopeName;
    }

    private boolean isWrongInstance() {
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            boolean isMainAction = intent.getAction() != null && intent.getAction().equals(ACTION_MAIN);
            return intent.hasCategory(CATEGORY_LAUNCHER) && isMainAction;
        }
        return false;
    }
}
