package net.jacoblo.app.SimpleFlashCardsAndroid;

import android.graphics.Color;

import java.util.ArrayList;

public class FlashCard
{
    private String title;
    private String value;
    private int m_Color;

    public FlashCard() { this(""); }
    public FlashCard(String t) { this(t,""); }

    public FlashCard (String t, String v ) {
        setTitle(t);
        setValue(v);
    }

    public FlashCard( String t, String v, int c ) {
        setTitle(t);
        setValue(v);
        m_Color = c;
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

        FlashCard fc = new FlashCard();
        if (result.size() > 0 ) {
            fc.setTitle(result.get(0));
        }
        if ( result.size() > 1 ) {
            fc.setValue(result.get(1));
        }

        return fc;
    }

    public static final int DARK_COLOR_FACTOR = 0x99;
    public static int setDarkerColor(int color) {

        int newR = Color.red(color) % DARK_COLOR_FACTOR ;
        int newG = Color.green(color) % DARK_COLOR_FACTOR ;
        int newB = Color.blue(color) % DARK_COLOR_FACTOR ;
        return Color.argb(Color.alpha(color),newR, newG, newB);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        m_Color = setDarkerColor( Color.argb( 0xff, title.hashCode(), Color.green(m_Color), 0xff - title.hashCode()) );
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        m_Color = setDarkerColor( Color.argb( 0xff, Color.red(m_Color), value.hashCode(), Color.blue(m_Color)) );
    }
    public int getColor() {
        return m_Color;
    }

    public void setColor(int color) {
        this.m_Color = color;
    }

}