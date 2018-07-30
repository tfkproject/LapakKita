package ta.widia.lapakkita;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.TextView;
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

import ta.widia.lapakkita.adapter.KeranjangAdapter;
import ta.widia.lapakkita.model.ItemKeranjang;
import ta.widia.lapakkita.util.Config;
import ta.widia.lapakkita.util.Request;
import ta.widia.lapakkita.util.SessionManager;

public class KeranjangActivity extends AppCompatActivity {

    private RecyclerView rc;
    private KeranjangAdapter adapter;
    private List<ItemKeranjang> itemList;
    private ProgressDialog pDialog;

    public String SERVER = Config.HOST+"lihat_keranjang.php";
    public String SERVER_REMOVE = Config.HOST+"hapus_dari_keranjang.php";
    public String SERVER_SUM = Config.HOST+"sum_keranjang.php";

    SessionManager session;

    private static final String TAG = KeranjangActivity.class.getSimpleName();

    TextView textTotal,textId_keranjang;
    Button btnBayar;
    private String id_keranjang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keranjang);

        ///
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        //kalau belum login
        if(!session.isLoggedIn()){
            finish();
            Toast.makeText(KeranjangActivity.this, "Anda belum login!", Toast.LENGTH_SHORT).show();
        }
        ///

        textId_keranjang = (TextView) findViewById(R.id.txt_id_keranjang);
        textTotal = (TextView) findViewById(R.id.txt_total);
        btnBayar = (Button) findViewById(R.id.btn_bayar);

        rc = (RecyclerView) findViewById(R.id.recycler_view);

        itemList = new ArrayList<>();
        adapter = new KeranjangAdapter(getApplicationContext(), itemList, new KeranjangAdapter.CardAdapterListener() {
            @Override
            public void onButtonSelected(int position, String id_item_beli) {
                //delete item keranjang
                new removeData(id_item_beli).execute();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rc.setLayoutManager(mLayoutManager);
        rc.setItemAnimator(new DefaultItemAnimator());
        rc.setAdapter(adapter);

        HashMap<String, String> user = session.getUserDetails();
        //input data ke keranjang
        String id_pelanggan = user.get(SessionManager.KEY_ID_PELANGGAN);
        String nama_pelanggan = user.get(SessionManager.KEY_NM_PELANGGAN);

        getSupportActionBar().setTitle("Kerajang");
        getSupportActionBar().setSubtitle(nama_pelanggan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new getData(id_pelanggan).execute();

        new getSum(id_pelanggan).execute();

    }

    private class removeData extends AsyncTask<Void,Void,String> {
        private String id_item_beli;

        public removeData(String id_item_beli){
            this.id_item_beli = id_item_beli;
        }

        private String scs = "";
        private String psn = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(KeranjangActivity.this);
            pDialog.setMessage("Loading..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                //menganbil data-data yang akan dikirim

                //generate hashMap to store encodedImage and the name
                HashMap<String,String> detail = new HashMap<>();
                detail.put("id_item_beli", id_item_beli);

                try{
                    //convert this HashMap to encodedUrl to send to php file
                    String dataToSend = hashMapToUrl(detail);
                    //make a Http request and send data to php file
                    String response = Request.post(SERVER_REMOVE,dataToSend);

                    //dapatkan respon
                    Log.e("Respon", response);

                    JSONObject ob = new JSONObject(response);
                    scs = ob.getString("success");
                    psn = ob.getString("message");

                }catch (JSONException e){
                    e.printStackTrace();
                    Log.e(TAG, "ERROR  " + e);
                    Toast.makeText(getApplicationContext(),"Maaf, terjadi error!",Toast.LENGTH_SHORT).show();
                    //return null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }



        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();

            if(scs.contains("1")){
                //reload activity
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
            }
            if(scs.contains("0")){
                Toast.makeText(getApplicationContext(), psn,Toast.LENGTH_SHORT).show();
            }

        }

    }

    /////////
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
            pDialog = new ProgressDialog(KeranjangActivity.this);
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
                            String id_item_beli = c.getString("id_item_beli");
                            id_keranjang = c.getString("id_keranjang");
                            String nama_produk = c.getString("nama_produk");
                            String nama_umkm = c.getString("nama_umkm");
                            String jumlah = c.getString("jumlah");
                            String harga = c.getString("harga");
                            String gambar = c.getString("gambar");

                            ItemKeranjang p = new ItemKeranjang();
                            p.setId_item_beli(id_item_beli);
                            //p.setId_keranjang(id_keranjang);
                            p.setId_item_beli(id_item_beli);
                            p.setId_keranjang(id_keranjang);
                            p.setNama_produk(nama_produk);
                            p.setNama_umkm(nama_umkm);
                            p.setJumlah(jumlah);
                            p.setHarga(harga);
                            p.setGambar(gambar);

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
            textId_keranjang.setText("ID Keranjang: "+id_keranjang);
            pDialog.dismiss();

        }

    }

    private class getSum extends AsyncTask<Void,Void,String> {

        //variabel untuk tangkap data
        private int scs = 0;
        private String id_pelanggan;
        String total_bayar;


        public getSum(String id_pelanggan){
            this.id_pelanggan = id_pelanggan;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                    String response = Request.post(SERVER_SUM,dataToSend);

                    //dapatkan respon
                    Log.e("Respon", response);

                    JSONObject ob = new JSONObject(response);
                    scs = ob.getInt("success");

                    if (scs == 1) {
                        JSONArray products = ob.getJSONArray("daftar");

                        for (int i = 0; i < products.length(); i++) {
                            JSONObject c = products.getJSONObject(i);

                            // Storing each json item in variable
                            total_bayar = c.getString("total_bayar");
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
            if(total_bayar == "null"){
                finish();
                Toast.makeText(KeranjangActivity.this, "Tidak ada item di keranjang anda", Toast.LENGTH_SHORT).show();
            }else{
                textTotal.setText("Total: Rp. "+total_bayar);

                btnBayar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(KeranjangActivity.this, PembayaranActivity.class);
                        intent.putExtra("key_total_bayar", total_bayar);
                        intent.putExtra("key_id_keranjang", id_keranjang);
                        startActivity(intent);
                    }
                });

            }
        }

    }
    /////////

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
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
