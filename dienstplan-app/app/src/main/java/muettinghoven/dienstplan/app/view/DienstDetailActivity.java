package muettinghoven.dienstplan.app.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import muettinghoven.dienstplan.app.model.DienstAusfuehrung;

public class DienstDetailActivity extends AppCompatActivity {

    public static final String DIENST_AUSFUEHRUNG = DienstAusfuehrung.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dienst_detail);

        final DienstAusfuehrung dienst = (DienstAusfuehrung) getIntent().getSerializableExtra(DIENST_AUSFUEHRUNG);

        final TextView bewohnerNameTextView = (TextView) findViewById(R.id.bewohnerNameTextView);
        bewohnerNameTextView.setText(dienst.getBewohner());

        final TextView dienstNameTextView = (TextView) findViewById(R.id.dienstNameTextView);
        dienstNameTextView.setText(dienst.getDienst());

        final TextView dienstBeschreibungTextView = (TextView) findViewById(R.id.dienstBeschreigungTextView);
        dienstBeschreibungTextView.setText(dienst.getDienstBeschreibung());

        final TextView zeitraumTextView = (TextView) findViewById(R.id.zeitraumTextView);
        zeitraumTextView.setText(dienst.getZeitraum());

        final EditText kommentarEditText = (EditText) findViewById(R.id.kommentarEditText);
        kommentarEditText.setText(dienst.getKommentar());
    }
}
