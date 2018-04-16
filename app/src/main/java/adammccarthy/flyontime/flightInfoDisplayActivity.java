package adammccarthy.flyontime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
    private CheckBox mLayoverDisplayCheckBox;

    private Button mLateFlightAckBtn;

    private CharSequence departureLoc;
    private CharSequence departureTerminal;
    private flightInfo FlightInfo;
    private flightInfo FlightInfoLayover;
    private boolean isLayover;

    private final String TAG = "Main Fly On Time Activity";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_directions:
                    setToDirections();
                    return true;
                case R.id.action_departure:
                    if(isLayover && mLayoverDisplayCheckBox.isChecked()){
                        setToLayoverDeparture();
                    }
                    else{
                        setToDeparture();
                    }
                    return true;
                case R.id.action_arrival:
                    if(isLayover && mLayoverDisplayCheckBox.isChecked()){
                        setToLayoverArrival();
                    }
                    else {
                        setToArrival();
                    }
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
        String scheduleResultLayover = "";
        String statusResultLayover = "";
        isLayover = false;
        if(bundle.containsKey("scheduleData")){
            scheduleResult = bundle.getString("scheduleData");
        }
        if(bundle.containsKey("statusData")){
            statusResult = bundle.getString("statusData");
        }
        if(bundle.containsKey("scheduleLayoverData")){
            scheduleResultLayover = bundle.getString("scheduleLayoverData");
            isLayover = true;
        }
        if(bundle.containsKey("statusLayoverData")){
            statusResultLayover = bundle.getString("statusLayoverData");
            isLayover = true;
        }
        FlightInfo = new flightInfo(scheduleResult, statusResult);
        if(!(scheduleResultLayover.isEmpty()) && !(statusResultLayover.isEmpty())){
            FlightInfoLayover = new flightInfo(scheduleResultLayover, statusResultLayover);
            //mLayoverDisplayCheckBox.setVisibility(View.VISIBLE);
        }

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
        mLayoverDisplayCheckBox = (CheckBox) findViewById(R.id.layoverDisplayCheckBox);
        mLateFlightAckBtn = (Button) findViewById(R.id.late_flight_ack);

        if(isLayover){
            mLayoverDisplayCheckBox.setVisibility(View.VISIBLE);
        }

        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
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

        mLayoverDisplayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(mLayoverDisplayCheckBox.isChecked() && navigation.getSelectedItemId() == R.id.action_departure){
                    setToLayoverDeparture();
                }
                else if(mLayoverDisplayCheckBox.isChecked() && navigation.getSelectedItemId() == R.id.action_arrival){
                    setToLayoverArrival();
                }
                else if(!mLayoverDisplayCheckBox.isChecked() && navigation.getSelectedItemId() == R.id.action_departure){
                    setToDeparture();
                }
                else if(!mLayoverDisplayCheckBox.isChecked() && navigation.getSelectedItemId() == R.id.action_arrival){
                    setToArrival();
                }
            }
        });



        setToDeparture(); // have default screen be the departure information tab
        navigation.setSelectedItemId(R.id.action_departure);
        departureLoc = mAirportText.getText();
        departureTerminal = mTerminalText.getText();

        practiceDatabase();
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
        if(isLayover){
            mLayoverDisplayCheckBox.setVisibility(View.VISIBLE);
        }



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

        new RetrieveWeatherTask().execute(FlightInfo.depAirport);
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
        mLateFlightAckBtn.setVisibility(View.INVISIBLE);
        new RetrieveWeatherTask().execute(FlightInfo.arrAirport);
        if(isLayover){
            mLayoverDisplayCheckBox.setVisibility(View.VISIBLE);
        }
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
        mLayoverDisplayCheckBox.setVisibility(View.INVISIBLE);
    }

    protected void setToLayoverDeparture(){
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
        mAirportText.setText(FlightInfoLayover.depAirport);
        mGateText.setVisibility(View.VISIBLE);
        mGateText.setText(FlightInfoLayover.depGate);
        mTerminalText.setVisibility(View.VISIBLE);
        mTerminalText.setText(FlightInfoLayover.depTerminal);
        mScheduledText.setVisibility(View.VISIBLE);
        mScheduledText.setText(FlightInfoLayover.depTime);
        mDateText.setVisibility(View.VISIBLE);
        mDateText.setText(FlightInfoLayover.depDay);
        mEstimatedText.setVisibility(View.VISIBLE);
        mEstimatedText.setText(FlightInfoLayover.depEstimated);
        mWeatherText.setVisibility(View.VISIBLE);

        if(System.currentTimeMillis() < System.currentTimeMillis() + 10000000) {
            mLateFlightAckBtn.setVisibility(View.VISIBLE);
        }

        new RetrieveWeatherTask().execute(FlightInfoLayover.depAirport);
        mLayoverDisplayCheckBox.setVisibility(View.VISIBLE);
    }
    protected void setToLayoverArrival(){
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
        mAirportText.setText(FlightInfoLayover.arrAirport);
        mGateText.setVisibility(View.VISIBLE);
        mGateText.setText(FlightInfoLayover.arrGate);
        mTerminalText.setVisibility(View.VISIBLE);
        mTerminalText.setText(FlightInfoLayover.arrTerminal);
        mScheduledText.setVisibility(View.VISIBLE);
        mScheduledText.setText(FlightInfoLayover.arrTime);
        mDateText.setVisibility(View.VISIBLE);
        mDateText.setText(FlightInfoLayover.arrDay);
        mEstimatedText.setVisibility(View.VISIBLE);
        mEstimatedText.setText(FlightInfoLayover.arrEstimated);
        mWeatherText.setVisibility(View.VISIBLE);
        mLateFlightAckBtn.setVisibility(View.INVISIBLE);
        mLayoverDisplayCheckBox.setVisibility(View.VISIBLE);
        new RetrieveWeatherTask().execute(FlightInfoLayover.arrAirport);
    }

    class RetrieveWeatherTask extends AsyncTask<String, Void, String> {

        protected String weatherResult = "";

        @Override
        protected String doInBackground(String... airportCode) {
            try {
                URL url = new URL(getString(R.string.fsWeather) + airportCode[0] + "?appId=" + getString(R.string.fsAppID) + "&appKey=+" + getString(R.string.fsAppKey));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                return "failed";
            }
        }



        protected void onPostExecute(String weatherResult){
            try {
                JSONObject jObject = new JSONObject(weatherResult);
                StringBuilder result = new StringBuilder();
                String jsonString1 = jObject.getString("metar");
                JSONObject jObject2 = new JSONObject(jsonString1);

                String jsonString2=jObject2.getString("tags");
                JSONArray jsonArray=new JSONArray(jsonString2);
                int conditionIndex=0;
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject arrayObj=jsonArray.getJSONObject(i);
                    String toCheck=arrayObj.toString();
                    if(toCheck.contains("Prevailing Conditions")){
                        conditionIndex=i;
                        break;
                    }
                }

                JSONObject jObject3=jsonArray.getJSONObject(conditionIndex);
                String last=jObject3.toString();
                int location=last.indexOf("\"value\":");
                location=location+9;
                int length=last.length()-2;
                String weather=last.substring(location,length);

                String jsonString4 = jObject2.getString("temperatureCelsius");
                //String tempString = jsonString4.substring(1, (jsonString4.length() - 1));
                double tempC = Double.parseDouble(jsonString4);
                double tempF = tempC * 1.8 + 32;

                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.HALF_UP);

                result.append(weather);
                result.append(" ");
                result.append(df.format(tempF));
                result.append("\u00b0F");

                mWeatherText.setText(result);
            }
            catch(JSONException e){
                mWeatherText.setText("JSON failed");
            }
        }


    }

    private void practiceDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ATL");

        myRef.setValue("20 minutes");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}