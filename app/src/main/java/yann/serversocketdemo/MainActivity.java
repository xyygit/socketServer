package yann.serversocketdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private Thread thread;
    private Runnable runnable;
    private TextView tvMeaasge;
    private MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMeaasge = findViewById(R.id.tv_message);
        myHandler = new MyHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        runnable = new ServerSocketRunable();
        thread = new Thread(runnable);
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.destroy();
        myHandler.removeCallbacks(runnable);
    }

    class ServerSocketRunable implements Runnable {

        @Override
        public void run() {
            ServerSocketPoolTest socketPoolTest = new ServerSocketPoolTest();
            socketPoolTest.testCommon(myHandler);
        }
    }

    /**
     * 声明一个静态的Handler内部类，并持有外部类的弱引用
     */
    public class MyHandler extends Handler {

        private final WeakReference<MainActivity> mActivty;

        private MyHandler(MainActivity mActivty) {
            this.mActivty = new WeakReference<>(mActivty);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = mActivty.get();
            if (activity != null) {
            }
        }
    }
}
