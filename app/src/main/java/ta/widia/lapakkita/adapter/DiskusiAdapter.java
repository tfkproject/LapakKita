package ta.widia.lapakkita.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import ta.widia.lapakkita.R;
import ta.widia.lapakkita.model.ItemDiskusi;
import ta.widia.lapakkita.model.ItemTransaksi;

/**
 * Created by taufik on 16/07/18.
 */

public class DiskusiAdapter extends RecyclerView.Adapter<DiskusiAdapter.MyViewHolder>  {

    private Context mContext;
    private List<ItemDiskusi> itemList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtNama;
        public TextView txtKomentar;
        public TextView waktu;

        public ImageButton btn_rincian;

        public MyViewHolder(View view) {
            super(view);

            txtNama = (TextView) view.findViewById(R.id.txt_nama);
            txtKomentar = (TextView) view.findViewById(R.id.txt_komentar);
            waktu = (TextView) view.findViewById(R.id.txt_waktu);
        }
    }

    public DiskusiAdapter(Context mContext, List<ItemDiskusi> itemList) {
        this.mContext = mContext;
        this.itemList = itemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_diskusi, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ItemDiskusi item = itemList.get(position);

        holder.txtNama.setText(item.getNama_pelanggan());
        holder.txtKomentar.setText(item.getKomentar());
        holder.waktu.setText("Pada: "+item.getTimestamp());

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

}