package adammccarthy.flyontime;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;


public class FlightInfo extends AppCompatActivity implements LoaderCallbacks<Cursor> {


    public String scheduleResult = "";
    public String statusResult = "";
    public boolean success1 = false;
    public boolean success2 = false;




    // UI references.
    private AutoCompleteTextView mAirlineCodeView;
    private EditText mFlightNumberView;
    private EditText mDepartDateView;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox mLayoverCheckBox;
    private LinearLayout mLayoverInfoView;
    private boolean mIsLayover;
    private AutoCompleteTextView mLayoverAirlineCodeView;
    private EditText mLayoverFlightNumberView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_info);
        // Set up the login form.
        mAirlineCodeView = (AutoCompleteTextView) findViewById(R.id.airline_code);
        mLayoverAirlineCodeView = (AutoCompleteTextView) findViewById(R.id.layover_airline_code);


        mLayoverInfoView = (LinearLayout) findViewById(R.id.flight_layover_info_form);
        mLayoverInfoView.setVisibility(View.INVISIBLE);
        mIsLayover = false;
        mLayoverCheckBox = (CheckBox) findViewById(R.id.layover_checkbox);

        mLayoverCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    mLayoverInfoView.setVisibility(View.VISIBLE);
                    mIsLayover = true;
                }
                else {
                    mLayoverInfoView.setVisibility(View.INVISIBLE);
                    mIsLayover = false;
                }
            }
        });

        mLayoverFlightNumberView = (EditText) findViewById(R.id.layover_flight_number);
        mFlightNumberView = (EditText) findViewById(R.id.flight_number);
        mFlightNumberView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptGetFlightInfo();
                    return true;
                }
                return false;
            }
        });
        mDepartDateView = (EditText) findViewById(R.id.depart_date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        mDepartDateView.setText(dateFormat.format(new Date()));

        Button mGetFlightInfoButton = (Button) findViewById(R.id.getFlightInfo);
        mGetFlightInfoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptGetFlightInfo();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.flight_info_form);
    }






    //need to find a way to pass multiple parameters to this, also need to parse date when getting. API call works need to find a way to parse out relevant info and pass to next activity
class FlightScheduleRetreiver extends AsyncTask<String, Void, String>{
        private String AirlineCode;
        private String FlightNumber;
        private Date DepartureDate;
        private Calendar calendar;
        //private String DepartureDate;

    FlightScheduleRetreiver(String airlineCode, String flightNum, Date date){
        AirlineCode = airlineCode;
        FlightNumber = flightNum;
        DepartureDate = date;
        calendar = Calendar.getInstance();
        calendar.setTime(DepartureDate);

    }
    protected String doInBackground(String... departDate){
        try {
            URL url = new URL(getResources().getString(R.string.fsScheduledFlightsByCarrierFNDate) + AirlineCode + "/" + FlightNumber + "/departing/" + calendar.get(calendar.YEAR) + "/"+(calendar.get(calendar.MONTH)+1)+"/"+ calendar.get(calendar.DAY_OF_MONTH) + "?appId=" + getResources().getString(R.string.fsAppID) + "&appKey=+" + getResources().getString(R.string.fsAppKey));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                scheduleResult = stringBuilder.toString();
                success1 = true;
                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        }
        catch(Exception e){
            success1 = false;
        }
        return "";
    }
        @Override
        protected void onPostExecute(String result){
            success1 = true;
            scheduleResult = result;
    }
}
    class FlightStatusRetreiver extends AsyncTask<String, Void, String>{
        private String AirlineCode;
        private String FlightNumber;
        private Date DepartureDate;
        private Calendar calendar;
        //private String DepartureDate;

        FlightStatusRetreiver(String airlineCode, String flightNum, Date date){
            AirlineCode = airlineCode;
            FlightNumber = flightNum;
            DepartureDate = date;
            calendar = Calendar.getInstance();
            calendar.setTime(DepartureDate);

        }
        protected String doInBackground(String... departDate){
            try {
                URL url = new URL(getResources().getString(R.string.fsFlightStatusByArrivalDate) + AirlineCode + "/" + FlightNumber + "/arr/" + calendar.get(calendar.YEAR) + "/"+(calendar.get(calendar.MONTH)+1)+"/"+ calendar.get(calendar.DAY_OF_MONTH) + "?appId=" + getResources().getString(R.string.fsAppID) + "&appKey=+" + getResources().getString(R.string.fsAppKey)+"&utc=true&airport=");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    statusResult = stringBuilder.toString();
                    success2 = true;
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            }
            catch(Exception e){
                success2 = false;
            }
            return "";
        }
        @Override
        protected void onPostExecute(String result){

            success2 = true;
            statusResult = result;

        }
    }


    private void attemptGetFlightInfo() {


        // Reset errors.
        mAirlineCodeView.setError(null);
        mFlightNumberView.setError(null);
        String airlineCode = mAirlineCodeView.getText().toString();
        String flightNumber = mFlightNumberView.getText().toString();
        String departureDate = mDepartDateView.getText().toString();
        Date d = new Date();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:sss");
            d = dateFormat.parse(departureDate);
        }
        catch(Exception e){

        }
        //  once making API calls add data verification in here
        FlightScheduleRetreiver scheduleRetreiver = new FlightScheduleRetreiver(airlineCode, flightNumber, d);
        scheduleRetreiver.execute(airlineCode, flightNumber);
        FlightStatusRetreiver statusRetreiver = new FlightStatusRetreiver(airlineCode, flightNumber, d);
        statusRetreiver.execute(airlineCode, flightNumber);





        boolean waiting = true;
        View focusView = null;
        while(waiting){
            if(success1 == false || success2 == false) {
                continue;
            }
            else {
                waiting = false;
                Intent intent = new Intent(this, flightInfoDisplayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("scheduleData", scheduleResult);
                bundle.putString("statusData", statusResult);
                if(mIsLayover){
                    layoverCalls();
                    bundle.putString("scheduleLayoverData", scheduleResult);
                    bundle.putString("statusLayoverData", statusResult);
                }
                intent.putExtras(bundle);
                startActivity(intent);//launch activity to display data
            }
        }



    }

    private void layoverCalls(){
        mLayoverAirlineCodeView.setError(null);
        mLayoverFlightNumberView.setError(null);
        //string requestUrl = ApiKeys.fsScheduledFlightsByCarrierFNDate + airCode + "/" + fn + "/departing/" + year + "/" + month + "/" + day + "?appId=" + ApiKeys.fsAppID + "&appKey=+" + ApiKeys.fsAppKey;
        // Store values at the time of the login attempt.
        String airlineLayoverCode = mLayoverAirlineCodeView.getText().toString();
        String flightLayoverNumber = mLayoverFlightNumberView.getText().toString();
        String departureLayoverDate = mDepartDateView.getText().toString();
        Date d = new Date();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:sss");
            d = dateFormat.parse(departureLayoverDate);
        }
        catch(Exception e){
            //need to have some sort error correction in here for the date parsing
        }

        scheduleResult = "";
        statusResult = "";
        success1 = false;
        success2 = false;
        //  once making API calls add data verification in here
        FlightScheduleRetreiver scheduleRetreiverLayover = new FlightScheduleRetreiver(airlineLayoverCode, flightLayoverNumber, d);
        scheduleRetreiverLayover.execute(airlineLayoverCode, flightLayoverNumber);
        FlightStatusRetreiver statusRetreiverLayover = new FlightStatusRetreiver(airlineLayoverCode, flightLayoverNumber, d);
        statusRetreiverLayover.execute(airlineLayoverCode, flightLayoverNumber);

        boolean waitingLayover = true;
        View focusView = null;
        while(waitingLayover){
            if(success1 == false || success2 == false) {
                continue;
            }
            else {
                waitingLayover = false;
                return;
            }
        }
    }



    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(FlightInfo.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mAirlineCodeView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }






}

