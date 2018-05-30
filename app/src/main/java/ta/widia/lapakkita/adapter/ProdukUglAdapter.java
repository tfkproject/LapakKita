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
import ta.widia.lapakkita.model.ItemProdukUgl;

/**
 * Created by taufik on 21/05/18.
 */

public class ProdukUglAdapter extends RecyclerView.Adapter<ProdukUglAdapter.ViewHolder> {

    List<ItemProdukUgl> items;
    Context context;

    public ProdukUglAdapter(Context context, List<ItemProdukUgl> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public ProdukUglAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produk_unggulan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProdukDetail.class);
                intent.putExtra("key_id_pdk", items.get(position).getId());
                context.startActivity(intent);
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
}
