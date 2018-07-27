package net.jacoblo.mem;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int CHOOSE_FILE_REQUESTCODE = 8777;

    private Timer m_Timer;
    private MemoryData m_MemData;
    private boolean showAns;

    public MainActivity () {
        super();
        m_Timer = new Timer();
        m_MemData = new MemoryData();
        showAns = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        // update GUI handler
        final Handler h = new Handler();
        final int delay = 1000; //milliseconds

        h.postDelayed(new Runnable(){
            public void run(){
                update();
                h.postDelayed(this, delay);
            }
        }, delay);

    }

    private void update() {
        TextView tv_num = findViewById(R.id.tv_num);
        TextView tv_left = findViewById(R.id.tv_left);
        TextView tv_ans = findViewById(R.id.tv_ans);
        tv_left.setText(""+ m_MemData.itemLeft());
        if ( m_MemData.itemLeft() <= 0 ) {
            m_Timer.pause();
            tv_num.setText("Done!");
            tv_ans.setText( "");
            return;
        }
        tv_num.setText(m_MemData.getCurrentItem().title);
        TextView tv_timer = findViewById(R.id.tv_timer);
        tv_timer.setText(m_Timer.toString());
        tv_ans.setText( ( showAns ? m_MemData.getCurrentItem().value : "" ));
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_Timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_Timer.pause();
    }

    public void onClick(View view) {
        switch ( view.getId()) {
            case R.id.tv_num:
            case R.id.tv_ans:
                m_MemData.next();
                showAns = false;
                break;
            case R.id.tv_left:
                showAns = true;
                break;
            case R.id.tv_timer:
                m_MemData.reset();
                m_Timer.reset();
                m_Timer.start();
                break;
            case R.id.tv_file:
                openFile(CHOOSE_FILE_REQUESTCODE);
                break;
        }
        this.update();
    }

    public void openFile(int CODE) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        startActivityForResult(intent, CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null ) {
            Uri uri = data.getData();
            setFilePathAndUpdateMemData(uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setFilePathAndUpdateMemData( Uri uri ) {
        ArrayList<String> inputs = readTextFromUri(uri);
        m_MemData.mem_data = MemoryData.convertToMemData(inputs);
        m_MemData.reset();
    }

    private ArrayList<String> readTextFromUri(Uri uri)  {
        ArrayList<String> result = new ArrayList<>();
        String line;

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream));

            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
            reader.close();
            inputStream.close();
        }
        catch ( IOException ioe ) {
            return new ArrayList<>();
        }

        return result;
    }

    private static class Timer
    {
        private int m_Secs;
        private Thread m_Timer;

        public Timer() { reset(); }

        public void start() { m_Timer.start(); }

        public void pause() { resetTimer(); }

        public void reset() {
            resetTimer();
            m_Secs = 0;
        }

        @Override
        public String toString() {
            long hours =   m_Secs  / 3600;
            long mins =  ( m_Secs / 60 ) % 60;
            long secs = m_Secs % 60;

            String minsS = ( mins < 10 ? "0"  : "" ) + mins;
            String secsS = ( secs < 10 ? "0"  : "" ) + secs;

            return ( hours > 0 ? hours + ":" : "") +
                    ( mins > 0 || hours > 0 ? minsS + ":" : "") +
                    secsS;
        }

        private void resetTimer() {
            if (m_Timer != null && m_Timer.isAlive()) {
                m_Timer.interrupt();
            }
            Runnable updateTimer = new Runnable() {
                public void run() {
                    while(true) {
                        m_Secs++;
                        try{
                            Thread.sleep(1000);
                        }catch(InterruptedException ie) {
                            return;
                        }
                    }
                }
            };
            m_Timer = new Thread(updateTimer);
        }
    }

    public static class MemoryData {

        private ArrayList<FlashCard>  mem_data;
        private int[] m_Randoms;
        private int m_Pointer;

        public MemoryData () {
            mem_data = new ArrayList<>();
            reset();
        }

        public int getCurrentNumber() {
            if (m_Pointer >= mem_data.size()) return Integer.MAX_VALUE;
            return m_Randoms[m_Pointer] + 1;
        }

        public FlashCard getCurrentItem() {
            if (m_Pointer >= mem_data.size()) return null;
            return mem_data.get(m_Randoms[m_Pointer]);
        }

        public int itemLeft() {
            return mem_data.size() - m_Pointer;
        }

        public boolean next() {
            if (m_Pointer >= mem_data.size() ) return false;
            m_Pointer++;
            return true;
        }

        public void reset() {
            m_Randoms = new int[mem_data.size()];
            for(int i = 0 ; i < m_Randoms.length ; i++ ) {
                m_Randoms[i] = i;
            }
            for (int i = 0 ; i < m_Randoms.length ; i++ ) {
                swap(m_Randoms, i, (int) (Math.random() * m_Randoms.length) );
            }
            m_Pointer = 0;
        }

        private void swap(int[] arr, int i, int j) {
            if (arr == null || i < 0 || j < 0 || i >= arr.length || j >= arr.length) return;
            int tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }

        public static ArrayList<FlashCard> convertToMemData(ArrayList<String> inputs) {
            if ( inputs == null || inputs.size() <= 0 ) return new ArrayList<>();

            ArrayList<FlashCard> resultCards = new ArrayList<>();
            for ( String s : inputs) {
                FlashCard fc = FlashCard.convertCRVToFlashCard(s);
                if (fc != null) {
                    resultCards.add(fc);
                }
            }

            return resultCards;
        }

        public static class FlashCard
        {
            String title;
            String value;

            public FlashCard ( String t, String v ) {
                title = t;
                value = v;
            }

            public static FlashCard convertCRVToFlashCard(String s) {
                if (s == null || s.length() <= 0 ) return null;

                int start = 0;

                ArrayList<String> result = new ArrayList<>();

                while( s.indexOf(',', start+1) > 0) {
                    int end = s.indexOf(',', start+1);
                    result.add(s.substring(start, end));
                    start = end;
                }

                result.add(s.substring(start+1, s.length()));

                FlashCard fc = new FlashCard("","");
                if (result.size() > 0 ) {
                    fc.title = result.get(0);
                }
                if ( result.size() > 1 ) {
                    fc.value = result.get(1);
                }

                return fc;
            }

        }
    }
}
