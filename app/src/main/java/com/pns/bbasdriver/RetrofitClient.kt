import com.google.gson.GsonBuilder
import com.pns.bbasdriver.RetrofitService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val gson = GsonBuilder().setLenient().create()
    private const val BASE_URL = "http://203.255.3.138:8004/v1/pnsApp/"

    private val instance = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    val mInstance = instance.create(RetrofitService::class.java)
}
