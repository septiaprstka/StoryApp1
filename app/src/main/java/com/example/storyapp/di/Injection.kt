import android.content.Context
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.data.preference.Pref
import com.example.storyapp.data.preference.dataStore
import com.example.storyapp.data.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = Pref.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return Repository.getInstance(apiService, pref)
    }
}