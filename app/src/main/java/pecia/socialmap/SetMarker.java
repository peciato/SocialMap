package pecia.socialmap;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class SetMarker extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_marker);

        setWindowParams();
    }

    public void setWindowParams(){
        WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.dimAmount = 0;
        wlp.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        getWindow().setAttributes(wlp);
    }

    public void submit(View view){
        EditText ti = (EditText) findViewById(R.id.editText3);
        EditText con = (EditText) findViewById(R.id.editText4);

        String pin[] = new String[2];

        pin[0] = ti.getText().toString();
        pin[1] = con.getText().toString();

        ((MyApplication) this.getApplication()).putValue(pin);


        Main3Activity.locationManager.removeUpdates(Main3Activity.locationListener);
        Main3Activity.delete.finish();

        Intent intent = new Intent(this, Main3Activity.class);
        intent.putExtra("bool", "1");
        startActivity(intent);

        this.finish();
    }
}
