package ta.widia.lapakkita.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import ta.widia.lapakkita.ProdukDetail;
import ta.widia.lapakkita.R;
import ta.widia.lapakkita.adapter.ProdukAdapter;
import ta.widia.lapakkita.adapter.SliderAdapter;
import ta.widia.lapakkita.model.ItemProduk;
import ta.widia.lapakkita.model.ItemSlider;
import ta.widia.lapakkita.util.Config;
import ta.widia.lapakkita.util.Request;

/**
 * Created by taufik on 21/05/18.
 */

public class Beranda extends Fragment{

    ViewPager viewPagerSlider;
    private ProgressDialog pDialog;
    SliderAdapter sliderAdapter;

    private ImageView[] dots;
    private List<ItemSlider> sliderItem;
    private static int currentPage = 0;
    private String url_slider = Config.HOST+"slider.php";

    private RecyclerView rc;
    private List<ItemProduk> itemPrdkUglList;
    private ProdukAdapter prdkAdapter;
    private String url_produk_ugl = Config.HOST+"produk_unggulan.php";
    private String url_post_view = Config.HOST+"update_view.php";

    public Beranda() {
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
        View view = inflater.inflate(R.layout.beranda, container, false);


        viewPagerSlider = (ViewPager) view.findViewById(R.id.viewPagerSlider);
        rc = (RecyclerView) view.findViewById(R.id.recycler_view_pdk_ugl);


        sliderItem = new ArrayList<ItemSlider>();
        sliderItem.add(new ItemSlider("", "", ""));
        sliderItem.add(new ItemSlider("", "", ""));
        sliderItem.add(new ItemSlider("", "", ""));
        sliderItem.add(new ItemSlider("", "", ""));

        new getSlider().execute();

        new getProdukUgl().execute();

        sliderAdapter = new SliderAdapter(getActivity(), sliderItem);
        viewPagerSlider.setAdapter(sliderAdapter);
        ////

        itemPrdkUglList = new ArrayList<>();

        prdkAdapter = new ProdukAdapter(getActivity(), itemPrdkUglList, new ProdukAdapter.AdapterListener() {
            @Override
            public void onSelected(int position, String id_produk) {
                Intent intent = new Intent(getActivity(), ProdukDetail.class);
                intent.putExtra("key_id_pdk", id_produk);
                startActivity(intent);

                new updateView(id_produk).execute();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        rc.setLayoutManager(mLayoutManager);
        rc.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        rc.setItemAnimator(new DefaultItemAnimator());
        rc.setAdapter(prdkAdapter);

        //sliderAdapter.notifyDataSetChanged();
        //prdkAdapter.notifyDataSetChanged();

        return view;
    }

    private class getSlider extends AsyncTask<Void,Void,String> {

        //variabel untuk tangkap data
        private int scs = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Memuat data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();*/
        }

        protected String doInBackground(Void... params) {

            try{
                //susun parameter
                HashMap<String,String> detail = new HashMap<>();

                try {
                    //convert this HashMap to encodedUrl to send to php file
                    String dataToSend = hashMapToUrl(detail);
                    //make a Http request and send data to php file
                    String response = Request.post(url_slider,dataToSend);

                    //dapatkan respon
                    Log.e("Respon", response);

                    JSONObject ob = new JSONObject(response);
                    scs = ob.getInt("success");

                    if (scs == 1) {
                        JSONArray products = ob.getJSONArray("field");
                        sliderItem.clear();
                        for (int i = 0; i < products.length(); i++) {
                            JSONObject c = products.getJSONObject(i);

                            // Storing each json item in variable
                            String judul = c.getString("judul_slider");
                            String pemilik = c.getString("nama_pemilik");
                            String gambar = c.getString("gambar_slider");

                            sliderItem.add(new ItemSlider(gambar, judul, pemilik));
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
            sliderAdapter.notifyDataSetChanged();

            if(sliderAdapter != null){
                // Auto start of viewpager
                final Handler handler = new Handler();
                final Runnable Update = new Runnable() {
                    public void run() {
                        if (currentPage == sliderItem.size()) {
                            currentPage = 0;
                        }
                        viewPagerSlider.setCurrentItem(currentPage++, true);
                    }
                };
                Timer swipeTimer = new Timer();
                swipeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, 3000, 3000);

            }
            ///////////
        }

    }

    private class getProdukUgl extends AsyncTask<Void,Void,String> {

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
                    String response = Request.post(url_produk_ugl,dataToSend);

                    //dapatkan respon
                    Log.e("Respon", response);

                    JSONObject ob = new JSONObject(response);
                    scs = ob.getInt("success");

                    if (scs == 1) {
                        JSONArray products = ob.getJSONArray("field");

                        for (int i = 0; i < products.length(); i++) {
                            JSONObject c = products.getJSONObject(i);

                            // Storing each json item in variable
                            String id = c.getString("id_produk");
                            String foto = c.getString("gambar");
                            String nama = c.getString("nama_produk");
                            String pemilik = c.getString("nama_umkm");

                            itemPrdkUglList.add(new ItemProduk(id, foto, nama, pemilik));

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
            prdkAdapter.notifyDataSetChanged();
            pDialog.dismiss();

        }

    }

    private class updateView extends AsyncTask<Void,Void,String> {

        //variabel untuk tangkap data
        private int scs = 0;
        private String id_produk, psn;

        public updateView(String id_produk){
            this.id_produk = id_produk;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pDialog = new ProgressDialog(UkmDetail.this);
            pDialog.setMessage("Memuat data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();*/
        }

        protected String doInBackground(Void... params) {

            try{
                //susun parameter
                HashMap<String,String> detail = new HashMap<>();
                detail.put("id_produk", id_produk);

                try {
                    //convert this HashMap to encodedUrl to send to php file
                    String dataToSend = hashMapToUrl(detail);
                    //make a Http request and send data to php file
                    String response = Request.post(url_post_view,dataToSend);

                    //dapatkan respon
                    Log.e("Respon", response);

                    JSONObject ob = new JSONObject(response);
                    scs = ob.getInt("success");

                    if (scs == 1) {
                        psn = ob.getString("message");
                    } else {
                        // fail
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
            //..

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

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
