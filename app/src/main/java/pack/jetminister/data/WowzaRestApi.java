package pack.jetminister.data;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface WowzaRestApi {
    @POST("live_streams")
    Call<LiveStream> createLiveStream(@Body Broadcast broadcast);

    @PUT("live_streams/{id}/start")
    Call<LiveStream> startLiveStream();

    @PUT("live_streams/{id}/stop")
    Call<LiveStream> stopLiveStream();

    @GET("live_streams/{id}/state")
    Call<LiveStream> getLiveStreamState();

    @DELETE("live_streams/{id}")
    Call<LiveStream> deleteLiveStream();
}