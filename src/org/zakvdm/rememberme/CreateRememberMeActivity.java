package org.zakvdm.rememberme;

import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.android.widgets.DateSlider.DateSlider;
import com.googlecode.android.widgets.DateSlider.DateTimeSlider;

public class CreateRememberMeActivity extends Activity {

    private static final int TIME_BEFORE_KITTY_IS_CHANGED_IN_MILLISECONDS = 3000;

    private static final int DELAY_BEFORE_FIRST_KITTY_IN_MILLISECONDS = 4000;

    static final int WHENSELECTOR_ID = 1;

    static final int NEW_REMEMBER_ME_REQUEST_CODE = 192837;

    private TextView dateText;

    private ImageView kitty;

    private static final int[] KITTY_IMAGES = new int[] { R.drawable.remembermemeow1, R.drawable.remembermemeow2, R.drawable.remembermemeow3,
            R.drawable.remembermemeow4, R.drawable.remembermemeow5 };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dateText = (TextView) this.findViewById(R.id.selectedWhen);
        kitty = (ImageView) this.findViewById(R.id.kitty);

        scheduleKittyCycling();

        registerWhenButtonHandler();
        registerOkButtonHandler();
    }

    private void registerWhenButtonHandler() {
        Button whenButton = (Button) this.findViewById(R.id.whenButton);
        whenButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                // call the internal showDialog method using the predefined ID
                showDialog(WHENSELECTOR_ID);
            }
        });
    }

    private void scheduleKittyCycling() {
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                CreateRememberMeActivity.this.runOnUiThread(new Runnable() {

                    public void run() {
                        Random random = new Random(System.currentTimeMillis());
                        kitty.setImageResource(KITTY_IMAGES[random.nextInt(KITTY_IMAGES.length)]);

                    }
                });
            }

        }, DELAY_BEFORE_FIRST_KITTY_IN_MILLISECONDS, TIME_BEFORE_KITTY_IS_CHANGED_IN_MILLISECONDS);
    }

    private void registerOkButtonHandler() {
        Button okButton = (Button) this.findViewById(R.id.okButton);
        okButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                createRememberMe(view.getContext());
            }

            private void createRememberMe(Context context) {
                
                EditText editText = (EditText) CreateRememberMeActivity.this.findViewById(R.id.rememberMeToTextEdit);
                String remememberMeToText = editText.getText().toString();
                
                // get a Calendar object with current time
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.SECOND, 5);
                Intent intent = new Intent(context, RememberMeReceiver.class);
                intent.putExtra(RememberMeReceiver.REMEMBER_ME_TO_TEXT, remememberMeToText);
                // In reality, you would want to have a static variable for the
                // request code instead of 192837
                PendingIntent sender = PendingIntent.getBroadcast(context, NEW_REMEMBER_ME_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Get the AlarmManager service
                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

                Toast.makeText(context, "I will remember to remember, so you can forget, meow", Toast.LENGTH_LONG).show();
            }
        });
    }

    private DateSlider.OnDateSetListener mWhenSelectedListener = new DateSlider.OnDateSetListener() {
        public void onDateSet(DateSlider view, Calendar selectedDate) {
            // update the dateText view with the corresponding date
            dateText.setText(String.format("The chosen time:%n%tR", selectedDate));
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        // this method is called after invoking 'showDialog' for the first time
        // here we initiate the corresponding DateSlideSelector and return the
        // dialog to its caller

        final Calendar c = Calendar.getInstance();
        switch (id) {
        case WHENSELECTOR_ID:
            return new DateTimeSlider(this, mWhenSelectedListener, c);
        }
        return null;
    }
}