package uk.co.birchlabs.touhouwalk.services.walker;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by birch on 01/01/2017.
 */

public class RenderWorker extends Thread {
    private static final String logTag = RenderWorker.class.getName();

    private static final long FPS = 15;

    private final WalkerView view;

    private boolean running;

    public RenderWorker(
            WalkerView view
    ) {
        this.view = view;
    }

    public ViewEventHandler getViewEventHandler() {
        return new ViewEventHandler() {
            @Override
            public void onReady() {
                running = true;
                RenderWorker.this.start();
            }

            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
            }
        };
    }

    public ServiceEventHandler getServiceEventHandler() {
        return new ServiceEventHandler() {
            @Override
            public void onDestroyed() {
                running = false;
                while (RenderWorker.this.isAlive()) {
                    try {
                        RenderWorker.this.join();
                        break;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        Log.i(logTag, "Interrupted during join", e);
                    }
                }
            }

            @Override
            public void onConfigurationChanged(Configuration newConfig) {
            }
        };
    }

    @SuppressLint("WrongCall")
    @Override
    public void run() {
        final long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        while (running) {
            Canvas c = null;
            startTime = System.currentTimeMillis();
            try {
                c = view.getHolder().lockCanvas();
                synchronized (view.getHolder()) {
                    view.onDraw(c);
                }
            } finally {
                if (c != null) {
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }
            sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
            try {
                sleep(Math.max(10, sleepTime));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.i(logTag, "Interrupted during sleep", e);
            }
        }
    }
}
