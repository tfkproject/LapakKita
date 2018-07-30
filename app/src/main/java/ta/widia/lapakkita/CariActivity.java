package ta.widia.lapakkita;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ta.widia.lapakkita.model.ItemPeta;
import ta.widia.lapakkita.model.ItemUmkm;
import ta.widia.lapakkita.util.Config;
import ta.widia.lapakkita.util.Request;


public class CariActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String keyword;
    private ProgressDialog pDialog;
    List<ItemUmkm> items;
    private static String url = Config.HOST+"cari_produk.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari);

        getSupportActionBar().setTitle("Pencarian Produk");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String key_pencarian = getIntent().getStringExtra("key_pencarian");
        if(key_pencarian == null){
            keyword = "";
        }else{
            keyword = key_pencarian;
        }

        //dapatkan  izin untuk melakukan thread policy (proses Background AsycnTask)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        items = new ArrayList<>();



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //new dapatkanData().execute();

        /*marker("0.485638", "101.402023", "http://203.153.21.11/app/lapor-sampah/images/IMG_1530604447.jpg", "Banyak sampah", "Jl, ballaasdf");
        marker("0.519283", "101.444595", "http://203.153.21.11/app/lapor-sampah/images/IMG_1530604447.jpg", "Ada sampah", "Jl, ballaasdf");
        marker("0.414229", "101.420562", "http://203.153.21.11/app/lapor-sampah/images/IMG_1530604447.jpg", "Sampah semua", "Jl, ballaasdf");*/

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(1.4730212,102.147085))); //area pekanbaru
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String mId = marker.getId();
                String markId = mId.replaceAll("\\D+","");
                int markerId = Integer.valueOf(markId);

                for(int position = 0 ; position < items.size() ; position++) {
                    if(markerId == position){
                        Intent intent = new Intent(CariActivity.this, UkmDetail.class);
                        intent.putExtra("key_id", items.get(position).getId());
                        intent.putExtra("key_nama", items.get(position).getNama_ukm());
                        intent.putExtra("key_alamat", items.get(position).getAlamat());
                        intent.putExtra("key_hp", items.get(position).getNo_hp());
                        intent.putExtra("key_gambar", items.get(position).getUrl_gambar());
                        intent.putExtra("key_lat", items.get(position).getLat());
                        intent.putExtra("key_lon", items.get(position).getLon());
                        startActivity(intent);
                    }

                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class dapatkanData extends AsyncTask<Void,Void,String> {

        //variabel untuk tangkap data
        private int scs = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CariActivity.this);
            pDialog.setMessage("Memuat data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(Void... params) {
            try{
                //susun parameter
                HashMap<String,String> detail = new HashMap<>();

                try {
                    //convert this HashMap to encodedUrl to send to php file
                    String dataToSend = hashMapToUrl(detail);
                    //make a Http request and send data to php file
                    String response = Request.post(url+"?q="+keyword,dataToSend);

                    //dapatkan respon
                    Log.e("Respon", response);

                    JSONObject ob = new JSONObject(response);
                    scs = ob.getInt("success");

                    if (scs == 1) {
                        JSONArray products = ob.getJSONArray("field");

                        for (int i = 0; i < products.length(); i++) {
                            JSONObject c = products.getJSONObject(i);

                            String foto = c.getString("logo");
                            String id = c.getString("id_umkm");
                            String pemilik = c.getString("nama_pemilik");
                            String nama = c.getString("nama_umkm");
                            String alamat = c.getString("alamat_umkm");
                            String nohp = c.getString("no_hp");
                            String latitude = c.getString("lat");
                            String longitude = c.getString("lon");

                            items.add(new ItemUmkm(id, foto, nama, nohp, alamat, latitude, longitude));
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
            pDialog.dismiss();
            for(int i = 0 ; i < items.size() ; i++) {
                createMarker(items.get(i).getLat(), items.get(i).getLon(), items.get(i).getUrl_gambar(), items.get(i).getNama_ukm(), items.get(i).getAlamat());

            }

        }

    }

    private String hashMapToUrl(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
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

    protected Marker createMarker(final String lok_lat, final String lok_long, final String link_img, final String nama_objek, final String lokasi_objek) {

        double lat = Double.valueOf(lok_lat);
        double lng = Double.valueOf(lok_long);

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(nama_objek)
                .snippet(lokasi_objek)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        /*MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(lat, lng));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(final Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.maps_info_window, null);

                ImageView img = v.findViewById(R.id.img);
                Glide.with(PetaActivity.this).load(link_img).asBitmap().override(250,250).listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        e.printStackTrace();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if(!isFromMemoryCache) marker.showInfoWindow();
                        return false;
                    }
                }).into(img);

                TextView nama = v.findViewById(R.id.txt_nama);
                nama.setText(nama_objek);

                TextView jalan = v.findViewById(R.id.txt_jalan);
                jalan.setText("Lokasi: "+lokasi_objek);

                TextView lat = v.findViewById(R.id.txt_lat);
                lat.setText("Lat: "+lok_lat);

                TextView lon = v.findViewById(R.id.txt_long);
                lon.setText("Lon: "+lok_long);
                return  v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }


        });
        Marker marker = mMap.addMarker(markerOptions);
        marker.showInfoWindow();

        return marker;*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        items.clear();
        new dapatkanData().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cari, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        /////
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(CariActivity.this, CariActivity.class);
                intent.putExtra("key_pencarian", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return true;

            }

        });
        /////


        return true;
    }
}
