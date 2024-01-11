package DM.MehrBB.acoustic;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;





public class SentMailingsRequest {


 String request = "xml=<Envelope><Body><SHARED /><GetSentMailingsForOrg><DATE_START>09/10/2023 00:00:00</DATE_START><DATE_END>12/21/2023 00:00:00</DATE_END></GetSentMailingsForOrg></Body></Envelope>";
 		
 String sendEncoding = "utf-8";

 String pod = "api-campaign-us-3.goacoustic.com";
 int BUFFER_SIZE = 4096;

 
List<String>data = new ArrayList<String>();




public List<String> makeRequest(String token) throws Exception{

HttpURLConnection urlConn = null; OutputStream out = null; InputStream in = null;


String apiUrl = "https://" + pod + "/XMLAPI";

try {
URL url = new URL(apiUrl);
System.out.println(url);
urlConn = (HttpURLConnection)url.openConnection(); urlConn.setRequestMethod("POST");
urlConn.setDoOutput(true);
urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

urlConn.setRequestProperty("Authorization","Bearer "+token);
        
urlConn.connect();

out = urlConn.getOutputStream(); 

out.write(request.getBytes(sendEncoding));

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

int i = 1;

while(i<response.split("<SentTS>").length){

    
	String mailingId = response.split("<MailingId>")[i];
	mailingId = mailingId.split("</MailingId>")[0];
	System.out.println(mailingId);

    String reportId = response.split("<ReportId>")[i];
	reportId = reportId.split("</ReportId>")[0];
    //System.out.println(reportId);

    String mailingName = response.split("<MailingName><!\\[CDATA\\[")[i];
    mailingName = mailingName.split("\\]\\]></MailingName>")[0];
    //System.out.println(mailingName);

    String mailingSubject = response.split("<Subject><!\\[CDATA\\[")[i];
    mailingSubject = mailingSubject.split("\\]\\]></Subject>")[0];
    //System.out.println(mailingSubject);

    String mailingTime = response.split("<SentTS>")[i];
    mailingTime = mailingTime.split("</SentTS>")[0];
    //System.out.println(mailingTime);

    data.add("\"mailingId\": "+mailingId+","+"\"reportId\": "+reportId+","+"\"mailingName\": "+mailingName+","+"\"mailingSubject\": "+"\""+mailingSubject+"\""+","+"\"mailingTime\": "+"\""+mailingTime+"\""+"}"); //+"\n"
    i++;

    }

System.out.println(data);


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
    return data;
}


}
