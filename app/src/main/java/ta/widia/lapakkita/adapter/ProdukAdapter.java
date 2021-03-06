package ta.widia.lapakkita.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import ta.widia.lapakkita.ProdukDetail;
import ta.widia.lapakkita.R;
import ta.widia.lapakkita.model.ItemProduk;

/**
 * Created by taufik on 21/05/18.
 */

public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.ViewHolder> {

    List<ItemProduk> items;
    Context context;
    private AdapterListener listener;

    public ProdukAdapter(Context context, List<ItemProduk> items, AdapterListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public ProdukAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produk_unggulan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSelected(position, items.get(position).getId());
            }
        });
        Glide.with(context).load(items.get(position).getUrl_gambar()).into(holder.imgProduk);
        holder.txtNama.setText(items.get(position).getNama_pdk());
        holder.txtUkm.setText(items.get(position).getUkm());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView imgProduk;
        TextView txtNama, txtUkm;

        public ViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_item);
            imgProduk = (ImageView) itemView.findViewById(R.id.img_produk);
            txtNama = (TextView) itemView.findViewById(R.id.txt_nama);
            txtUkm = (TextView) itemView.findViewById(R.id.txt_ukm);
        }
    }

    public interface AdapterListener {
        void onSelected(int position, String id_produk);
    }
}
