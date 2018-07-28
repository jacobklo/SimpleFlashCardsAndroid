package net.jacoblo.simpleflashcardsandroid;
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

import net.jacoblo.lib.Timer;
import net.jacoblo.app.SimpleFlashCardsAndroid.MemoryData;

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
        tv_num.setText(m_MemData.getCurrentItem().getTitle());
        TextView tv_timer = findViewById(R.id.tv_timer);
        tv_timer.setText(m_Timer.toString());
        tv_ans.setText( ( showAns ? m_MemData.getCurrentItem().getValue() : "" ));

        View root = findViewById(R.id.layout_Root);
        root.setBackgroundColor(m_MemData.getCurrentItem().getColor());

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
        m_MemData.set_mem_data(MemoryData.convertToMemData(inputs));
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

}
