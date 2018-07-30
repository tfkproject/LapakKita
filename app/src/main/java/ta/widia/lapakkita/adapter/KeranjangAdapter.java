package ta.widia.lapakkita.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import ta.widia.lapakkita.R;
import ta.widia.lapakkita.model.ItemKeranjang;

/**
 * Created by taufik on 25/06/18.
 */

public class KeranjangAdapter extends RecyclerView.Adapter<KeranjangAdapter.MyViewHolder>  {

    private Context mContext;
    private List<ItemKeranjang> itemList;
    private CardAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView namaProduk;
        public TextView namaUmkm;
        public TextView jum;
        public TextView harga;
        public ImageView thumbnail;
        public CardView cardView;

        public ImageButton btn_remove;

        public MyViewHolder(View view) {
            super(view);

            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            cardView = (CardView) view.findViewById(R.id.card_view);
            namaProduk = (TextView) view.findViewById(R.id.nama_produk);
            namaUmkm = (TextView) view.findViewById(R.id.nama_umkm);
            jum = (TextView) view.findViewById(R.id.jumlah);
            harga = (TextView) view.findViewById(R.id.harga);

            btn_remove = (ImageButton) view.findViewById(R.id.overflow);
        }
    }

    public KeranjangAdapter(Context mContext, List<ItemKeranjang> itemList, CardAdapterListener listener) {
        this.mContext = mContext;
        this.itemList = itemList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_keranjang, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ItemKeranjang keranjangItem = itemList.get(position);

        holder.namaProduk.setText(keranjangItem.getNama_produk());
        holder.namaUmkm.setText(keranjangItem.getNama_umkm());
        holder.jum.setText("Jumlah Pesan: "+keranjangItem.getJumlah()+" Paket");
        holder.harga.setText("Harga Total: Rp. "+keranjangItem.getHarga());
        // loading item cover using Glide library
        Glide.with(mContext).load(keranjangItem.getGambar()).into(holder.thumbnail);

        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onButtonSelected(position, keranjangItem.getId_item_beli());
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public interface CardAdapterListener {
        void onButtonSelected(int position, String id_item_beli);
    }
}