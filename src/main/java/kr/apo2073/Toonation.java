package kr.apo2073;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import kr.apo2073.data.Chatting;
import kr.apo2073.data.Donation;
import kr.apo2073.exception.TokenNotFound;
import kr.apo2073.listener.ToonationEventListener;
import kr.apo2073.utilities.Debugger;
import kr.apo2073.utilities.Streamer;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Toonation extends WebSocketListener {
    private final String key;
    private boolean timeout;
    private final Debugger debugger;
    private final String payload;
    private WebSocket socket;
    private final List<ToonationEventListener> listeners;
    private final Subject<Donation> donationSubject;
    private final Subject<Chatting> chattingSubject;

    public static Streamer getStreamer(String id) {
        //_DisplayCreatorName_1ku87_235
        try {
            Document doc = Jsoup.connect("https://toon.at/donate/"+id)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Gecko/20100101 Firefox/139.0")
                    .cookie("language_code", "en")
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .get();
            //._DisplayCreatorName_1ku87_235

            System.out.println(doc);
            Elements element = doc.select("*[class*=DisplayCreatorName]");
            System.out.println(Objects.requireNonNull(element).text());
            String nickname = element.text();
            return new Streamer(id, nickname);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Toonation(ToonationBuilder builder) {
        this.key=builder.key;
        this.timeout= builder.timeout;
        this.listeners=builder.listeners;
        this.debugger=new Debugger(builder.debug);

        try {
            Document document= Jsoup
                    .connect("https://toon.at/widget/alertbox/"+key)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .get();
            Elements el=document.getElementsByTag("script");
            String script=el.stream().filter(e-> !e.hasAttr("src"))
                    .map(Element::toString).collect(Collectors.joining());
            String payload=parsePayload(script);
            if (payload==null) throw new TokenNotFound();
            this.payload=payload;
//            System.out.println(payload);
            OkHttpClient client=new OkHttpClient().newBuilder()
                    .pingInterval(12, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(0, TimeUnit.MILLISECONDS)
                    .build();
            Request request=new Request.Builder()
                    .url("wss://ws.toon.at/"+payload)
                    .build();
            socket =client.newWebSocket(request, this);
            client.dispatcher().executorService().close();

            donationSubject= PublishSubject.create();
            chattingSubject= PublishSubject.create();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            JsonObject json= new Gson().fromJson(text, JsonObject.class);
//            System.out.println(text);
            Donation donation=getDonation(json);
            if (donation!=null) {
                for (ToonationEventListener listener: listeners)
                    listener.onDonation(donation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parsePayload(String script) {
        Pattern p = Pattern.compile("\\\\u0022payload\\\\u0022:\\\\u0022(.*?)\\\\u0022,");
        Matcher m = p.matcher(script);
        if (m.find()) return m.group(1);
        return null;
    }


    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        try {
            if(!timeout) {
                debugger.debug("투네이션에 연결되었습니다");
                for (ToonationEventListener listener : listeners) {
                    listener.onConnect();
                }
            } else {timeout=false;}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        try {
            timeout = true;

            try {
                webSocket.close(1000, null);
            } catch (Exception closeEx) {
                closeEx.printStackTrace();
            }

            debugger.debug("WebSocket connection failed: " + t.getMessage());
            t.printStackTrace();

            if (response != null) {
                debugger.debug("HTTP code: " + response.code());
                debugger.debug("HTTP message: " + response.message());
            } else {
                debugger.debug("No HTTP response received. Likely a network or handshake failure.");
            }

            OkHttpClient client = new OkHttpClient.Builder()
                    .pingInterval(12, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(0, TimeUnit.MILLISECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url("wss://ws.toon.at/" + payload)
                    .build();
            socket = client.newWebSocket(request, this);

            for (ToonationEventListener listener : listeners) {
                listener.onFail();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        debugger.debug("연결이 종료되었습니다");
        for (ToonationEventListener listener : listeners) {
            listener.onDisconnect();
        }
    }


    public void close() {
        donationSubject.onComplete();
        chattingSubject.onComplete();
        socket.close(1000, null);
    }

    private Donation getDonation(JsonObject json) {
        try {
            if (!json.has("content")) return null;
            json= json.get("content").getAsJsonObject();
            return new Donation(
                    json.get("account").toString(),
                    json.get("name").toString(),
                    json.get("message").toString(),
                    json.get("amount").getAsLong()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
//    private Chatting getChatting(JsonObject json) {
//        try {
//            if (!json.has("content")) return null;
//            json= json.get("content").getAsJsonObject();
//            return new Chatting(
//                    json.get("account").toString(),
//                    json.get("name").toString(),
//                    json.get("message").toString()
//            );
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public String getKey() {
        return key;
    }

    public boolean isTimeout() {
        return timeout;
    }
}
