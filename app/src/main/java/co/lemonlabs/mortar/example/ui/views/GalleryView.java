package co.lemonlabs.mortar.example.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import co.lemonlabs.mortar.example.R;
import co.lemonlabs.mortar.example.ui.gallery.GalleryAdapter;
import co.lemonlabs.mortar.example.ui.misc.BetterViewAnimator;
import co.lemonlabs.mortar.example.ui.screens.GalleryScreen;
import mortar.Mortar;

public class GalleryView extends BetterViewAnimator {
    @InjectView(R.id.gallery_grid) AbsListView galleryView;

    @Inject GalleryScreen.Presenter presenter;

    private GalleryAdapter adapter;

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Mortar.inject(context, this);
    }

    public GalleryAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(GalleryAdapter adapter) {
        galleryView.setAdapter(adapter);
    }

    public void updateChildId() {
        setDisplayedChildId(R.id.gallery_grid);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.dropView(this);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
        adapter = new GalleryAdapter(getContext(), presenter.getPicasso());
        setAdapter(adapter);
        presenter.takeView(this);
    }
}
