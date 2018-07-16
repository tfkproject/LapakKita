package ta.widia.lapakkita.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ta.widia.lapakkita.R;
import ta.widia.lapakkita.adapter.UmkmAdapter;
import ta.widia.lapakkita.model.ItemUmkm;
import ta.widia.lapakkita.util.Config;
import ta.widia.lapakkita.util.Request;

/**
 * Created by taufik on 21/05/18.
 */

public class Umkm extends Fragment {

    private RecyclerView rc;
    private List<ItemUmkm> itemList;
    private UmkmAdapter adapter;
    private String url = Config.HOST+"umkm.php";

    private ProgressDialog pDialog;


    public Umkm() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.umkm, container, false);

        rc = (RecyclerView) view.findViewById(R.id.recycler_view_umkm);
        itemList = new ArrayList<>();

        //isi disini
        new getData().execute();

        adapter = new UmkmAdapter(getActivity(), itemList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rc.setLayoutManager(mLayoutManager);
        rc.setAdapter(adapter);

        return view;
    }

    private class getData extends AsyncTask<Void,Void,String> {

        //variabel untuk tangkap data
        private int scs = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
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
                    String response = Request.post(url,dataToSend);

                    //dapatkan respon
                    Log.e("Respon", response);

                    JSONObject ob = new JSONObject(response);
                    scs = ob.getInt("success");

                    if (scs == 1) {
                        JSONArray products = ob.getJSONArray("field");

                        for (int i = 0; i < products.length(); i++) {
                            JSONObject c = products.getJSONObject(i);

                            // Storing each json item in variable
                            String foto = c.getString("logo");
                            String id = c.getString("id_umkm");
                            String pemilik = c.getString("nama_pemilik");
                            String nama = c.getString("nama_umkm");
                            String alamat = c.getString("alamat_umkm");
                            String nohp = c.getString("no_hp");
                            String latitude = c.getString("lat");
                            String longitude = c.getString("lon");

                            itemList.add(new ItemUmkm(id, foto, nama, nohp, alamat, latitude, longitude));

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
}
