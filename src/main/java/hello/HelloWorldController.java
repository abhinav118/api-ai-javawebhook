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

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

@Controller
@RequestMapping("/webhook")
public class HelloWorldController {

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody WebhookResponse webhook(@RequestBody String obj) throws UnsupportedEncodingException, UnirestException{
    	JSONObject result = new JSONObject(obj);
        JSONArray messages=result.getJSONArray("messages");
        JSONObject message=(JSONObject)messages.get(0);
        String userText=message.getString("text");
        String userId=result.getJSONObject("appUser").getString("_id");
        
        //call api.ai
        
        String apiAiUrl="https://api.api.ai/v1/query?v=20150910&query="+URLEncoder.encode(userText, "UTF-8")+"&timezone=America/Los_Angeles&lang=en&sessionId=bd6e8074-e4a2-4b64-8088-0c45ea12978d";
		com.mashape.unirest.http.HttpResponse<JsonNode> resp1= Unirest.get(apiAiUrl).header("Authorization", "Bearer "+"c1a85789d30c41caaf2a3a57d6944b0a").asJson();
		System.out.println("API AI : url:"+apiAiUrl+" resp:"+resp1.getBody().toString());
		String responses = resp1.getBody().getObject().get("result").toString();
		JSONObject resultAPI = new JSONObject(responses);
		JSONObject fulfillment=resultAPI.getJSONObject("fulfillment");
		//JSONObject botResponse=fulfillment.getJSONObject("speech");
		responses=fulfillment.getString("speech");
		
        //reply back post message
        String smoochUrl="https://api.smooch.io/v1/appusers/"+userId+"/conversation/messages";
		//curl https://api.smooch.io/v1/appusers/20ae49ed73335d252915a305/conversation/messages      -X POST      -d '{"text":"hello", "role": "appMaker"}'      -H 'content-type: application/json'      -H 'authorization: Bearer
		String jwt="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImtpZCI6IjU3MTQ4NzZkZTMwNDhlNTEwMDM4OWE4MyJ9.eyJzY29wZSI6ImFwcCIsImlhdCI6MTQ2MTAwODUwMH0.5OxoBODWHpO3K6vVLxtzcKPKnkZDIqr9P8JFJnK1QHc";
		com.mashape.unirest.http.HttpResponse<JsonNode> sendBack= Unirest.post(smoochUrl).
																	header("Content-Type","application/json").
																	header("Authorization", "Bearer "+jwt)
																	.body("{\"text\":\""+responses+"\", \"role\": \"appMaker\"}")
																	.asJson();
		
        
        
        
		System.out.println("url:"+smoochUrl+" resp:"+sendBack.getBody().toString());
		
        return new WebhookResponse(sendBack.getBody().toString());
    }
}
