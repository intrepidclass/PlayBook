package voice.app.features.imagepicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import voice.app.R;

/**
 * Layout that enables a crop selection. Put this on top of over another view.
 */
public class CropOverlay extends FrameLayout {

    private final View leftCircle;
    private final View topCircle;
    private final View rightCircle;
    private final View bottomCircle;
    private final PointF lastTouchPoint = new PointF();
    private final RectF dragRectCache = new RectF();
    private final RectF dragRect = new RectF();
    private final RectF bounds = new RectF();
    private final Paint darkeningPaint = new Paint();

    private final ScaleGestureDetector scaleGestureDetector;

    private boolean selectionOn = false;

    public CropOverlay(@NonNull Context context) {
        this(context, null);
    }

    public CropOverlay(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropOverlay(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        darkeningPaint.setARGB(120, 0, 0, 0);

        scaleGestureDetector = new ScaleGestureDetector(
                context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        float dx = detector.getCurrentSpanX() - detector.getPreviousSpanX();
                        float dy = detector.getCurrentSpanY() - detector.getPreviousSpanY();
                        float max = Math.max(dx, dy);
                        squareInset(dragRect, -max);
                        return max != 0;
                    }
                }
        );

        leftCircle = newCircle();
        topCircle = newCircle();
        rightCircle = newCircle();
        bottomCircle = newCircle();

        setWillNotDraw(false);

        addView(leftCircle);
        addView(topCircle);
        addView(rightCircle);
        addView(bottomCircle);

        updateForSelectionState();
    }

    public boolean isSelectionOn() {
        return selectionOn;
    }

    public void setSelectionOn(boolean selectionOn) {
        if (this.selectionOn != selectionOn) {
            this.selectionOn = selectionOn;
            updateForSelectionState();
        }
    }

    private void updateForSelectionState() {
        int visibility = selectionOn ? View.VISIBLE : View.GONE;
        leftCircle.setVisibility(visibility);
        rightCircle.setVisibility(visibility);
        topCircle.setVisibility(visibility);
        bottomCircle.setVisibility(visibility);

        invalidate();
    }

    private EventType eventType = null;
    private Resize resizeType = null;
    private final float touchOffset = getContext().getResources().getDisplayMetrics().density * 16;

    private View newCircle() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.circle, this, false);
        view.setVisibility(View.GONE);
        return view;
    }

    private float minRectSize() {
        return Math.min(bounds.width(), bounds.height()) / 3f;
    }

    private boolean inRangeOf(float value, float target) {
        return value >= (target - touchOffset) && value <= (target + touchOffset);
    }

    private Resize asResizeType(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (inRangeOf(x, dragRect.left)) return Resize.LEFT;
        if (inRangeOf(x, dragRect.right)) return Resize.RIGHT;
        if (inRangeOf(y, dragRect.top)) return Resize.TOP;
        if (inRangeOf(y, dragRect.bottom)) return Resize.BOTTOM;
        return null;
    }

    private void squareInset(RectF rect, float value) {
        rect.inset(value, value);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!selectionOn) return super.onTouchEvent(event);

        dragRectCache.set(dragRect);

        scaleGestureDetector.onTouchEvent(event);
        boolean gestureDetectorIsHandling = scaleGestureDetector.isInProgress();
        if (gestureDetectorIsHandling) {
            resizeType = null;
            eventType = null;
            lastTouchPoint.set(0f, 0f);
        } else {
            int action = event.getAction();
            float x = event.getX();
            float y = event.getY();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    resizeType = asResizeType(event);
                    if (resizeType != null) {
                        eventType = EventType.RESIZE;
                        lastTouchPoint.set(x, y);
                    } else if (dragRect.contains(x, y)) {
                        lastTouchPoint.set(x, y);
                        eventType = EventType.DRAG;
                    } else {
                        eventType = null;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = x - lastTouchPoint.x;
                    float deltaY = y - lastTouchPoint.y;
                    lastTouchPoint.set(x, y);

                    if (eventType == EventType.DRAG) {
                        dragRect.offset(deltaX, deltaY);
                    } else if (eventType == EventType.RESIZE) {
                        float inset = 0;
                        switch (resizeType) {
                            case TOP:
                                inset = y - dragRect.top;
                                break;
                            case RIGHT:
                                inset = dragRect.right - x;
                                break;
                            case BOTTOM:
                                inset = dragRect.bottom - y;
                                break;
                            case LEFT:
                                inset = x - dragRect.left;
                                break;
                        }
                        squareInset(dragRect, inset);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    lastTouchPoint.set(0f, 0f);
                    break;
            }
        }

        preserveSize();
        preserveBounds();
        if (!dragRect.equals(dragRectCache)) invalidate();
        return true;
    }

    private void preserveBounds() {
        float rightDiff = dragRect.right - bounds.right;
        if (rightDiff > 0) {
            dragRect.offset(-rightDiff, 0f);
        }

        float leftDiff = dragRect.left - bounds.left;
        if (leftDiff < 0) {
            dragRect.offset(-leftDiff, 0f);
        }

        float topDiff = dragRect.top - bounds.top;
        if (topDiff < 0) {
            dragRect.offset(0f, -topDiff);
        }

        float bottomDiff = dragRect.bottom - bounds.bottom;
        if (bottomDiff > 0) {
            dragRect.offset(0f, -bottomDiff);
        }
    }

    private void preserveSize() {
        float minSize = minRectSize();
        float w = dragRect.width();
        if (w < minSize) {
            float diff = minSize - w;
            squareInset(dragRect, -diff / 2f);
        }

        float dragW = dragRect.width();
        float boundsSize = Math.min(bounds.width(), bounds.height()) - 2f;
        float diff = dragW - boundsSize;
        if (diff > 0) {
            squareInset(dragRect, diff / 2f);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        lastTouchPoint.set(0f, 0f);
        float wf = w;
        float hf = h;
        bounds.set(0f, 0f, wf, hf);
        float dragSize = Math.min(wf, hf);

        dragRect.set(0f, 0f, dragSize, dragSize);
        dragRect.offset(bounds.centerX() - dragSize / 2f, bounds.centerY() - dragSize / 2f);
    }

    @NonNull
    public Rect getSelectedRect() {
        int realLeft = Math.round(dragRect.left);
        int realTop = Math.round(dragRect.top);
        int realRight = Math.round(dragRect.right);
        int realBottom = Math.round(dragRect.bottom);

        return new Rect(realLeft, realTop, realRight, realBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!selectionOn) return;

        if (!bounds.isEmpty()) {
            float boundsHeight = bounds.height();
            float boundsWidth = bounds.width();

            float left = dragRect.left;
            float top = dragRect.top;
            float right = dragRect.right;
            float bottom = dragRect.bottom;
            float centerX = left + dragRect.width() / 2f;
            float centerY = top + dragRect.height() / 2f;

            canvas.drawRect(0f, 0f, left, boundsHeight, darkeningPaint); // left
            canvas.drawRect(left, 0f, right, top, darkeningPaint); // top
            canvas.drawRect(right, 0f, boundsWidth, boundsHeight, darkeningPaint); // right
            canvas.drawRect(left, bottom, right, boundsHeight, darkeningPaint); // bottom

            center(topCircle, centerX, top);
            center(leftCircle, left, centerY);
            center(rightCircle, right, centerY);
            center(bottomCircle, centerX, bottom);
        }
    }

    private void center(View view, float x, float y) {
        view.setTranslationX(x - view.getWidth() / 2f);
        view.setTranslationY(y - view.getHeight() / 2f);
    }

    enum EventType {
        DRAG,
        RESIZE,
    }

    enum Resize {
        TOP,
        RIGHT,
        BOTTOM,
        LEFT,
    }
}
