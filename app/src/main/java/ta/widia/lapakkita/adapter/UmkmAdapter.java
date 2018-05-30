package ta.widia.lapakkita.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import ta.widia.lapakkita.R;
import ta.widia.lapakkita.UkmDetail;
import ta.widia.lapakkita.model.ItemUmkm;

/**
 * Created by taufik on 21/05/18.
 */

public class UmkmAdapter extends RecyclerView.Adapter<UmkmAdapter.ViewHolder> {

    List<ItemUmkm> items;
    Context context;

    public UmkmAdapter(Context context, List<ItemUmkm> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public UmkmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_umkm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.itemUmkm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UkmDetail.class);
                intent.putExtra("key_nama", items.get(position).getNama_ukm());
                intent.putExtra("key_alamat", items.get(position).getAlamat());
                intent.putExtra("key_hp", items.get(position).getNo_hp());
                intent.putExtra("key_gambar", items.get(position).getUrl_gambar());
                intent.putExtra("key_lat", items.get(position).getLat());
                intent.putExtra("key_lon", items.get(position).getLon());
                context.startActivity(intent);
            }
        });
        Glide.with(context).load(items.get(position).getUrl_gambar()).into(holder.imgProfil);
        holder.txtNama.setText(items.get(position).getNama_ukm());
        holder.txtHP.setText(items.get(position).getNo_hp());
        holder.txtAlamat.setText(items.get(position).getAlamat());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout itemUmkm;
        ImageView imgProfil;
        TextView txtNama, txtHP, txtAlamat;

        public ViewHolder(View itemView) {
            super(itemView);

            itemUmkm = (LinearLayout) itemView.findViewById(R.id.item_umkm);

            imgProfil = (ImageView) itemView.findViewById(R.id.profil_umkm);
            txtNama = (TextView) itemView.findViewById(R.id.nama_umkm);
            txtHP = (TextView) itemView.findViewById(R.id.hp);
            txtAlamat = (TextView) itemView.findViewById(R.id.alamat);
        }
    }
}
