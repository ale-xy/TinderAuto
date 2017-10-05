package name.alexy.test.tinderauto.phoneservice;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by alexeykrichun on 05/10/2017.
 */

public interface PhoneServiceApi {

    Retrofit retrofit = new Retrofit.Builder().baseUrl("http://www.freesmsverifications.com/smsapi/")
            .client(new OkHttpClient.Builder().addInterceptor(
                    new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            HttpUrl url = request.url().newBuilder()
                                    .addQueryParameter("apiId", Credentials.API_ID)
                                    .addQueryParameter("secret", Credentials.API_SECRET)
                                    .build();
                            request = request.newBuilder().url(url).build();
                            return chain.proceed(request);                }
                    })
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build())
            .addConverterFactory(GsonConverterFactory.create()).build();

    PhoneServiceApi service = retrofit.create(PhoneServiceApi.class);

    @GET("GetNumbers")
    Call<PhoneData> getPhoneData();

    @GET("GetMessages")
    Call<SmsData> getSmsData(@Query("number")String number);
}
