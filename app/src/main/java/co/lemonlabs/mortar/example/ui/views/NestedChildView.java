package co.lemonlabs.mortar.example.ui.views;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;

import butterknife.ButterKnife;
import co.lemonlabs.mortar.example.ui.screens.NestedScreen;
import mortar.Mortar;

public class NestedChildView extends FrameLayout {

    private NestedScreen.ChildPresenter presenter;

    private ObjectAnimator animator;

    public NestedChildView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Mortar.inject(context, this);
    }

    public void setPresenter(NestedScreen.ChildPresenter presenter) {
        this.presenter = presenter;
        presenter.takeView(this);
    }

    public void toggleAnimation() {
        if (animator != null) {
            if (animator.isRunning()) {
                animator.end();
            } else {
                animator.start();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        createAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        animator.end();
        animator = null;
        presenter.dropView(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    private void createAnimation() {
        animator = ObjectAnimator.ofFloat(this, "rotation", 360);
        animator.setDuration(3600);
        animator.setInterpolator(new BounceInterpolator());
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
    }
}

