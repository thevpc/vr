package net.vpc.app.vainruling.core.restclient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by vpc on 2/26/17.
 */
public class VrRestClient {
    private CloseableHttpClient httpClient;
    private CookieStore cookieStore = new BasicCookieStore();
    private HttpContext httpContext = new BasicHttpContext();
    private String url = "http://localhost:8080/";
    private String wsContext = "ws";

    public static void main(String[] args) {
        VrRestClient s = new VrRestClient();
        s.login("admin", "admin");

        JsonObject ret = s.wscriptJson("" +
                "Return(bean('calendarsPlugin').loadCalendars('my-calendars', ''));" +
                "");
//        Map ret = s.remoteScriptInvocation("" +
//                "Return(bean('vrMenuManager').createMenu(''));" +
//                "");
//        Map ret = s.remoteScriptInvocation("" +
//                "Return(bean('corePlugin').getPluginBeans());" +
//                "var y=(bean('corePlugin').getPluginBeans());" +
//                "for(i=0;i<y.length;i++){" +
//                " var oo={};" +
//                " var b=bean(y[i]);" +
//                " oo.cls=b.getClass().getDeclaredMethods();" +
//                " Return(oo);" +
//                "}" +
//                "");
        System.out.println(ret);

        s.logout();

    }

    public void logout() {
        invokePost("/core/logout");
    }

    public void login(String login, String password) {
        invokePost("/core/login"
                , "login", login
                , "password", password
        );
    }

    public Map wscriptMap(String script) {
        String s=invokePost("/core/wscript"
                , "script", script
        );
        Gson g=new Gson();
        return g.fromJson(s,LinkedHashMap.class);
    }

    public JsonObject wscriptJson(String script) {
        String s=invokePost("/core/wscript"
                , "script", script
        );
        Gson g=new Gson();
        return g.fromJson(s,JsonObject.class);
    }

    private void check() {
        if (httpClient == null) {
            HttpClientBuilder b = HttpClientBuilder.create();
            httpClient = b.build();
            httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
        }
    }

    public void close() {
        try {
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public Object execAs(Class cls, String path, Object... args) {
//        String result = exec(path, args);
//        return convert(result, cls);
//    }
//
//    private Object convert(String str, Class cls) {
//        return str;
//    }
//    private String toJson(Object o){
//        if(o==null){
//            throw new RuntimeException("Unsupported");
//        }
//        TypedParam t=null;
//        if(!(o instanceof TypedParam)){
//            t=new TypedParam(o,o.getClass());
//        }else{
//            t=(TypedParam) o;
//        }
//        switch (t.getType().getName()){
//            case "java.lang.String" :{
//                String formatted=(t.getValue()==null)?"null":("\""+t.getValue().toString()+"\"");
//                return  "{\"String\":" + formatted + "}";
//            }
//            case "java.lang.Integer" :{
//                String formatted=(t.getValue()==null)?"null":(t.getValue().toString());
//                return  "{\"Integer\":" + formatted + "}";
//            }
//            case "java.lang.Long" :{
//                String formatted=(t.getValue()==null)?"null":(t.getValue().toString());
//                return  "{\"Long\":" + formatted + "}";
//            }
//            case "java.lang.Double" :{
//                String formatted=(t.getValue()==null)?"null":(t.getValue().toString());
//                return  "{\"Double\":" + formatted + "}";
//            }
//            case "java.util.Date" :{
//                String formatted=(t.getValue()==null)?"null":("\""+ UNIVERSAL_DATE_FORMAT.format(t.getValue().toString())+"\"");
//                return  "{\"Date\":" + formatted + "}";
//            }
//        }
//        throw new IllegalArgumentException("Unhandled");
//    }

//    public String exec(String path, Object... args) {
//        check();
//        StringBuilder ret = new StringBuilder();
//        try {
////            org.apache.http.client.utils.URIBuilder
//            URIBuilder builder = new URIBuilder("http://localhost:8080/ws" + path);
//            for (int i = 0; i < args.length; i += 2) {
//                builder.setParameter((String) args[i], toJson(args[i + 1]));
//            }
//
//            HttpGet getRequest = new HttpGet(builder.build());
//
////            getRequest.addHeader("accept", "application/json");
//            HttpResponse response = httpClient.execute(getRequest, httpContext);
//
//            if (response.getStatusLine().getStatusCode() != 200) {
//                throw new RuntimeException("Failed : HTTP error code : "
//                        + response.getStatusLine().getStatusCode());
//            }
//
//            BufferedReader br = new BufferedReader(
//                    new InputStreamReader((response.getEntity().getContent())));
//
//            String output;
//            System.out.println("Output from Server .... \n");
//            while ((output = br.readLine()) != null) {
//                ret.append(output).append("\n");
//            }
//
//
//        } catch (URISyntaxException e) {
//
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//
//            e.printStackTrace();
//
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//        return ret.toString();
//    }

    public String invokePost(String path, Object... args) {
        check();
        StringBuilder ret = new StringBuilder();
        try {
//            org.apache.http.client.utils.URIBuilder
            URIBuilder builder = new URIBuilder(url+"/"+wsContext+"/" + path);
//            for (int i = 0; i < args.length; i += 2) {
//                builder.setParameter((String) args[i], toJson(args[i + 1]));
//            }

            HttpPost httpPost = new HttpPost(builder.build());

            ArrayList postParameters = new ArrayList<NameValuePair>();
            for (int i = 0; i < args.length; i += 2) {
                postParameters.add(new BasicNameValuePair(String.valueOf(args[i]), String.valueOf(args[i+1])));
            }

            httpPost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));

//            getRequest.addHeader("accept", "application/json");
            HttpResponse response = httpClient.execute(httpPost, httpContext);

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
