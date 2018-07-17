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
import ta.widia.lapakkita.model.ItemTransaksi;

/**
 * Created by taufik on 16/07/18.
 */

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.MyViewHolder>  {

    private Context mContext;
    private List<ItemTransaksi> itemList;
    private CardAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView id_transaksi;
        public TextView total_bayar;
        public TextView waktu;
        public TextView status;
        public CardView cardView;

        public ImageButton btn_rincian;

        public MyViewHolder(View view) {
            super(view);
            id_transaksi = (TextView) view.findViewById(R.id.txt_id_transaksi);
            total_bayar = (TextView) view.findViewById(R.id.txt_total_bayar);
            waktu = (TextView) view.findViewById(R.id.txt_waktu);
            status = (TextView) view.findViewById(R.id.txt_status);
            cardView = (CardView) view.findViewById(R.id.card_view);

            btn_rincian = (ImageButton) view.findViewById(R.id.overflow);
        }
    }

    public TransaksiAdapter(Context mContext, List<ItemTransaksi> itemList, CardAdapterListener listener) {
        this.mContext = mContext;
        this.itemList = itemList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaksi, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ItemTransaksi invoiceItem = itemList.get(position);

        holder.id_transaksi.setText("INV/"+invoiceItem.getId_transaksi());
        holder.total_bayar.setText("Total Bayar: Rp. "+invoiceItem.getTotal_bayar());
        holder.waktu.setText("Waktu: "+invoiceItem.getTimestamp());

        String status = "";
        final String res_status = invoiceItem.getStatus();
        if(res_status.contains("N")){
            status = "Belum Bayar";
            holder.status.setTextColor(Color.RED);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onCardSelected(position, invoiceItem.getId_transaksi(), res_status);
                }
            });
        }
        if(res_status.contains("W")){
            status = "Menunggu Konfirmasi";
            holder.status.setTextColor(Color.MAGENTA);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onCardSelected(position, invoiceItem.getId_transaksi(), res_status);
                }
            });
        }
        if(res_status.contains("Y")){
            status = "Sudah Bayar";
            holder.status.setTextColor(Color.BLUE);
            holder.cardView.setOnClickListener(null);
        }
        if(res_status.contains("S")){
            status = "Paket sedang dalam pengiriman";
            holder.status.setTextColor(Color.GRAY);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onCardSelected(position, invoiceItem.getId_transaksi(), res_status);
                }
            });
        }
        if(res_status.contains("X")){
            status = "Paket belum diterima";
            holder.status.setTextColor(Color.RED);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onCardSelected(position, invoiceItem.getId_transaksi(), res_status);
                }
            });
        }
        if(res_status.contains("D")){
            status = "Paket sudah diterima";
            holder.status.setTextColor(Color.GREEN);
            holder.cardView.setOnClickListener(null);
            holder.cardView.setCardBackgroundColor(Color.GRAY);
        }

        holder.status.setText(status);

        holder.btn_rincian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onButtonSelected(position, invoiceItem.getId_transaksi());
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public interface CardAdapterListener {
        void onCardSelected(int position, String id_transaksi, String status);
        void onButtonSelected(int position, String id_transaksi);
    }
}