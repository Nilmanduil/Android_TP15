package fr.codevallee.formation.android_tp15;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private AtomicBoolean isThreadRunning = new AtomicBoolean();
    private AtomicBoolean isThreadPausing = new AtomicBoolean();

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

            if(isThreadRunning.get()) {
                int progress = msg.getData().getInt("PROGRESS");
                // progressBar.setProgress(progress);
                progressBar.incrementProgressBy(1);
            }
        }
    };

    private Thread progressThread = new Thread(new Runnable() {
        @Override
        public void run() {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

            if (isThreadRunning.get()) {
                try {
                    int i = progressBar.getProgress();
                    while (i <= 100){
                        if(isThreadPausing.get()) {
                            Thread.sleep(1000);
                        } else {
                            Message message = handler.obtainMessage();
                            Bundle messageBundle = new Bundle();
                            messageBundle.putInt("PROGRESS", i);
                            message.setData(messageBundle);
                            handler.sendMessage(message);

                            i = progressBar.getProgress();
                            Log.d("Info", "Progress : " + i + "%");
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (i == 100)
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(MainActivity.this, R.string.toast, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

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
            /*
            progressThread.interrupt();
            try {
                progressThread.join();
            } catch (Exception e) {
                e.printStackTrace();
            } //*/
            isThreadRunning.set(false);
        }

        progressBar.setProgress(0);
        //*
        if(isThreadRunning.get()) {

        } else {
            isThreadRunning.set(true);
            // progressThread.start();
            ProgressAsyncTask progressTask = new ProgressAsyncTask();
            progressTask.execute();
        } //*/
    }

    private class ProgressAsyncTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            int i = progressBar.getProgress();
            while (i < 101) {
                if (isCancelled())
                    break;

                try {
                    Thread.sleep (100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                publishProgress(i);
                i = progressBar.getProgress();
                if (i == 100) {
                    break;
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Integer progress = values[0];
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.incrementProgressBy(1);
            Log.d("Info", "Progress bar : " + progress + "%");
        }
    }
}
