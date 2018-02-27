package adammccarthy.flyontime;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
        new RetrieveWeatherTask().execute("ATL");
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
        new RetrieveWeatherTask().execute("DFW");
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

                String jsonString2 = jObject2.getString("tags");
                JSONArray jsonArray = new JSONArray(jsonString2);
                int conditionIndex = 0;
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject arrayObj = jsonArray.getJSONObject(i);
                    String toCheck = arrayObj.toString();
                    if(toCheck.contains("Prevailing Conditions")){
                        conditionIndex = i;
                        break;
                    }
                }

                JSONObject jObject3 = jsonArray.getJSONObject(conditionIndex);
                String last = jObject3.toString();
                int location = last.indexOf("\"value\":");
                location = location + 9;
                int length = last.length() - 2;
                String weather = last.substring(location, length);

                String jsonString4 = jObject2.getString("temperatureCelsius");
                //String tempString = jsonString4.substring(1, (jsonString4.length() - 1));
                double temp = Double.parseDouble(jsonString4) * (9/5) + 32;

                result.append(weather);
                result.append(" ");
                result.append(temp);
                result.append("\u00b0F");

                mWeatherText.setText(result);
            }
            catch(JSONException e){
                mWeatherText.setText("JSON failed");
            }
        }

    }
}


