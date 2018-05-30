package ta.widia.lapakkita.util;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import ta.widia.lapakkita.util.jarak.Example;

/**
 * Created by taufik on 30/05/18.
 */

public interface RetrofitMapsJarak {
    /*
    * Retrofit get annotation with our URL
    * And our method that will return us details of student.
    */
    @GET("api/directions/json?")
    Call<Example> getDistanceDuration(@Query("units") String units, @Query("origin") String origin, @Query("destination") String destination, @Query("mode") String mode);
}