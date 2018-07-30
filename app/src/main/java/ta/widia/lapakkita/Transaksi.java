package ta.widia.lapakkita;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import ta.widia.lapakkita.adapter.TransaksiAdapter;
import ta.widia.lapakkita.model.ItemTransaksi;
import ta.widia.lapakkita.util.Config;
import ta.widia.lapakkita.util.Request;
import ta.widia.lapakkita.util.SessionManager;

public class Transaksi extends AppCompatActivity {

    private RecyclerView rc;
    private TransaksiAdapter adapter;
    private List<ItemTransaksi> itemList;
    private ProgressDialog pDialog;
    public String SERVER = Config.HOST+"lihat_transaksi.php";
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);

        ///
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        //kalau belum login
        if(!session.isLoggedIn()){
            finish();
            Toast.makeText(Transaksi.this, "Anda belum login!", Toast.LENGTH_SHORT).show();
        }
        ///

        getSupportActionBar().setTitle("Transaksi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rc = (RecyclerView) findViewById(R.id.recycler_view);

        itemList = new ArrayList<>();
        adapter = new TransaksiAdapter(Transaksi.this, itemList, new TransaksiAdapter.CardAdapterListener() {
            @Override
            public void onCardSelected(int position, String id_transaksi, String status) {
                if(status.contains("N") || status.contains("W")){
                    //masuk ke konfirmasi pembayaran
                    Intent intent = new Intent(Transaksi.this, KonfirmasiBayarActivity.class);
                    intent.putExtra("key_id_transaksi", id_transaksi);
                    startActivity(intent);
                }
                if(status.contains("S") || status.contains("X")){
                    //masuk ke konfirmasi barang
                    //Intent intent = new Intent(getActivity(), KonfirmasiBarangActivity.class);
                    //intent.putExtra("key_id_transaksi", id_transaksi);
                    //startActivity(intent);
                }
                if(status.contains("Y")){
                    Toast.makeText(Transaksi.this, "Anda sudah bayar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onButtonSelected(int position, String id_transaksi) {
                //buka printed invoice
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://203.153.21.11/app/lapakkita-widia/api/print/?id_transaksi="+id_transaksi));
                startActivity(browserIntent);
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(Transaksi.this);
        rc.setLayoutManager(mLayoutManager);
        rc.setItemAnimator(new DefaultItemAnimator());
        rc.setAdapter(adapter);

        HashMap<String, String> user = session.getUserDetails();
        //input data ke keranjang
        String id_pelanggan = user.get(SessionManager.KEY_ID_PELANGGAN);

        new getData(id_pelanggan).execute();

    }

    private class getData extends AsyncTask<Void,Void,String> {

        //variabel untuk tangkap data
        private int scs = 0;
        private String id_pelanggan;

        public getData(String id_pelanggan){
            this.id_pelanggan = id_pelanggan;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Transaksi.this);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(Void... params) {
            try{
                //susun parameter
                HashMap<String,String> detail = new HashMap<>();
                detail.put("id_pelanggan", id_pelanggan);

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
                            String id_transaksi = c.getString("id_transaksi");
                            String id_keranjang = c.getString("id_keranjang");
                            String id_pelanggan = c.getString("id_pelanggan");
                            String total_bayar = c.getString("total_bayar");
                            String waktu = c.getString("waktu");
                            String status = c.getString("status");

                            ItemTransaksi p = new ItemTransaksi();
                            p.setId_transaksi(id_transaksi);
                            p.setId_keranjang(id_keranjang);
                            p.setId_pelanggan(id_pelanggan);
                            p.setTotal_bayar(total_bayar);
                            p.setTimestamp(waktu);
                            p.setStatus(status);

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
