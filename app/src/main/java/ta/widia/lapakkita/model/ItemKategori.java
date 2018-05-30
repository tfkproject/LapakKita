package ta.widia.lapakkita.model;

/**
 * Created by taufik on 22/05/18.
 */

public class ItemKategori {
    String id, kategori;

    public ItemKategori(String id, String kategori){
        this.id = id;
        this.kategori = kategori;
    }

    public String getId() {
        return id;
    }

    public String getKategori() {
        return kategori;
    }
}
