package ta.widia.lapakkita.model;

/**
 * Created by taufik on 21/05/18.
 */

public class ItemSlider {

    String url_gambar, judul, pemilik;

    public ItemSlider(String url_gambar, String judul, String pemilik){
        this.url_gambar = url_gambar;
        this.judul = judul;
        this.pemilik = pemilik;
    }

    public String getUrl_gambar() {
        return url_gambar;
    }

    public String getJudul() {
        return judul;
    }

    public String getPemilik() {
        return pemilik;
    }
}
