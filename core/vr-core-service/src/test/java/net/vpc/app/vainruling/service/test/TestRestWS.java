package net.vpc.app.vainruling.service.test;


import net.vpc.upa.exceptions.UPAIllegalArgumentException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

/**
 * Created by vpc on 9/16/16.
 */
public class TestRestWS {
    CloseableHttpClient httpClient ;
    CookieStore cookieStore = new BasicCookieStore();
    HttpContext httpContext = new BasicHttpContext();

    public static void main(String[] args) {
        TestRestWS s = new TestRestWS();
        s.start();
        s.exec("/core/login"
                ,"login","admin"
                ,"password","admin"
        );

        String ret = s.exec("/calendars/my-calendars");
        System.out.println(ret);

        s.exec("/core/logout");


        ret = s.exec("/calendars/my-calendars");
        System.out.println(ret);

        s.shutdown();
    }

    public void start() {
        HttpClientBuilder b=HttpClientBuilder.create();
        httpClient = b.build();
        httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
    }

    public void shutdown() {
        try {
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String exec(String path, Object ... args) {
        StringBuilder ret=new StringBuilder();
        try {
//            org.apache.http.client.utils.URIBuilder
            URIBuilder builder = new URIBuilder("http://localhost:8080/ws" + path);
            for (int i = 0; i < args.length; i+=2) {
                Object arg = args[i + 1];
                String sarg=null;
                if(arg instanceof String){
                    sarg="{\"String\":\""+arg+"\"}";
                }else{
                    throw new UPAIllegalArgumentException("Unhandled");
                }
                builder.setParameter((String)args[i], sarg);
            }

            HttpGet getRequest = new HttpGet(builder.build());
//            getRequest.addHeader("accept", "application/json");
            HttpResponse response = httpClient.execute(getRequest, httpContext);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                ret.append(output).append("\n");
            }


        } catch (URISyntaxException e) {

            e.printStackTrace();
        } catch (ClientProtocolException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }
        return ret.toString();
    }

}

