package com.shadhin.ahsanalqasas;

import static com.shadhin.ahsanalqasas.DbStore.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.view.MenuItem;
import android.widget.TextView;

public class Tafsir extends AppCompatActivity {
    TextView tafsirDetails;
    TextView tafsirText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tafsir);
        Toolbar tb = findViewById(R.id.appbar);
        setSupportActionBar(tb);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        tafsirDetails = findViewById(R.id.tafshir_details);
        tafsirText = findViewById(R.id.tafshir_text);
        int surah_id = intent.getIntExtra("sura", 0);
        int ayat_id = intent.getIntExtra("ayat",0);
        DbStore db = getDbStoreInstant();
        setTitle(db.getSuraName(surah_id)+" "+surah_id+":"+ayat_id);
        String afsir = db.getTafshir(surah_id,ayat_id);
        String textfir = db.getTafshirText(surah_id,ayat_id);
        Spanned result = HtmlCompat.fromHtml(afsir, HtmlCompat.FROM_HTML_MODE_LEGACY);
        tafsirText.setText(HtmlCompat.fromHtml(textfir, HtmlCompat.FROM_HTML_MODE_LEGACY));
        tafsirDetails.setText(result);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}