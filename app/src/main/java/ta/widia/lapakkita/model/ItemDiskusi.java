package ta.widia.lapakkita.model;

/**
 * Created by taufik on 25/06/18.
 */

public class ItemDiskusi {

    private String id_diskusi;
    private String id_produk;
    private String nama_pelanggan;
    private String komentar;
    private String timestamp;

    public void setId_diskusi(String id_diskusi) {
        this.id_diskusi = id_diskusi;
    }

    public String getId_diskusi() {
        return id_diskusi;
    }

    public void setId_produk(String id_produk) {
        this.id_produk = id_produk;
    }

    public String getId_produk() {
        return id_produk;
    }

    public void setNama_pelanggan(String nama_pelanggan) {
        this.nama_pelanggan = nama_pelanggan;
    }

    public String getNama_pelanggan() {
        return nama_pelanggan;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
