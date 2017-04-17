package ie.sheehan.smarthome;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import ie.sheehan.smarthome.model.IntrusionReading;
import ie.sheehan.smarthome.utility.DateUtility;

public class IntrusionViewActivity extends AppCompatActivity {

    ImageView imageView;
    TextView dateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intrusion_view);

        imageView = (ImageView) findViewById(R.id.image_intrusion);
        dateView = (TextView) findViewById(R.id.text_label_intrusion_date);

        Bundle arguments = getIntent().getExtras();

        IntrusionReading intrusionReading = (IntrusionReading) arguments.getSerializable("intrusion");

        if (intrusionReading == null) {
            Toast.makeText(this, R.string.toast_no_intrusion_selected, Toast.LENGTH_SHORT).show();
            return;
        }

        String dateText = DateUtility.getDateFormat().format(new Date(intrusionReading.timestamp * 1000L));
        dateView.setText(String.format(getResources().getString(R.string.text_label_intrusion_date), dateText));

        byte[] data = Base64.decode(intrusionReading.image, Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);

        imageView.setImageBitmap(image);
        imageView.refreshDrawableState();
    }

}
