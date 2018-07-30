package hughes.alex.marinerlicenceprep

import android.os.AsyncTask
import android.util.Log
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class StripeCharge(private val token: String, val name: String, private val amount: Int) : AsyncTask<String, Void, String>() {
    override fun doInBackground(vararg params: String?): String {
        println("Greska 3")
        Thread{
            postData(name, token, amount.toString())
            println("Thread start")
        }.start()
        return "Done"
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        Log.e("Result",result)
        println("Post execute $result")
    }

    private fun postData(description: String, token: String, amount: String){
        try {
            val url = URL("https://marinerlicenseprep.com/api/Charge")
            val conn = url.openConnection() as HttpURLConnection
            conn.readTimeout = 10000
            conn.connectTimeout = 15000
            conn.requestMethod = "POST"
            conn.doInput = true
            conn.doOutput = true

            val params = ArrayList<Pair<String, String>>()
            params.add(Pair("method", "charge"))
            params.add(Pair("description", description))
            params.add(Pair("source", token))
            params.add(Pair("amount", amount))

            val os: OutputStream

            os = conn.outputStream
            val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
            writer.write(params.toString()) //TODO Check this line
            writer.flush()
            writer.close()
            os.close()

        }catch (e: IOException) {
            e.printStackTrace()
            println("Greska 2")
        }
    }
    /*
        public void postData(String description, String token,String amount) {
        // Create a new HttpClient and Post Header
        try {
            URL url = new URL("[YOUR_SERVER_CHARGE_SCRIPT_URL]");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new NameValuePair("method", "charge"));
            params.add(new NameValuePair("description", description));
            params.add(new NameValuePair("source", token));
            params.add(new NameValuePair("amount", amount));

            OutputStream os = null;

            os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     */
}