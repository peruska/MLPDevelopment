package hughes.alex.marinerlicenceprep

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class NetworkSingleton constructor(context: Context) {
    companion object {
        @Volatile
        private var NetworkSingletonInstance: NetworkSingleton? = null
        fun getNetworkSingletonInstance(context: Context) =
                NetworkSingletonInstance
                        ?: synchronized(this) {
                    NetworkSingletonInstance
                            ?: NetworkSingleton(context)
                }
    }
    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }
    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}