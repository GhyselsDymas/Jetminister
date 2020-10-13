package pack.jetminister.data;

import pack.jetminister.data.util.Broadcast;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

//all HTTP requests made to server using Retrofit library
public interface WowzaRestApi {
    @POST("live_streams")
    Call<Broadcast> createLiveStream(@Header("wsc-api-key") String APIKey,
                                     @Header("wsc-access-key") String accessKey,
                                     @Body Broadcast broadcast);

    @PUT("live_streams/{id}/start")
    Call<Broadcast> startLiveStream(@Header("wsc-api-key") String APIKey,
                                    @Header("wsc-access-key") String accessKey,
                                    @Path("id") String streamId);

    @PUT("live_streams/{id}/stop")
    Call<Broadcast> stopLiveStream(@Header("wsc-api-key") String APIKey,
                                   @Header("wsc-access-key") String accessKey,
                                   @Path("id") String streamId);

    @GET("live_streams/{id}/state")
    Call<Broadcast> getLiveStreamState(@Header("wsc-api-key") String APIKey,
                                       @Header("wsc-access-key") String accessKey,
                                       @Path("id") String streamId);

    @DELETE("live_streams/{id}")
    Call deleteLiveStream(@Header("wsc-api-key") String APIKey,
                          @Header("wsc-access-key") String accessKey,
                          @Path("id") String streamId);
}
