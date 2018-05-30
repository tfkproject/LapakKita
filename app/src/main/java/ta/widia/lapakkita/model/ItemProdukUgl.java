package ta.widia.lapakkita.model;

/**
 * Created by taufik on 21/05/18.
 */

public class ItemProdukUgl {
    String id, url_gambar, nama_pdk, ukm;

    public ItemProdukUgl(String id, String url_gambar, String nama_pdk, String ukm){
        this.id = id;
        this.url_gambar = url_gambar;
        this.nama_pdk = nama_pdk;
        this.ukm = ukm;
    }

    public String getId() {
        return id;
    }

    public String getUrl_gambar() {
        return url_gambar;
    }

    public String getNama_pdk() {
        return nama_pdk;
    }

    public String getUkm() {
        return ukm;
    }
}
