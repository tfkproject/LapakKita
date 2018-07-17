package ta.widia.lapakkita.model;

/**
 * Created by taufik on 25/06/18.
 */

public class ItemTransaksi {

    private String id_transaksi;
    private String id_keranjang;
    private String id_pelanggan;
    private String total_bayar;
    private String status;
    private String file_bukti;
    private String metode_bayar;
    private String alamat;
    private String timestamp;

    public String getId_transaksi() {
        return id_transaksi;
    }

    public String getId_keranjang() {
        return id_keranjang;
    }

    public String getId_pelanggan() {
        return id_pelanggan;
    }

    public String getTotal_bayar() {
        return total_bayar;
    }

    public String getStatus() {
        return status;
    }

    public String getFile_bukti() {
        return file_bukti;
    }

    public String getMetode_bayar() {
        return metode_bayar;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId_transaksi(String id_transaksi) {
        this.id_transaksi = id_transaksi;
    }

    public void setId_keranjang(String id_keranjang) {
        this.id_keranjang = id_keranjang;
    }

    public void setId_pelanggan(String id_pelanggan) {
        this.id_pelanggan = id_pelanggan;
    }

    public void setTotal_bayar(String total_bayar) {
        this.total_bayar = total_bayar;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFile_bukti(String file_bukti) {
        this.file_bukti = file_bukti;
    }

    public void setMetode_bayar(String metode_bayar) {
        this.metode_bayar = metode_bayar;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
