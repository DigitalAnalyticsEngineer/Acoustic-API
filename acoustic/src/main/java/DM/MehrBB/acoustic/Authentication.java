package DM.MehrBB.acoustic;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Authentication {


 String sendEncoding = "utf-8";

 String pod = "api-campaign-us-3.goacoustic.com";
 int BUFFER_SIZE = 4096;

 String token = "";



public String makeRequest() throws Exception{



HttpURLConnection urlConn = null; OutputStream out = null; InputStream in = null;


String apiUrl = "https://" + pod + "/oauth/token";

try {
URL url = new URL(apiUrl);
System.out.println(url);
urlConn = (HttpURLConnection)url.openConnection(); urlConn.setRequestMethod("POST");
urlConn.setDoOutput(true);
urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        
urlConn.connect();

out = urlConn.getOutputStream(); 

out.write("grant_type=refresh_token&client_id=878c9073-503d-4fb2-8184-6e04cae32d01&client_secret=83e492f2-28ef-4a2d-84d8-2ed77b7ee683&refresh_token=rpVBvQpycDBPuR7ff0Bn2hZ6iswhti68cDy7B0_5q9dcS1".getBytes());

out.flush();



System.out.println(urlConn);
in = urlConn.getInputStream();

InputStreamReader inReader = new InputStreamReader(in, sendEncoding); 

StringBuffer responseBuffer = new StringBuffer();
char[] buffer = new char[BUFFER_SIZE]; 
int bytes;
while ((bytes = inReader.read(buffer)) != -1) {
responseBuffer.append(buffer, 0, bytes);
}

String response = responseBuffer.toString();
System.out.println(response);
token = response;
token = token.split("\"access_token\":\"",0)[1];
token = token.split("\"")[0];
System.out.println(token);


} finally {
    
if (out != null) {
try {out.close();} catch (Exception e) {}
}
if (in != null) {
try {in.close();} catch (Exception e) {}
}
if (urlConn != null) { urlConn.disconnect();
}
}
    return token;
}

}
