package uk.co.birchlabs.touhouwalk.services.walker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by birch on 08/10/2016.
 */

public class WalkerView extends SurfaceView {
    private Paint systemPaint;
//    private Paint systemPaint2;
    private SurfaceHolder holder;
//    private DisplayMetrics metrics;
//    private WindowManager wm;

    private Bitmap bmp;
    private Gensoukyou gensoukyou;
    private final int scaleFactor = 2;
    private ViewEventHandler viewEventHandler;

//    private final Context c;

    public WalkerView(Context c, AttributeSet attributeSet) {
        super(c, attributeSet);
//        this.c = c;
    }

    public WalkerView(Context c) {
        super(c);
//        this.c = c;
    }

    public void init(
            final ViewEventHandler viewEventHandler,
            final Gensoukyou gensoukyou
    ) {
//        metrics = new DisplayMetrics();
//        wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
//        wm.getDefaultDisplay().getMetrics(metrics);

        this.viewEventHandler = viewEventHandler;
        this.gensoukyou = gensoukyou;

        setZOrderOnTop(true);
        holder = getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);

        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
//                Canvas c = holder.lockCanvas(null);
//                onDraw(c);
//                holder.unlockCanvasAndPost(c);
                viewEventHandler.onReady();
            }

            @Override
            public void surfaceChanged(
                    SurfaceHolder holder,
                    int format,
                    int width,
                    int height
            ) {

            }
        });
//        final BitmapFactory.Options dimensions = new BitmapFactory.Options();
//        dimensions.inJustDecodeBounds = true;
//        final Bitmap sham = BitmapFactory.decodeResource(getResources(), R.drawable.reimu, dimensions);


        systemPaint = new Paint();
//        systemPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
//        systemPaint.setColor(Color.TRANSPARENT);
//        systemPaint.setAlpha(75);

//        updateDisplay();

    }

    private Bitmap getScaledBmp(int id) {
        return resizeBitmap(
                getRawBmp(
                        id
                ),
                scaleFactor
        );
    }

    private Bitmap getRawBmp(int id) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        return BitmapFactory.decodeResource(
                getResources(),
                id,
                options
        );
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int multiplier) {
        return Bitmap.createScaledBitmap(
                bitmap,
                bitmap.getWidth()*multiplier,
                bitmap.getHeight()*multiplier,
                false
        );
    }


    private void updateDisplay() {
        requestLayout();
        // or
        // invalidate();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (viewEventHandler != null) {
            viewEventHandler.onSizeChanged(w, h, oldw, oldh);
        }
//        Toast.makeText(getContext(), String.format("%d*%d -> %d*%d", oldw, oldh, w,h), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        wm.getDefaultDisplay().getMetrics(metrics);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

//        canvas.drawRect(
//                0,
//                0,
//                getWidth(),
//                getHeight(),
//                systemPaint
//        );
//        canvas.drawColor(Color.BLACK);
//        canvas.drawColor(Color.RED);

        for(Baka baka : gensoukyou.getBakas()) {
            drawBaka(
                    canvas,
                    baka
            );
        }
    }

    private void drawBaka(
            Canvas canvas,
            Baka baka
    ) {
        canvas.drawBitmap(
                baka.getBitmap(),
                baka.getFrame(),
                baka.getDestination(
                        gensoukyou.getWidthPixels(),
                        gensoukyou.getHeightPixels()
                ),
                systemPaint
        );

//        canvas.drawBitmap(
//                bmp,
//                x,
//                y,
//                systemPaint
//        );
    }
}
