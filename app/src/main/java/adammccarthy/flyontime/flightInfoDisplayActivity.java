package adammccarthy.flyontime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

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

    private Button mLateFlightAckBtn;

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
    //function from the original project to do main api call
//    public JsonResult getFlightSchedule(string airCode, string fn, string year, string month, string day)
//    {
//        string requestUrl = ApiKeys.fsScheduledFlightsByCarrierFNDate + airCode + "/" + fn + "/departing/" + year + "/" + month + "/" + day + "?appId=" + ApiKeys.fsAppID + "&appKey=+" + ApiKeys.fsAppKey;
//
//        WebClient client = new WebClient();
//        string information = client.DownloadString(requestUrl);
//
//        return Json(information, JsonRequestBehavior.AllowGet);
//    }

    //below is example from the internet on api calls in android, need to convert above to this
//     try {
//        URL url = new URL(API_URL + "email=" + email + "&apiKey=" + API_KEY);
//        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//        try {
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//            StringBuilder stringBuilder = new StringBuilder();
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                stringBuilder.append(line).append("\n");
//            }
//            bufferedReader.close();
//            return stringBuilder.toString();
//        }
//        finally{
//            urlConnection.disconnect();
//        }
//    }
//            catch(Exception e) {
//        Log.e("ERROR", e.getMessage(), e);
//        return null;
//    }
//}
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

        mLateFlightAckBtn = (Button) findViewById(R.id.late_flight_ack);

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
        mLateFlightAckBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(flightInfoDisplayActivity.this);
                alertDialogBuilder.setTitle("Warning");
                alertDialogBuilder.setMessage("You are late for your flight!");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialogBuilder.setPositiveButton("Acknowledge", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with discard
                        Toast.makeText(flightInfoDisplayActivity.this, "Acknowledge", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        Toast.makeText(flightInfoDisplayActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialogBuilder.show();
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

        //Need to discus
        //google maps api call example
        //https://maps.googleapis.com/maps/api/distancematrix/json?
        // origins=Vancouver+BC|Seattle
        // &destinations=San+Francisco|Victoria+BC
        // &mode=bicycling&language=fr-FR
        // &key=YOUR_API_KEY
        //Suggestion: Use resulting JSON to extract trip duration in seconds, convert to milliseconds
        //  add to System.currentTimeMillis and compare to ETA
        if(System.currentTimeMillis() < System.currentTimeMillis() + 10000000) {
            mLateFlightAckBtn.setVisibility(View.VISIBLE);
        }
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
        mLateFlightAckBtn.setVisibility(View.INVISIBLE);
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
        mLateFlightAckBtn.setVisibility(View.INVISIBLE);
    }

    /*
    //added by morgan 2/14/2018
    //flight information pertaining to time of flight
    //time of departure
    //time of boarding
    //time of arrival
    //estimated time of passenger arrival to gate/boarding
    //
    //hard coded for testing
    //{
    private double timeOfDeparture;
    private double timeOfBoarding;
    private double timeOfArrival;
    private double estTimeOfPassengerArrival;

    private flightInfoDisplayActivity(double tod, double tob, double toa, double eta) {
        timeOfDeparture = tod;
        timeOfBoarding = tob;
        timeOfArrival= toa;
        estTimeOfPassengerArrival = eta;
    }

    //Added 2/28/18 - Morgan Beaty
    //Functions related to flight info object
    //allow for get/set of object variables
    //compares eta to actual arrival/boarding times

    //Methods for getting flight time information
    double newBT = System.currentTimeMillis() + 750000000;
    double newAT = System.currentTimeMillis() + 835323989;
    double newDT = System.currentTimeMillis() + 1000000000;
    double newETA = System.currentTimeMillis() + 850000000;
    /*
    flightInfoDisplayActivity fida = new flightInfoDisplayActivity(newDT, newBT, newAT, newETA);

    protected double getTimeOfBoarding() {
        return this.timeOfBoarding;
    }

    protected double getTimeOfDeparture() {
        return this.timeOfDeparture;
    }

    protected double getTimeofArrival() {
        return this.timeOfArrival;
    }

    protected double getEstTimeOfPassengerArrival() {
        return this.estTimeOfPassengerArrival;
    }

    //Methods for adjusting flight time information
    private void setTimeOfBoarding(double nBoardingTime) {
        this.timeOfBoarding = nBoardingTime;
    }

    private void setTimeOfArrival(double nArrivalTime) {
        this.timeOfArrival = nArrivalTime;
    }

    private void setTimeOfDeparture(double nDepartureTime) {
        this.timeOfDeparture = nDepartureTime;
    }

    private void setEstimatedPassengerArrivalTime(double nEstPassengerArrivalTime) {
        this.estTimeOfPassengerArrival = nEstPassengerArrivalTime;
    }
    /*
    //Method for checking for estimated passenger arrival time > boarding time
    //+ changing color of appropriate ui element color
    protected void estTimeVsBoardingTime() {
        setContentView(R.layout.activity_flight_info_display);

        if (getEstTimeOfPassengerArrival() > getTimeOfBoarding()) {
            TextView textElement = (TextView) findViewById(R.id.estimatedText);
            textElement.setTextColor(0xFF000000);
        } else {
            //fida.showLateFlightAck();
            TextView textElement = (TextView) findViewById(R.id.estimatedText);
            textElement.setTextColor(0xFF00FF00);
        }
    }

    //}

    */

}
