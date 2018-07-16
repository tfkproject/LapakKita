package ta.widia.lapakkita.model;

/**
 * Created by taufik on 25/06/18.
 */

public class ItemKeranjang {

    private String id_item_beli;
    private String id_keranjang;
    private String nama_produk;
    private String nama_umkm;
    private String jumlah;
    private String harga;
    private String gambar;

    public String getId_item_beli() {
        return id_item_beli;
    }

    public String getId_keranjang() {
        return id_keranjang;
    }

    public String getNama_produk() {
        return nama_produk;
    }

    public String getNama_umkm() {
        return nama_umkm;
    }

    public String getJumlah() {
        return jumlah;
    }

    public String getHarga() {
        return harga;
    }

    public String getGambar() {
        return gambar;
    }


    public void setId_item_beli(String id_item_beli) {
        this.id_item_beli = id_item_beli;
    }

    public void setId_keranjang(String id_keranjang) {
        this.id_keranjang = id_keranjang;
    }

    public void setNama_produk(String nama_produk) {
        this.nama_produk = nama_produk;
    }

    public void setNama_umkm(String nama_umkm) {
        this.nama_umkm = nama_umkm;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
