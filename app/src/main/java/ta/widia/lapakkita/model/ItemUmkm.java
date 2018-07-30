package ta.widia.lapakkita.model;

/**
 * Created by taufik on 21/05/18.
 */

public class ItemUmkm {
    String id, url_gambar, nama_ukm, no_hp, alamat, lat, lon;

    public ItemUmkm(String id, String url_gambar, String nama_ukm, String no_hp, String alamat, String lat, String lon){
        this.id = id;
        this.url_gambar = url_gambar;
        this.nama_ukm = nama_ukm;
        this.no_hp = no_hp;
        this.alamat = alamat;
        this.lat = lat;
        this.lon = lon;
    }

    public String getId() {
        return id;
    }

    public String getUrl_gambar() {
        return url_gambar;
    }

    public String getNama_ukm() {
        return nama_ukm;
    }

    public String getNo_hp() {
        return no_hp;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }
}
