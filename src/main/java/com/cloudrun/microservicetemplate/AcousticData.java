package main.java.com.cloudrun.microservicetemplate;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.json.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;




public class AcousticData {

 Date today = new Date();
 static Calendar cal = new GregorianCalendar();

	public static void main(String[] args) throws Exception {

        AcousticData self= new AcousticData();
        String token = self.makeAuthRequest();

        List<JSONObject>data3 = new ArrayList<JSONObject>();
          
		List<String>data2 = self.makeMailingsRequest(token);

		System.out.println("POST DONE");

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new java.util.Date());
        //System.out.println(timeStamp);

        String mailID = "";
        String reportID = "";


        for(int j=0;j<data2.size();j++){
                
            System.out.println(data2.get(j));
            mailID =data2.get(j).split("\"mailingId\":")[1];
                mailID = mailID.split(",")[0];
                System.out.println(mailID);

                reportID =data2.get(j).split("\"reportId\":")[1];
                reportID = reportID.split(",")[0];
                //System.out.println(reportID);
                
            if(!data3.contains(data2.get(j))){

                String json1 = self.makeTrackingRequest(mailID, reportID, token);

                if(json1 != ""){
                    //System.out.println("String:"+json1+data2.get(j));


                    //build one Json-Object from both requests
                    JSONObject dataset = new JSONObject(json1+data2.get(j));
                    //System.out.println(dataset);
                    data3.add(dataset);
                }

            } 
  
           
                
        }
//System.out.println(data3);
System.out.println(data3.size());
JSONArray jsonStr = new JSONArray(data3);
//System.out.println(jsonStr);

JSONObject jsonComplex = new JSONObject();
  
 JSONObject state = new JSONObject();
 
  state.put("transactionsCursor", timeStamp);
  

  JSONObject transactions = new JSONObject();
  transactions.put("transactions", jsonStr);
  
  
  JSONArray pKey = new JSONArray();
    pKey.put("mailingId");
    pKey.put("reportId");
    JSONObject PK = new JSONObject();
    PK.put("primary_key", pKey);
    JSONObject sTransactions = new JSONObject();
    sTransactions.put("transactions", PK);
    
    jsonComplex.put("state", state);
   jsonComplex.put("insert", transactions);
   jsonComplex.put("schema", sTransactions);
  jsonComplex.put("hasMore", "false");
	
  System.out.println(jsonComplex);

}

public String makeAuthRequest() throws Exception{

    String sendEncoding = "utf-8";

    String pod = "api-campaign-us-3.goacoustic.com";
    int BUFFER_SIZE = 4096;
   
    String token = "";

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
    

  
   
   
   
   
   public List<String> makeMailingsRequest(String token) throws Exception{

      String request = "xml=<Envelope><Body><SHARED /><GetSentMailingsForOrg><DATE_START>09/10/2023 00:00:00</DATE_START><DATE_END>12/21/2023 00:00:00</DATE_END></GetSentMailingsForOrg></Body></Envelope>";
 		
    String sendEncoding = "utf-8";
   
    String pod = "api-campaign-us-3.goacoustic.com";
    int BUFFER_SIZE = 4096;
   
    
   List<String>data = new ArrayList<String>();
   
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
   


  
  
  public String makeTrackingRequest(String mailid, String reportid, String token) throws Exception{

       String result = ""; 
 
   String sendEncoding = "utf-8";
  
   int BUFFER_SIZE = 4096;
  
  
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