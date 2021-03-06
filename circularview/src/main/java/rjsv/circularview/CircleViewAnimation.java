package rjsv.circularview;

import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import rjsv.circularview.enumerators.AnimationStyle;
import rjsv.circularview.utils.CircleViewAnimationListener;
import rjsv.circularview.utils.Disposable;

/**
 * Description
 *
 * @author <a href="mailto:ricardo.vieira@xpand-it.com">RJSV</a>
 * @version $Revision : 1 $
 */

public class CircleViewAnimation extends Animation implements Disposable {

    private CircleView circleView;

    private float startValue;
    private float endValue;
    private float currentValue;
    private long duration;
    private boolean isAnimationRunning = false;
    private AnimationStyle circleViewAnimationStyle;
    private Interpolator circleViewInterpolator;
    private CircleViewAnimationListener circleViewAnimationListener;
    private Handler timerManager;
    private Runnable timerOperation;

    // Constructor
    public CircleViewAnimation(final CircleView circleView) {
        this.startValue = circleView.getProgressValue();
        this.endValue = 0;
        this.circleView = circleView;
        this.circleViewAnimationStyle = AnimationStyle.PERIODIC;
        this.circleViewAnimationListener = new CircleViewAnimationListener();
        this.timerManager = new Handler();
        this.timerOperation = new Runnable() {
            public void run() {
                System.out.println("rsv -> we have successfully finished");
            }
        };
        setInterpolator(new LinearInterpolator());
    }

    public CircleViewAnimation setDuration(float durationInSeconds) {
        setDuration((long) durationInSeconds);
        return this;
    }

    public CircleViewAnimation setDuration(int durationInSeconds) {
        setDuration((long) durationInSeconds);
        return this;
    }

    public CircleViewAnimation setAnimationStyle(AnimationStyle style) {
        this.circleViewAnimationStyle = style;
        return this;
    }

    public CircleViewAnimation setCustomAnimationListener(AnimationListener listener) {
        this.circleViewAnimationListener.registerAnimationListener(listener);
        setAnimationListener(listener != null ? circleViewAnimationListener : null);
        return this;
    }

    public CircleViewAnimation setCustomInterpolator(Interpolator i) {
        this.circleViewInterpolator = i;
        super.setInterpolator(circleViewInterpolator);
        return this;
    }

    public CircleViewAnimation setTimerOperationOnFinish(Runnable r) {
        if (r != null) {
            timerOperation = r;
        }
        return this;
    }

    // Overridden values
    public void start(float startValue, float endValue) {
        if (!isAnimationRunning) {
            this.startValue = startValue;
            this.endValue = endValue;
            setDuration(startValue - endValue);
            isAnimationRunning = true;
            circleView.startAnimation(this);
            timerManager.postDelayed(timerOperation, duration * 1000);
        }
    }

    public void stop() {
        if (isAnimationRunning) {
            isAnimationRunning = false;
            if (this.circleView != null) {
                timerManager.removeCallbacks(timerOperation);
                circleView.clearAnimation();
            }
        }
    }

    @Override
    public void start() {
        start(circleView.getProgressValue(), 0);
    }

    @Override
    public void setDuration(long durationInSeconds) {
        duration = durationInSeconds;
        super.setDuration(duration * 1000);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        if (interpolatedTime == 1.0) {
            stop();
        }
        currentValue = startValue + ((endValue - startValue) * interpolatedTime);
        float changingValue = currentValue;
        if (AnimationStyle.PERIODIC.equals(circleViewAnimationStyle)) {
            changingValue = (int) changingValue;
        }
        circleView.setProgressValue(changingValue);
    }

    @Override
    public void disposeData() {
        this.setInterpolator(null);
        circleViewInterpolator = null;
        if (circleViewAnimationListener != null) {
            circleViewAnimationListener.unregisterAnimationListeners();
            circleViewAnimationListener = null;
        }
        if (timerManager != null) {
            if (timerOperation != null) {
                timerManager.removeCallbacks(timerOperation);
            }
            timerOperation = null;
            timerManager = null;
        }
    }

}