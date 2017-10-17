package fr.codevallee.formation.android_tp15;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

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

    private void setUpProgress() {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                int progress = msg.getData().getInt("PROGRESS");
                progressBar.setProgress(progress);
            }
        };

        Thread progressThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 100; i++) {
                    Message message = handler.obtainMessage();
                    Bundle messageBundle = new Bundle();
                    messageBundle.putInt("PROGRESS", i);
                    message.setData(messageBundle);
                    handler.sendMessage(message);
                    Log.d("Info", "Progress : " + i + "%");
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        progressThread.start();
    }
}
