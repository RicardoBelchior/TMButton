package com.rbelchior.tmbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * A custom image view with two states: checked and unchecked. Each state is represented with two
 * colors, unchecked and checked color.
 * When the view is clicked, the state changes automatically with a scale and color animation.
 *
 * <p><strong>Note:</strong> Make sure to call <code>android:clipChildren="false"</code> on the parent
 * layout, otherwise the scaling animation will not occur.</p>
 *
 * <p><strong>XML attributes</strong></p>
 *
 * <p><code>icon_drawable</code>: icon drawable</p>
 * <p><code>color_unchecked</code>: color of the unchecked state, this is the default value</p>
 * <p><code>color_checked</code>: color of the checked state</p>
 */
public class TMButton extends FrameLayout implements Checkable {

    private static final DecelerateInterpolator INTERPOLATOR_DECELERATE = new DecelerateInterpolator(2.0f);
    private static final float SCALE_FACTOR = 2.5f;
    private static final int DURATION_COLOR = 300;
    private static final int DURATION_SHADOW_ANIM = 500;


    private ImageView iconView;
    private ImageView shadowIconView;

    private ObjectAnimator colorAnimator;

    private boolean isChecked;
    private boolean broadcasting;

    private int colorUnchecked;
    private int colorChecked;

    private OnCheckedChangeListener onCheckedChangeListener;

    public TMButton(@NonNull Context context) {
        this(context, null, 0);
    }

    public TMButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TMButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        initViews(context, attrs, defStyleAttr);
        initAttrs(context, attrs);

        isChecked = false;

        iconView.setColorFilter(colorUnchecked);
        shadowIconView.setVisibility(View.GONE);

        colorAnimator = ObjectAnimator
                .ofObject(iconView, "colorFilter", new ArgbEvaluator(), 0, 0)
                .setDuration(DURATION_COLOR);
    }

    private void initViews(Context context, AttributeSet attrs, int defStyleAttr) {
        iconView = new ImageView(context, attrs, defStyleAttr);
        shadowIconView = new ImageView(context, attrs, defStyleAttr);

        addView(iconView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(shadowIconView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray attributes =
                context.obtainStyledAttributes(attrs, R.styleable.trinity_mirror_like_button);

        initColorDefault(attributes);
        initColorChecked(attributes);
        initIconDrawable(attributes);

        attributes.recycle();
    }

    private void initColorDefault(TypedArray attributes) {
        setColorUnchecked(
                attributes.getColor(
                        R.styleable.trinity_mirror_like_button_color_unchecked,
                        Color.LTGRAY));
    }

    private void initColorChecked(TypedArray attributes) {
        setColorChecked(
                attributes.getColor(
                        R.styleable.trinity_mirror_like_button_color_checked,
                        Color.MAGENTA));
    }

    private void initIconDrawable(TypedArray attributes) {
        if (!attributes.hasValue(R.styleable.trinity_mirror_like_button_icon_drawable)) {
            throw new IllegalArgumentException("Missing attribute: icon_drawable");
        }

        setIconDrawable(
                attributes.getDrawable(
                        R.styleable.trinity_mirror_like_button_icon_drawable));
    }

    @Override
    public boolean performClick() {
        toggle();

        final boolean handled = super.performClick();
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }

        return handled;
    }

    private void animateCheck() {
        shadowIconView.animate().cancel();
        shadowIconView.setVisibility(View.VISIBLE);

        shadowIconView.animate()
                .scaleX(SCALE_FACTOR)
                .scaleY(SCALE_FACTOR)
                .alpha(0.0f)
                .setDuration(DURATION_SHADOW_ANIM)
                .setListener(shadowAnimatorListener)
                .setInterpolator(INTERPOLATOR_DECELERATE);

        colorAnimator.cancel();
        colorAnimator.setObjectValues(colorUnchecked, colorChecked);
        colorAnimator.start();
    }

    private void animateUnCheck() {
        colorAnimator.cancel();
        colorAnimator.setObjectValues(colorChecked, colorUnchecked);
        colorAnimator.start();
    }


    private AnimatorListenerAdapter shadowAnimatorListener = new AnimatorListenerAdapter() {

        @Override
        public void onAnimationCancel(Animator animation) {
            resetShadowView();
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            resetShadowView();
        }
    };

    private void resetShadowView() {
        shadowIconView.setVisibility(View.GONE);
        shadowIconView.setScaleX(1.0f);
        shadowIconView.setScaleY(1.0f);
        shadowIconView.setAlpha(1.0f);
        shadowIconView.setColorFilter(colorChecked);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                iconView.animate()
                        .scaleX(0.7f)
                        .scaleY(0.7f)
                        .setDuration(150)
                        .setInterpolator(INTERPOLATOR_DECELERATE);
                setPressed(true);
                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                boolean isInside = (x > 0 && x < getWidth() && y > 0 && y < getHeight());
                if (isPressed() != isInside) {
                    setPressed(isInside);
                }
                break;

            case MotionEvent.ACTION_UP:
                iconView.animate()
                        .scaleX(1)
                        .scaleY(1)
                        .setInterpolator(INTERPOLATOR_DECELERATE);
                if (isPressed()) {
                    performClick();
                    setPressed(false);
                }
                break;
        }
        return true;
    }

    /**
     * Set the icon drawable
     */
    public void setIconDrawable(Drawable iconDrawable) {
        this.iconView.setImageDrawable(iconDrawable);
        this.shadowIconView.setImageDrawable(iconDrawable);
    }

    /**
     * Set the color for state unchecked
     */
    public void setColorUnchecked(@ColorInt int color) {
        this.colorUnchecked = color;
    }

    /**
     * Set the color for state checked
     */
    public void setColorChecked(@ColorInt int color) {
        this.colorChecked = color;
    }

    /**
     * Change the checked state of the view.
     * Calls {@link #setChecked(boolean, boolean)} with given state, but <code>animateChange=false</code>
     * @param checked new checked state
     */
    @Override
    public void setChecked(boolean checked) {
        setChecked(checked, false);
    }

    /**
     * Change the checked state of the view
     * @param checked new checked state
     * @param animateChange if true, animates the state change.
     */
    public void setChecked(boolean checked, boolean animateChange) {
        if (this.isChecked == checked) {
            return;
        }
        this.isChecked = checked;

        if (animateChange) {
            if (isChecked) {
                animateCheck();
            } else {
                animateUnCheck();
            }
        }

        // Avoid infinite recursions if setChecked() is called from a listener
        if (broadcasting) {
            return;
        }

        broadcasting = true;
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, isChecked);
        }
        broadcasting = false;
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    /**
     * Change the checked state of the view to the inverse of its current state.
     * The state change is animated.
     */
    @Override
    public void toggle() {
        setChecked(!isChecked, true);
    }

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a compound button changed.
     */
    public interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        void onCheckedChanged(TMButton buttonView, boolean isChecked);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

}
