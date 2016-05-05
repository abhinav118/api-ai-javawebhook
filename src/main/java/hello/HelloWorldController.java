package hello;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

@Controller
@RequestMapping("/webhook")
public class HelloWorldController {

	public static final String ACK="Awesome!! Your order is set. Let us know if you need anything else. You can pay thru credit/cash upon delivery at your seat.";
	public static final String AIRTABLE_KEY="keyoftrav837aUPXt";
	public Boolean orderflag=true;
	public static UUID sample=UUID.randomUUID();
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody WebhookResponse webhook(@RequestBody String obj) throws UnsupportedEncodingException, UnirestException{
    	System.out.println("dump input :"+obj);
    	JSONObject result = new JSONObject(obj);
        JSONArray messages=result.getJSONArray("messages");
        JSONObject message=(JSONObject)messages.get(0);
        String userText=message.getString("text");
        String userId=result.getJSONObject("appUser").getString("_id");
        
        //call api.ai
        //generate new UUID
        generateUUID();
        UUID newUUID = sample;
        
        String apiAiUrl="https://api.api.ai/v1/query?v=20150910&query="+URLEncoder.encode(userText, "UTF-8")+"&timezone=America/Los_Angeles&lang=en&sessionId="+newUUID;
		com.mashape.unirest.http.HttpResponse<JsonNode> resp1= Unirest.get(apiAiUrl).header("Authorization", "Bearer "+"c1a85789d30c41caaf2a3a57d6944b0a").asJson();
		System.out.println("API AI : url:"+apiAiUrl+" \nresp:"+resp1.getBody().toString());
		String responses = resp1.getBody().getObject().get("result").toString();
		JSONObject resultAPI = new JSONObject(responses);
		JSONObject fulfillment=resultAPI.getJSONObject("fulfillment");
		//JSONObject botResponse=fulfillment.getJSONObject("speech");
		responses=fulfillment.getString("speech");
		String food=null,seat=null;
		
	//	if(resultAPI.getJSONObject("parameters")!=null && resultAPI.getJSONObject("parameters").getString("food-type")!=null)
		// food=resultAPI.getJSONObject("parameters").getString("food-type");
		//if(resultAPI.getJSONObject("parameters")!=null && resultAPI.getJSONObject("parameters").getString("seat-type")!=null)
		//seat=resultAPI.getJSONObject("parameters").getString("seat-type");
		
//		String foodquery=null;
//		if(food!=null && orderflag){
//			 foodquery=resultAPI.getJSONObject("resolvedQuery").toString();
//			orderflag=false;
//		}
		//write to DB based on condition
//		if(responses.equals(ACK)){
//			com.mashape.unirest.http.HttpResponse<JsonNode> sendBack= Unirest.post("https://api.airtable.com/v0/app3UMH5dguRtyYrG/Orders").
//					header("Content-Type","application/json").
//					header("Authorization", "Bearer "+AIRTABLE_KEY)
//					.body("{  \"fields\": {   \"items\": \""+food+"\",    \"quantity\": 1,   \"order ready\": false,   \"seat\": \""+seat+"\" }}")
//					.asJson();
//			System.out.println("airtable resp:"+sendBack.getBody().toString());
//		}
//		
		
        //reply back post message
        String smoochUrl="https://api.smooch.io/v1/appusers/"+userId+"/conversation/messages";
		//curl https://api.smooch.io/v1/appusers/20ae49ed73335d252915a305/conversation/messages      -X POST      -d '{"text":"hello", "role": "appMaker"}'      -H 'content-type: application/json'      -H 'authorization: Bearer
		String jwt="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImtpZCI6IjU3MTQ4NzZkZTMwNDhlNTEwMDM4OWE4MyJ9.eyJzY29wZSI6ImFwcCIsImlhdCI6MTQ2MTAwODUwMH0.5OxoBODWHpO3K6vVLxtzcKPKnkZDIqr9P8JFJnK1QHc";
		com.mashape.unirest.http.HttpResponse<JsonNode> sendBack= Unirest.post(smoochUrl).
																	header("Content-Type","application/json").
																	header("Authorization", "Bearer "+jwt)
																	.body("{\"text\":\""+responses+"\", \"role\": \"appMaker\"}")
																	.asJson();
		
        
        
        
		//System.out.println("url:"+smoochUrl+" resp:"+sendBack.getBody().toString());
		
        return new WebhookResponse(sendBack.getBody().toString());
    }
    
    private static void generateUUID(){ 
    Timer timer = new Timer();
    Calendar date = Calendar.getInstance();
    //Setting the date from when you want to activate scheduling
    date.set(2016, 5, 4, 23, 8);
    //execute every 3 seconds
    timer.schedule(new GenerateUUID(), date.getTime(), 120000);
	}
    
    public static class GenerateUUID extends TimerTask{	
        public static UUID newOne;
    	public void run() {
    		sample=UUID.randomUUID();
    		System.out.println("NEW UUID"+sample);
        }
    }
    
}
