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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class flightInfoDisplayActivity extends AppCompatActivity {
    private class flightInfo{
        private String depAirport;      //dep fields for departure information, arr fields for arrival
        private String depGate;
        private String depTerminal;
        private String depDay;
        private String depTime;
        private String depEstimated;
        private String depWeather;

        private String arrAirport;
        private String arrGate;
        private String arrTerminal;
        private String arrDay;
        private String arrTime;
        private String arrEstimated;
        private String arrWeather;

        flightInfo(String scheduleJson, String statusJson){
            try {
                JSONObject scheduleMain = new JSONObject(scheduleJson);
                //JSONObject sub = main.getJSONObject("request");
                JSONArray scheduleSub = scheduleMain.getJSONArray("scheduledFlights");
                JSONObject scheduleSub2 = scheduleSub.getJSONObject(0);
                depAirport = scheduleSub2.getString("departureAirportFsCode");
                arrAirport = scheduleSub2.getString("arrivalAirportFsCode");
                depTerminal = scheduleSub2.getString("departureTerminal");
                arrTerminal = scheduleSub2.getString("arrivalTerminal");
                depTime = scheduleSub2.getString("departureTime");
                arrTime = scheduleSub2.getString("arrivalTime");
                Date depDate = new Date();
                Date arrDate = new Date();
                JSONObject statusMain = new JSONObject(statusJson);
                JSONArray statusSub = statusMain.getJSONArray("flightStatuses");
                JSONObject statusSub2 = statusSub.getJSONObject(0);
                JSONObject statusSub3 = statusSub2.getJSONObject("airportResources");
                JSONObject statusSub4 = statusSub2.getJSONObject("operationalTimes");
                JSONObject depEstTimeJ = statusSub4.getJSONObject("estimatedGateDeparture");
                String depEstTime = depEstTimeJ.getString("dateLocal");
                JSONObject arrEstTimeJ = statusSub4.getJSONObject("estimatedGateArrival");
                String arrEstTime = arrEstTimeJ.getString("dateLocal");
                depGate = statusSub3.getString("departureGate");
                arrGate = statusSub3.getString("arrivalGate");
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    depDate = dateFormat.parse(depTime);
                    arrDate = dateFormat.parse(arrTime);
                    Date depEst = dateFormat.parse(depEstTime);
                    Date arrEst = dateFormat.parse(arrEstTime);
                    depTime = new SimpleDateFormat("H:mm a").format(depDate);
                    arrTime = new SimpleDateFormat("H:mm a").format(arrDate);
                    depDay = new SimpleDateFormat("MM/dd/yyyy").format(depDate);
                    arrDay = new SimpleDateFormat("MM/dd/yyyy").format(arrDate);
                    depEstimated = new SimpleDateFormat("H:mm a").format(depEst);
                    arrEstimated = new SimpleDateFormat("H:mm a").format(arrEst);

                }
                catch(Exception e){
                    int test = 0;
                }


                //will do JSONObject sub = main.getJSONObject(name);, then form there do sub.getJsonString(name) to get the actual value and add them to their appropriate fields.
                //might have to use getString instead of getJsonString, will have to try it first
            }
            catch (JSONException e){
                int test = 0;
                // throw some sort of error
            }
        }
    }

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
    private flightInfo FlightInfo;

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
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String scheduleResult = "";
        String statusResult = "";
        if(bundle.containsKey("scheduleData")){
            scheduleResult = bundle.getString("scheduleData");
        }
        if(bundle.containsKey("statusData")){
            statusResult = bundle.getString("statusData");
        }
        FlightInfo = new flightInfo(scheduleResult, statusResult);
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
        mAirportText.setText(FlightInfo.depAirport);
        mGateText.setVisibility(View.VISIBLE);
        mGateText.setText(FlightInfo.depGate);
        mTerminalText.setVisibility(View.VISIBLE);
        mTerminalText.setText(FlightInfo.depTerminal);
        mScheduledText.setVisibility(View.VISIBLE);
        mScheduledText.setText(FlightInfo.depTime);
        mDateText.setVisibility(View.VISIBLE);
        mDateText.setText(FlightInfo.depDay);
        mEstimatedText.setVisibility(View.VISIBLE);
        mEstimatedText.setText(FlightInfo.depEstimated);
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
        mAirportText.setText(FlightInfo.arrAirport);
        mGateText.setVisibility(View.VISIBLE);
        mGateText.setText(FlightInfo.arrGate);
        mTerminalText.setVisibility(View.VISIBLE);
        mTerminalText.setText(FlightInfo.arrTerminal);
        mScheduledText.setVisibility(View.VISIBLE);
        mScheduledText.setText(FlightInfo.arrTime);
        mDateText.setVisibility(View.VISIBLE);
        mDateText.setText(FlightInfo.arrDay);
        mEstimatedText.setVisibility(View.VISIBLE);
        mEstimatedText.setText(FlightInfo.arrEstimated);
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
