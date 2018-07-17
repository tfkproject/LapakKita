package ta.widia.lapakkita;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ta.widia.lapakkita.adapter.DiskusiAdapter;
import ta.widia.lapakkita.adapter.TransaksiAdapter;
import ta.widia.lapakkita.model.ItemDiskusi;
import ta.widia.lapakkita.model.ItemTransaksi;
import ta.widia.lapakkita.util.Config;
import ta.widia.lapakkita.util.Request;
import ta.widia.lapakkita.util.SessionManager;

public class Diskusi extends AppCompatActivity {

    private RecyclerView rc;
    private DiskusiAdapter adapter;
    private List<ItemDiskusi> itemList;
    private ProgressDialog pDialog;
    public String SERVER = Config.HOST+"lihat_diskusi.php";
    public String url_post_komen = Config.HOST+"diskusi_post.php";
    SessionManager session;

    private EditText edtKomentar;
    private ImageButton btnSend;

    String id_produk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diskusi);

        ///
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        //kalau belum login
        if(!session.isLoggedIn()){
            finish();
            Toast.makeText(Diskusi.this, "Anda belum login!", Toast.LENGTH_SHORT).show();
        }
        ///

        getSupportActionBar().setTitle("Diskusi Produk");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rc = (RecyclerView) findViewById(R.id.recycler_view);

        itemList = new ArrayList<>();
        adapter = new DiskusiAdapter(Diskusi.this, itemList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(Diskusi.this);
        rc.setLayoutManager(mLayoutManager);
        rc.setItemAnimator(new DefaultItemAnimator());
        rc.setAdapter(adapter);

        HashMap<String, String> user = session.getUserDetails();
        //input data ke keranjang
        final String id_pelanggan = user.get(SessionManager.KEY_ID_PELANGGAN);

        id_produk = getIntent().getStringExtra("key_id_produk");

        new getData(id_produk).execute();

        edtKomentar = (EditText) findViewById(R.id.edt_komentar);

        btnSend = (ImageButton) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtKomentar.getText().toString().length() > 0){
                    String komentar = edtKomentar.getText().toString();
                    new postData(id_produk, id_pelanggan, komentar).execute();
                    edtKomentar.setText("");
                }
                else{
                    Toast.makeText(Diskusi.this, "Mohon isi komentar anda", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private class getData extends AsyncTask<Void,Void,String> {

        //variabel untuk tangkap data
        private int scs = 0;
        private String id_produk;

        public getData(String id_produk){
            this.id_produk = id_produk;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Diskusi.this);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(Void... params) {
            try{
                //susun parameter
                HashMap<String,String> detail = new HashMap<>();
                detail.put("id_produk", id_produk);

                try {
                    //convert this HashMap to encodedUrl to send to php file
                    String dataToSend = hashMapToUrl(detail);
                    //make a Http request and send data to saveImage.php file
                    String response = Request.post(SERVER,dataToSend);

                    //dapatkan respon
                    Log.e("Respon", response);

                    JSONObject ob = new JSONObject(response);
                    scs = ob.getInt("success");

                    if (scs == 1) {
                        JSONArray products = ob.getJSONArray("daftar");

                        for (int i = 0; i < products.length(); i++) {
                            JSONObject c = products.getJSONObject(i);

                            // Storing each json item in variable
                            String id_diskusi = c.getString("id_diskusi");
                            String nama_pelanggan = c.getString("nama_pelanggan");
                            String komentar = c.getString("komentar");
                            String waktu = c.getString("waktu");

                            ItemDiskusi p = new ItemDiskusi();
                            p.setId_diskusi(id_diskusi);
                            p.setNama_pelanggan(nama_pelanggan);
                            p.setKomentar(komentar);
                            p.setTimestamp(waktu);

                            itemList.add(p);

                        }
                    } else {
                        // no data found

                    }

                } catch (JSONException e){
                    e.printStackTrace();
                }

            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            adapter.notifyDataSetChanged();
            pDialog.dismiss();

        }

    }

    private class postData extends AsyncTask<Void,Void,String> {

        //variabel untuk tangkap data
        private int scs = 0;
        private String psn;
        private String id_produk, id_pelanggan, komentar;

        public postData(String id_produk, String id_pelanggan, String komentar){
            this.id_produk = id_produk;
            this.id_pelanggan = id_pelanggan;
            this.komentar = komentar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Diskusi.this);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(Void... params) {
            try{
                //susun parameter
                HashMap<String,String> detail = new HashMap<>();
                detail.put("id_produk", id_produk);
                detail.put("id_pelanggan", id_pelanggan);
                detail.put("komentar", komentar);

                try {
                    //convert this HashMap to encodedUrl to send to php file
                    String dataToSend = hashMapToUrl(detail);
                    //make a Http request and send data to saveImage.php file
                    String response = Request.post(url_post_komen,dataToSend);

                    //dapatkan respon
                    Log.e("Respon", response);

                    JSONObject ob = new JSONObject(response);
                    scs = ob.getInt("success");

                    if (scs == 1) {
                        psn = ob.getString("message");
                    } else {
                        psn = ob.getString("message");
                    }

                } catch (JSONException e){
                    e.printStackTrace();
                }

            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();

            itemList.clear();
            new getData(id_produk).execute();

        }

    }

    private String hashMapToUrl(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
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

        return super.onOptionsItemSelected(item);
    }

}
