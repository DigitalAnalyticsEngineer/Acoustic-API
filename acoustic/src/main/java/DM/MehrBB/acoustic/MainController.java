package DM.MehrBB.acoustic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.json.*;




public class MainController {

 Date today = new Date();
 static Calendar cal = new GregorianCalendar();


	public static void main(String[] args) throws Exception {

        Authentication auth = new Authentication();
        String token = auth.makeRequest();

        List<JSONObject>data3 = new ArrayList<JSONObject>();
          
		SentMailingsRequest mails = new SentMailingsRequest();
		List<String>data2 = mails.makeRequest(token);

		System.out.println("POST DONE");

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new java.util.Date());
        //System.out.println(timeStamp);

        
        AggregatedTrackingRequest track = new AggregatedTrackingRequest();

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

                String json1 = track.makeRequest(mailID, reportID, token);

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

}