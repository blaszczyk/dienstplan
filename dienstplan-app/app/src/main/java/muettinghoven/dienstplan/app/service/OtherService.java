package muettinghoven.dienstplan.app.service;

import retrofit2.Call;
import retrofit2.http.GET;


public interface OtherService {

    @GET("server/ping")
    public Call<Long> ping();

}
