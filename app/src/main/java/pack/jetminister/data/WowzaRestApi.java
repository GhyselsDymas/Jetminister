package pack.jetminister.data;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface WowzaRestApi {
    @POST("live_streams")
    Call<LiveStream> createLiveStream();

    @PUT("start")
    Call<LiveStream> startLiveStream();

    @PUT("stop")
    Call<LiveStream> stopLiveStream();

    @GET("state")
    Call<LiveStream> getLiveStreamState();

    @DELETE("delete")
    Call<LiveStream> deleteLiveStream();
}
