package pecia.socialmap;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

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
        EditText pos = (EditText) findViewById(R.id.editText4);

        String titolo = ti.getText().toString();
        String post = pos.getText().toString();

        if (titolo.matches("")) {
            Toast.makeText(this, "Inserisci titolo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (post.matches("")) {
            Toast.makeText(this, "Inserisci messaggio", Toast.LENGTH_SHORT).show();
            return;
        }

        NewPost newPost = new NewPost(titolo,post,"UTENTE!");


        Main3Activity.locationManager.removeUpdates(Main3Activity.locationListener);
        Main3Activity.delete.finish();

        Intent intent = new Intent(this, Main3Activity.class);
        intent.putExtra("bool", "1");
        intent.putExtra("Post",newPost);
        startActivity(intent);

        this.finish();
    }
}
