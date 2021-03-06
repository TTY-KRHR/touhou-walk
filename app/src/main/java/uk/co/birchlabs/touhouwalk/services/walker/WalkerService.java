package uk.co.birchlabs.touhouwalk.services.walker;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.Arrays;

/**
 * Created by birch on 08/10/2016.
 */

public class WalkerService extends Service {
    private WalkerView view;
    private RenderWorker renderWorker;
    private WorldWorker worldWorker;
    private ServiceEventHandler serviceEventHandler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        System.out.println("Walker service created");

        super.onCreate();

        final WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);

        final DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        view = new WalkerView(this);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, // TYPE_SYSTEM_ALERT is denied in apiLevel >=19
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN,
                PixelFormat.TRANSLUCENT
        );
//        view.setFitsSystemWindows(false); // allow us to draw over status bar, navigation bar
//        params.width = params.height = Math.max(metrics.widthPixels, metrics.heightPixels);
        params.setTitle("Touhou");

        wm.addView(view, params);

        final GensoukyouFactory gensoukyouFactory = new GensoukyouFactory(
                metrics.widthPixels,
                metrics.heightPixels,
                getApplicationContext()
        );
        final Gensoukyou gensoukyou = gensoukyouFactory.construct();

        renderWorker = new RenderWorker(view);
        worldWorker = new WorldWorker(gensoukyou);

        serviceEventHandler = new ServiceEventHandlerDelegator(
                Arrays.asList(
                        worldWorker.getServiceEventHandler(),
                        renderWorker.getServiceEventHandler()
                )
        );

        view.init(
                new ViewLifeCycleCallbackDelegator(
                        Arrays.asList(
                                worldWorker.getViewEventHandler(),
                                renderWorker.getViewEventHandler(),
                                gensoukyou.getViewEventHandler()
                        )
                ),
                gensoukyou
        );

//
//        final LayoutInflater inflater = LayoutInflater.from(this);
//
//        final ViewGroup mTopView = (ViewGroup) inflater.inflate(R.layout.activity_main, null);
//        this.getWindow().setAttributes(params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        serviceEventHandler.onDestroyed();

        ((WindowManager)getSystemService(WINDOW_SERVICE)).removeView(view);
        view = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        serviceEventHandler.onConfigurationChanged(newConfig);
    }
}
