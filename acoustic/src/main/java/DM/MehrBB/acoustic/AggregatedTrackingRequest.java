package DM.MehrBB.acoustic;


import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;

public class AggregatedTrackingRequest {

	

String result = ""; 
 
 String sendEncoding = "utf-8";

 int BUFFER_SIZE = 4096;



public String makeRequest(String mailid, String reportid, String token) throws Exception{

String request2 = "xml=<Envelope><Body><SHARED /><GetAggregateTrackingForMailing><MAILING_ID>"+mailid+"</MAILING_ID><REPORT_ID>"+reportid+"</REPORT_ID></GetAggregateTrackingForMailing></Body></Envelope>";
System.out.println(request2);

HttpURLConnection urlConn = null; OutputStream out = null; InputStream in = null;

String pod = "api-campaign-us-3.goacoustic.com";

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



out.write(request2.getBytes(sendEncoding));


out.flush();



System.out.println(urlConn);
in = urlConn.getErrorStream();
System.out.println(in);
in = urlConn.getInputStream();
InputStreamReader inReader = new InputStreamReader(in, sendEncoding); 

StringBuffer responseBuffer = new StringBuffer();
char[] buffer = new char[BUFFER_SIZE]; 
int bytes;
while ((bytes = inReader.read(buffer)) != -1) {
responseBuffer.append(buffer, 0, bytes);
}

String response = responseBuffer.toString();
//System.out.print(response);




//RequestData 2
String responseMID = response;
//System.out.println(responseMID);
int i = 1;
while(i<responseMID.split("<MailingId>").length){

String mailingId = response.split("<MailingId>")[i];
mailingId = mailingId.split("</MailingId>")[0];
//System.out.println(mailingId);

String numSent = response.split("<NumSent>")[i];
numSent = numSent.split("</NumSent>")[0];
//System.out.println(numSent);

String numBouncesSoft = response.split("<NumBounceSoft>")[i];
numBouncesSoft = numBouncesSoft.split("</NumBounceSoft>")[0];
//System.out.println(numBouncesSoft);

String numBouncesHard = response.split("<NumBounceHard>")[i];
numBouncesHard = numBouncesHard.split("</NumBounceHard>")[0];
//System.out.println(numBouncesHard);

String numUniqueClicks = response.split("<NumUniqueClick>")[i];
numUniqueClicks = numUniqueClicks.split("</NumUniqueClick>")[0];
////System.out.println(numUniqueClicks);

String numUniqueOpen = response.split("<NumUniqueOpen>")[i];
numUniqueOpen = numUniqueOpen.split("</NumUniqueOpen>")[0];
//System.out.println(numUniqueOpen);

String numUnsub = response.split("<NumUnsubscribes>")[i];
numUnsub = numUnsub.split("</NumUnsubscribes>")[0];
//System.out.println(numUnsub);

result = "{"+"\"mailId\": "+mailingId+","+"\"numSent\": "+numSent+","+"\"numBouncesSoft\": "+numBouncesSoft+","+"\"numBouncesHard\": "+numBouncesHard+","+"\"numUniqueClicks\": "+numUniqueClicks+","+"\"numUniqueOpen\": "+numUniqueOpen+","+"\"numUnsub\": "+numUnsub + ",";
//System.out.println("String:"+result);
i++;

}


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
    //return data;

	return result;
}

}
