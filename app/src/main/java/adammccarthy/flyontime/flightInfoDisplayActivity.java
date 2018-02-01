package adammccarthy.flyontime;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class flightInfoDisplayActivity extends AppCompatActivity {

    private TextView mAirportText;
    private TextView mGateText;
    private TextView mTerminalText;
    private TextView mDateText;
    private TextView mScheduledText;
    private TextView mEstimatedText;
    private TextView mWeatherText;
    private TextView mAirportLbl;
    private TextView mGateLbl;
    private TextView mTerminalLbl;
    private TextView mDateLbl;
    private TextView mScheduledLbl;
    private TextView mEstimatedLbl;
    private TextView mWeatherLbl;
    private Button mAirportDirBtn;
    private Button mTerminalDirBtn;
    private Button mParkingDirBtn;
    private CharSequence departureLoc;
    private CharSequence departureTerminal;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_directions:
                    setToDirections();
                    return true;
                case R.id.action_departure:
                   setToDeparture();
                    return true;
                case R.id.action_arrival:
                    setToArrival();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_info_display);
        mAirportText = (TextView) findViewById(R.id.airportText);
        mDateText = (TextView) findViewById(R.id.dateText);
        mGateText = (TextView) findViewById(R.id.gateText);
        mTerminalText = (TextView) findViewById(R.id.terminalText);
        mScheduledText = (TextView) findViewById(R.id.scheduledText);
        mEstimatedText = (TextView) findViewById(R.id.estimatedText);
        mWeatherText = (TextView) findViewById(R.id.weatherText);
        mAirportLbl = (TextView) findViewById(R.id.airportLabel);
        mDateLbl = (TextView) findViewById(R.id.dateLabel);
        mGateLbl = (TextView) findViewById(R.id.gateLabel);
        mTerminalLbl = (TextView) findViewById(R.id.terminalLabel);
        mScheduledLbl = (TextView) findViewById(R.id.scheduledLabel);
        mEstimatedLbl = (TextView) findViewById(R.id.estimatedLabel);
        mWeatherLbl = (TextView) findViewById(R.id.weatherLabel);
        mAirportDirBtn = (Button) findViewById(R.id.airport_directions);
        mTerminalDirBtn = (Button) findViewById(R.id.terminal_directions);
        mParkingDirBtn = (Button) findViewById(R.id.parking_directions);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mAirportDirBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+departureLoc));
                startActivity(intent);
            }
        });
        mParkingDirBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+departureLoc+"airport parking"));//this does the same thing as the regular directions, need to find a way to differentiate
                startActivity(intent);
            }
        });
        mTerminalDirBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+departureLoc+ " terminal "+departureTerminal));
                startActivity(intent);
            }
        });
        setToDeparture(); // have default screen be the departure information tab
        departureLoc = mAirportText.getText();
        departureTerminal = mTerminalText.getText();
    }
    protected void setToDeparture(){
        //change all text to departure information, change all of these when actually getting api calls
        mAirportDirBtn.setVisibility(View.INVISIBLE);
        mTerminalDirBtn.setVisibility(View.INVISIBLE);
        mParkingDirBtn.setVisibility(View.INVISIBLE);
        mAirportLbl.setVisibility(View.VISIBLE);
        mGateLbl.setVisibility(View.VISIBLE);
        mTerminalLbl.setVisibility(View.VISIBLE);
        mScheduledLbl.setVisibility(View.VISIBLE);
        mDateLbl.setVisibility(View.VISIBLE);
        mEstimatedLbl.setVisibility(View.VISIBLE);
        mWeatherLbl.setVisibility(View.VISIBLE);
        mAirportText.setVisibility(View.VISIBLE);
        mAirportText.setText("ATL");
        mGateText.setVisibility(View.VISIBLE);
        mGateText.setText("32");
        mTerminalText.setVisibility(View.VISIBLE);
        mTerminalText.setText("B");
        mScheduledText.setVisibility(View.VISIBLE);
        mScheduledText.setText("11:30 AM");
        mDateText.setVisibility(View.VISIBLE);
        mDateText.setText("1/31/2018");
        mEstimatedText.setVisibility(View.VISIBLE);
        mEstimatedText.setText("12:00 PM");
        mWeatherText.setVisibility(View.VISIBLE);
        mWeatherText.setText("Sunny, 85°F");
    }
    protected void setToArrival(){
        mAirportDirBtn.setVisibility(View.INVISIBLE);
        mTerminalDirBtn.setVisibility(View.INVISIBLE);
        mParkingDirBtn.setVisibility(View.INVISIBLE);
        mAirportLbl.setVisibility(View.VISIBLE);
        mGateLbl.setVisibility(View.VISIBLE);
        mTerminalLbl.setVisibility(View.VISIBLE);
        mScheduledLbl.setVisibility(View.VISIBLE);
        mDateLbl.setVisibility(View.VISIBLE);
        mEstimatedLbl.setVisibility(View.VISIBLE);
        mWeatherLbl.setVisibility(View.VISIBLE);
        mAirportText.setVisibility(View.VISIBLE);
        mAirportText.setText("DFW");
        mGateText.setVisibility(View.VISIBLE);
        mGateText.setText("12");
        mTerminalText.setVisibility(View.VISIBLE);
        mTerminalText.setText("A");
        mScheduledText.setVisibility(View.VISIBLE);
        mScheduledText.setText("1:30 PM");
        mDateText.setVisibility(View.VISIBLE);
        mDateText.setText("1/31/2018");
        mEstimatedText.setVisibility(View.VISIBLE);
        mEstimatedText.setText("2:00 PM");
        mWeatherText.setVisibility(View.VISIBLE);
        mWeatherText.setText("Rainy, 87°F");
    }
    protected void setToDirections(){
        //make directions buttons visible and remove everything else
        mAirportDirBtn.setVisibility(View.VISIBLE);
        mTerminalDirBtn.setVisibility(View.VISIBLE);
        mParkingDirBtn.setVisibility(View.VISIBLE);
        mAirportText.setVisibility(View.INVISIBLE);
        mGateText.setVisibility(View.INVISIBLE);
        mTerminalText.setVisibility(View.INVISIBLE);
        mScheduledText.setVisibility(View.INVISIBLE);
        mDateText.setVisibility(View.INVISIBLE);
        mEstimatedText.setVisibility(View.INVISIBLE);
        mWeatherText.setVisibility(View.INVISIBLE);
        mAirportLbl.setVisibility(View.INVISIBLE);
        mGateLbl.setVisibility(View.INVISIBLE);
        mTerminalLbl.setVisibility(View.INVISIBLE);
        mScheduledLbl.setVisibility(View.INVISIBLE);
        mDateLbl.setVisibility(View.INVISIBLE);
        mEstimatedLbl.setVisibility(View.INVISIBLE);
        mWeatherLbl.setVisibility(View.INVISIBLE);
    }


}
