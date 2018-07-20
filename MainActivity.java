package net.jacoblo.mysampleapp1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Timer timer;
    private MemoryData md;
    private boolean showAns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timer = new Timer();
        md = new MemoryData();
        showAns = false;

        setContentView(R.layout.activity_main);

        final Handler h = new Handler();
        final int delay = 500; //milliseconds

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
        tv_left.setText(""+md.itemLeft());
        if ( md.itemLeft() <= 0 ) {
            timer.pause();
            tv_num.setText("Done!");
            tv_ans.setText( "");
            return;
        }
        tv_num.setText(""+md.getCurrentNumber());
        TextView tv_timer = findViewById(R.id.tv_timer);
        tv_timer.setText(timer.toString());
        tv_ans.setText( ( showAns ? md.getCurrentItem() : "" ));
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.pause();
    }

    public void onClick(View view) {
        switch ( view.getId()) {
            case R.id.tv_num:
            case R.id.tv_ans:
                md.next();
                showAns = false;
                break;
            case R.id.tv_left:
                showAns = true;
                break;
            case R.id.tv_timer:
                md.reset();
                timer.reset();
                timer.start();
                break;
        }
        this.update();
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
        private static final String[] MEM_DATA = {
                "Vivian Law"
                ,"O'Brien"
                ,"Linus"
                ,"OrlanDo bloom"
        };
        private int[] m_Randoms;
        private int m_Pointer;

        public MemoryData () {
            reset();
        }

        public int getCurrentNumber() {
            if (m_Pointer >= MEM_DATA.length) return Integer.MAX_VALUE;
            return m_Randoms[m_Pointer] + 1;
        }

        public String getCurrentItem() {
            if (m_Pointer >= MEM_DATA.length) return "";
            return MEM_DATA[m_Randoms[m_Pointer]];
        }

        public int itemLeft() {
            return MEM_DATA.length - m_Pointer;
        }

        public boolean next() {
            if (m_Pointer >= MEM_DATA.length ) return false;
            m_Pointer++;
            return true;
        }

        public void reset() {
            m_Randoms = new int[MEM_DATA.length];
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
    }
}
