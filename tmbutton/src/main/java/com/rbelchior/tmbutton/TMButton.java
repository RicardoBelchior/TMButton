package com.rbelchior.tmbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.widget.TextViewCompat;

/**
 * <p>
 * A custom image view with two states: checked and unchecked. Each state is represented with two
 * colors, unchecked and checked color.
 * When the view is clicked, the state changes automatically with a scale and color animation.
 * </p>
 * <p><strong>Note:</strong> Make sure to call <code>android:clipChildren="false"</code> on the parent
 * layout, otherwise the scaling animation will not occur.</p>
 * <p><strong>XML attributes:</strong></p>
 * <p><code>icon_drawable</code>: icon drawable</p>
 * <p><code>color_unchecked</code>: color of the unchecked state, this is the default value</p>
 * <p><code>color_checked</code>: color of the checked state</p>
 */
public class TMButton extends LinearLayout implements Checkable {

    private static final DecelerateInterpolator INTERPOLATOR_DECELERATE = new DecelerateInterpolator(2.0f);
    private static final float SCALE_FACTOR = 2.5f;
    private static final int DURATION_COLOR = 300;
    private static final int DURATION_SHADOW_ANIM = 500;


    private ImageView iconView;
    private ImageView shadowIconView;
    private TextSwitcher textSwitcher;

    private ViewPropertyAnimator shadowAnimator;
    private ObjectAnimator colorAnimator;

    private boolean isChecked;
    private boolean broadcasting;

    private int colorUnchecked;
    private int colorChecked;
    private String textChecked;
    private String textUnchecked;

    private Drawable checkedDrawable;

    /**
     * Optional drawable to be specifically used when unchecked.
     * Note the animations are not as smooth in this case. To be improved.
     */
    @Nullable
    private Drawable uncheckedDrawable;

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

        setClickable(true);

        isChecked = false;

        setIconViewUnchecked();
        setTextViewUnchecked();

        shadowIconView.setVisibility(View.GONE);
        shadowAnimator = shadowIconView.animate();
        colorAnimator = ObjectAnimator
                .ofObject(iconView, "colorFilter", new ArgbEvaluator(), 0, 0)
                .setDuration(DURATION_COLOR);
    }

    private void initViews(Context context, AttributeSet attrs, int defStyleAttr) {
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new LinearLayout.LayoutParams(context, attrs));
        setClipChildren(false);
        setGravity(Gravity.CENTER_VERTICAL);

        iconView = new ImageView(context, attrs, defStyleAttr);
        shadowIconView = new ImageView(context, attrs, defStyleAttr);

        textSwitcher = new TextSwitcher(context, attrs);
        textSwitcher.setFactory(() -> createTextView(context, attrs));
        textSwitcher.setInAnimation(createTextSwitcherAnim(context, android.R.anim.fade_in));
        textSwitcher.setOutAnimation(createTextSwitcherAnim(context, android.R.anim.fade_out));
        addView(textSwitcher);

        final FrameLayout iconLayout = new FrameLayout(context, attrs, defStyleAttr);
        iconLayout.setClipChildren(false);
        iconLayout.addView(iconView);
        iconLayout.addView(shadowIconView);
        addView(iconLayout);
    }

    @NonNull
    private TextView createTextView(Context context, AttributeSet attrs) {
        return new TextView(context, attrs);
    }

    @NonNull
    private Animation createTextSwitcherAnim(Context context, int fade_in) {
        Animation fadeIn = AnimationUtils.loadAnimation(context, fade_in);
        fadeIn.setDuration(DURATION_COLOR);
        return fadeIn;
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TMButton);

        initColorDefault(attributes);
        initTextViewAttrs(context, attributes);
        initColorChecked(attributes);
        initCheckedDrawable(attributes);
        initUncheckedDrawable(attributes);

        attributes.recycle();
    }

    private void initTextViewAttrs(Context context, TypedArray attributes) {
        textChecked = attributes.getString(R.styleable.TMButton_tmbutton_text_checked);
        textUnchecked = attributes.getString(R.styleable.TMButton_tmbutton_text_unchecked);
        int textAppearanceResId = attributes.getResourceId(R.styleable.TMButton_tmbutton_text_appearance, -1);
        int drawablePadding = attributes.getDimensionPixelSize(R.styleable.TMButton_tmbutton_drawable_padding, 0);

        if (TextUtils.isEmpty(textUnchecked) && TextUtils.isEmpty(textChecked)) {
            textSwitcher.setVisibility(View.GONE);
            return;
        }
        if (!TextUtils.isEmpty(textUnchecked) && TextUtils.isEmpty(textChecked)) {
            textChecked = textUnchecked;
        } else if (TextUtils.isEmpty(textUnchecked) && !TextUtils.isEmpty(textChecked)) {
            textUnchecked = textChecked;
        }

        textSwitcher.setCurrentText(textChecked);
        textSwitcher.setText(textUnchecked);

        for (int i = 0; i < textSwitcher.getChildCount(); i++) {
            TextView textView = (TextView) textSwitcher.getChildAt(i);
            TextViewCompat.setTextAppearance(textView, textAppearanceResId);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) textView.getLayoutParams();
            params.setMarginEnd(drawablePadding);
        }
    }

    private void initColorDefault(TypedArray attributes) {
        setColorUnchecked(
                attributes.getColor(
                        R.styleable.TMButton_color_unchecked,
                        Color.LTGRAY));
    }

    private void initColorChecked(TypedArray attributes) {
        setColorChecked(
                attributes.getColor(
                        R.styleable.TMButton_color_checked,
                        Color.MAGENTA));
    }

    private void initCheckedDrawable(TypedArray attributes) {
        if (!attributes.hasValue(R.styleable.TMButton_icon_drawable)) {
            throw new IllegalArgumentException("Missing attribute: icon_drawable");
        }
        Drawable drawable;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = attributes.getDrawable(R.styleable.TMButton_icon_drawable);
        } else {
            int iconResId = attributes.getResourceId(R.styleable.TMButton_icon_drawable, 0);
            drawable = AppCompatResources.getDrawable(getContext(), iconResId);
        }

        setIconDrawable(drawable);
    }

    private void initUncheckedDrawable(TypedArray attributes) {
        if (!attributes.hasValue(R.styleable.TMButton_unchecked_drawable)) {
            // this attr is optional
            return;
        }

        Drawable drawable;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = attributes.getDrawable(R.styleable.TMButton_unchecked_drawable);
        } else {
            int iconResId = attributes.getResourceId(R.styleable.TMButton_unchecked_drawable, 0);
            drawable = AppCompatResources.getDrawable(getContext(), iconResId);
        }

        setUncheckedDrawable(drawable);
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
        if (isUncheckedDrawableAvailable()) {
            iconView.setImageDrawable(checkedDrawable);
        }

        shadowIconView.setVisibility(View.VISIBLE);

        shadowAnimator.cancel();
        shadowAnimator
                .scaleX(SCALE_FACTOR)
                .scaleY(SCALE_FACTOR)
                .alpha(0.0f)
                .setDuration(DURATION_SHADOW_ANIM)
                .setListener(shadowAnimatorListener)
                .setInterpolator(INTERPOLATOR_DECELERATE);

        colorAnimator.cancel();
        colorAnimator.removeAllListeners();
        colorAnimator.addListener(colorAnimatorCheckListener);
        colorAnimator.setObjectValues(colorUnchecked, colorChecked);
        colorAnimator.start();

        ((TextView) textSwitcher.getNextView()).setTextColor(colorChecked);
        textSwitcher.setText(textChecked);
    }

    private void animateUnCheck() {
        if (isUncheckedDrawableAvailable()) {
            iconView.setImageDrawable(uncheckedDrawable);
        }

        shadowAnimator.cancel();
        colorAnimator.cancel();
        colorAnimator.removeAllListeners();
        colorAnimator.addListener(colorAnimatorUncheckListener);
        colorAnimator.setObjectValues(colorChecked, colorUnchecked);
        colorAnimator.start();

        textSwitcher.setText(textUnchecked);
    }


    private final AnimatorListenerAdapter shadowAnimatorListener = new AnimatorListenerAdapter() {

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

    private final Animator.AnimatorListener colorAnimatorCheckListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationCancel(Animator animation) {
            setIconViewChecked();
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            setIconViewChecked();
        }
    };

    private final Animator.AnimatorListener colorAnimatorUncheckListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationCancel(Animator animation) {
            setIconViewUnchecked();

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            setIconViewUnchecked();
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                iconView.animate()
                        .scaleX(0.7f)
                        .scaleY(0.7f)
                        .setDuration(150)
                        .setInterpolator(INTERPOLATOR_DECELERATE);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                iconView.animate()
                        .scaleX(1)
                        .scaleY(1)
                        .setInterpolator(INTERPOLATOR_DECELERATE);
                break;
        }
        return result;
    }

    /**
     * Set the icon drawable
     *
     * @param iconDrawable {@link Drawable}
     */
    public void setIconDrawable(Drawable iconDrawable) {
        this.checkedDrawable = iconDrawable;
        this.iconView.setImageDrawable(iconDrawable);
        this.shadowIconView.setImageDrawable(iconDrawable);
    }

    /**
     * Set the unchecked icon drawable.
     *
     * @param uncheckedDrawable {@link Drawable}
     */
    public void setUncheckedDrawable(@Nullable Drawable uncheckedDrawable) {
        this.uncheckedDrawable = uncheckedDrawable;
    }

    /**
     * Set the color for state unchecked
     * (Does not update immediately, call setChecked with forceUpdate=true, to force an update)
     *
     * @param color packed color int, AARRGGBB
     */
    public void setColorUnchecked(@ColorInt int color) {
        this.colorUnchecked = color;
    }

    /**
     * Set the color for state checked.
     * (Does not update immediately, call setChecked with forceUpdate=true, to force an update)
     *
     * @param color packed color int, AARRGGBB
     */
    public void setColorChecked(@ColorInt int color) {
        this.colorChecked = color;
    }

    /**
     * Change the checked state of the view.
     * Calls {@link #setChecked(boolean, boolean)} with given state, but <code>animateChange=false</code>
     *
     * @param checked new checked state
     */
    @Override
    public void setChecked(boolean checked) {
        setChecked(checked, false);
    }

    /**
     * Change the checked state of the view
     *
     * @param checked       new checked state
     * @param animateChange if true, animates the state change.
     */
    public void setChecked(boolean checked, boolean animateChange) {
        setChecked(checked, animateChange, false);
    }

    /**
     * Change the checked state of the view
     *
     * @param checked       new checked state
     * @param animateChange if true, animates the state change
     * @param forceUpdate   if true, forces an update of the drawable, otherwise skip changes when
     *                      already (un)checked.
     */
    public void setChecked(boolean checked, boolean animateChange, boolean forceUpdate) {
        if (!forceUpdate && this.isChecked == checked) {
            return;
        }
        this.isChecked = checked;

        if (animateChange) {
            if (isChecked) {
                animateCheck();
            } else {
                animateUnCheck();
            }
        } else {
            colorAnimator.cancel();
            if (isChecked) {
                setIconViewChecked();
                setTextViewChecked();
            } else {
                setIconViewUnchecked();
                setTextViewUnchecked();
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

    private void setIconViewChecked() {
        iconView.setColorFilter(colorChecked);

        if (isUncheckedDrawableAvailable()) {
            iconView.setImageDrawable(checkedDrawable);
        }
    }

    private void setTextViewChecked() {
        textSwitcher.setCurrentText(textChecked);
    }

    private void setIconViewUnchecked() {
        iconView.setColorFilter(colorUnchecked);

        if (isUncheckedDrawableAvailable()) {
            iconView.setImageDrawable(uncheckedDrawable);
        }
    }

    private void setTextViewUnchecked() {
        textSwitcher.setCurrentText(textUnchecked);
    }

    /**
     * Return true if the optional {@link #uncheckedDrawable} was provided, either by XML
     * attribute {@link R.attr#unchecked_drawable} or setter {@link #setUncheckedDrawable(Drawable)}.
     */
    private boolean isUncheckedDrawableAvailable() {
        return uncheckedDrawable != null;
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
