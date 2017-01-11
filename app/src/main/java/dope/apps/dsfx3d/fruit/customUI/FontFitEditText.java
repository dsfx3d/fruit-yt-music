package dope.apps.dsfx3d.fruit.customUI;
import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.EditText;

public class FontFitEditText extends EditText {

    // Attributes
    private Paint mTestPaint;
    /** 'Initial' text size */
    private float mDefaultTextSize;

    public FontFitEditText(final Context context) {
        super(context);
        initialize();
    }

    public FontFitEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        mTestPaint = new Paint();
        mTestPaint.set(this.getPaint());
        mDefaultTextSize = getTextSize();
    }

    /*
     * Re size the font so the specified text fits in the text box
     * assuming the text box is the specified width.
     */
    private void refitText(final String text, final int textWidth) {

        if(textWidth <= 0 || text.isEmpty()) {
            return;
        }

        int targetWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();

        // this is most likely a non-relevant call
        if(targetWidth <= 2) {
            return;
        }

        // text already fits with the xml-defined font size?
        mTestPaint.set(this.getPaint());
        mTestPaint.setTextSize(mDefaultTextSize);

        // adjust text size using binary search for efficiency
        float hi = mDefaultTextSize;
        float lo = 2;
        final float threshold = 0.2f; // How close we have to be

        while (hi - lo > threshold) {
            float size = (hi + lo) / 2;
            mTestPaint.setTextSize(size);
            if(mTestPaint.measureText(text) >= targetWidth) {
                hi = size; // too big
            } else {
                lo = size; // too small
            }
        }

        // Use lo so that we undershoot rather than overshoot
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, lo);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int height = getMeasuredHeight();
        refitText(this.getText().toString(), parentWidth);
        this.setMeasuredDimension(parentWidth, height);
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start,
                                 final int before, final int after) {
        refitText(text.toString(), this.getWidth());
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        if (w != oldw || h != oldh) {
            refitText(this.getText().toString(), w);
        }
    }
}