package fr.codevallee.formation.android_tp15;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private AtomicBoolean isThreadRunning = new AtomicBoolean();
    private AtomicBoolean isThreadPausing = new AtomicBoolean();

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

            if(isThreadRunning.get()) {
                int progress = msg.getData().getInt("PROGRESS");
                progressBar.setProgress(progress);
            }
        }
    };

    private Thread progressThread = new Thread(new Runnable() {
        @Override
        public void run() {
            if (isThreadRunning.get()) {
                try {
                    for (int i = 0; i <= 100; i++) {
                        if(isThreadPausing.get()) {
                            Thread.sleep(1000);
                        } else {
                            Message message = handler.obtainMessage();
                            Bundle messageBundle = new Bundle();
                            messageBundle.putInt("PROGRESS", i);
                            message.setData(messageBundle);
                            handler.sendMessage(message);
                            Log.d("Info", "Progress : " + i + "%");
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        if (i == 100) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button goButton = (Button) findViewById(R.id.goButton);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpProgress();
            }
        });
    }

    protected void onDestroy() {
        isThreadRunning.set(false);
        super.onDestroy();
    }

    protected void onPause() {
        isThreadPausing.set(true);
        super.onPause();
    }

    protected void onResume() {
        isThreadPausing.set(false);
        super.onResume();
    }

    private void setUpProgress() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if(progressBar.getProgress() == 100) {
            progressThread.interrupt();
            isThreadRunning.set(false);
        }

        if(!isThreadRunning.get()) {
            isThreadRunning.set(true);
            progressThread.start();
        }
    }
}
