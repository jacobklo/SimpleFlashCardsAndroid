package net.jacoblo.lib;

public class Timer
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

