package ta.widia.lapakkita;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ta.widia.lapakkita.fragment.Beranda;
import ta.widia.lapakkita.fragment.Umkm;
import ta.widia.lapakkita.model.ItemKategori;
import ta.widia.lapakkita.util.Config;
import ta.widia.lapakkita.util.Request;

public class UkmDetail extends AppCompatActivity {

    ImageView logo;
    TextView txtNama, txtAlamat;
    Button btnTelp, btnDirek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.umkm_detail);

        final String nama = getIntent().getStringExtra("key_nama");
        String alamat = getIntent().getStringExtra("key_alamat");
        String gambar = getIntent().getStringExtra("key_gambar");
        final String hp = getIntent().getStringExtra("key_hp");
        final String lat = getIntent().getStringExtra("key_lat");
        final String lon = getIntent().getStringExtra("key_lon");

        getSupportActionBar().setTitle(nama);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        logo = (ImageView) findViewById(R.id.img_logo);
        txtNama = (TextView) findViewById(R.id.txt_nama);
        txtAlamat = (TextView) findViewById(R.id.txt_alamat);

        btnTelp = (Button) findViewById(R.id.btn_telp);
        btnDirek = (Button) findViewById(R.id.btn_direk);

        Glide.with(UkmDetail.this).load(gambar).into(logo);
        txtNama.setText(nama);
        txtAlamat.setText(alamat);
        btnTelp.setText(hp);

        btnTelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+hp));
                startActivity(intent);
            }
        });

        btnDirek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UkmDetail.this, DirectionMap.class);
                intent.putExtra("key_nama_tujuan", nama);
                intent.putExtra("key_lat_tujuan", lat);
                intent.putExtra("key_long_tujuan", lon);
                startActivity(intent);
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        /*else if (id == R.id.action_keluar){
            finish();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

}
