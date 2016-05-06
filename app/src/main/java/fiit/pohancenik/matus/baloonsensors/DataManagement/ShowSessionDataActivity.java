package fiit.pohancenik.matus.baloonsensors.DataManagement;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import fiit.pohancenik.matus.baloonsensors.R;

import java.io.IOException;
import java.util.List;

/**
 * Created by matus on 7. 4. 2016.
 */
public class ShowSessionDataActivity extends Activity implements MyDialogFragmentListener {

    private String FILEPATH;
    private FileManager FM;
    private boolean[] checkedItems;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show_session_data);
        FM = new FileManager(getApplicationContext());
        Bundle bundle = getIntent().getExtras();
        FILEPATH = bundle.getString("filepath");
        //   db = new DatabaseHandler(this);
        checkedItems = new boolean[]{true,true,true,false,false,false};

        TextView t =(TextView) findViewById(R.id.Title_text_show_session_data);
        t.setText(bundle.getString("name"));
        ImageButton im = (ImageButton) findViewById(R.id.imageSettings);
        im.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog();

            }
        });






    }


    protected void onResume() {
        super.onResume();

        readData(FILEPATH);
    }



    public void readData(String filepath){
        MyAsyncTask asyncTask =new MyAsyncTask(this);
        asyncTask.execute(filepath);
    }


    void showDialog() {

        DialogFragment newFragment = CheckBoxAlertDialogFragment.newInstance(checkedItems);
        newFragment.show(getFragmentManager(), "dialog");


    }
    @Override
    public void onReturnValue(boolean[] checkedItems) {
        this.checkedItems = checkedItems;
        readData(FILEPATH);
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Integer> {


        private Activity activity;
        private ProgressDialog dialog;
        private Context context;
        private List<String[]> data;


        public MyAsyncTask(Activity activity) {
            this.activity = activity;
            this.context = activity;
            this.dialog = new ProgressDialog(activity,  R.style.AlertDialogCustom);
            this.dialog.setTitle("Loading");
            this.dialog.setMessage("Loading data from file ...");
            if (!this.dialog.isShowing()) {
                this.dialog.show();
            }
        }


        @Override
        protected Integer doInBackground(String... params) {

            try {
                data = FM.readCSVFile(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }


        @Override
        public void onPostExecute(Integer result) {
            TextView t = (TextView) findViewById(R.id.data_text_show_session_data);
            t.setText("");

            for (String[] s : data) {

                if (checkedItems[(Integer.parseInt(s[0])) - 1]) {
                    t.append(SensorType.values()[(Integer.parseInt(s[0])) - 1].toString() + " : ");
                    t.append(s[1] + "\n");
                }


            }
            this.dialog.dismiss();

        }

    }
}

