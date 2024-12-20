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
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
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

    public Toonation(ToonationBuilder builder) {
        this.key=builder.key;
        this.timeout= builder.timeout;
        this.listeners=builder.listeners;
        this.debugger=new Debugger(builder.debug);

        try {
            Document document= Jsoup.connect(
                    "https://toon.at/widget/alertbox/"
                            +key
            ).get();
            Elements el=document.getElementsByTag("script");
            String script=el.stream().filter(e-> !e.hasAttr("src"))
                    .map(Element::toString).collect(Collectors.joining());

            String payload=parsePayload(script);
            if (payload==null) throw new TokenNotFound();

            this.payload=payload;
            OkHttpClient client=new OkHttpClient().newBuilder()
                    .readTimeout(0, TimeUnit.MILLISECONDS)
                    .build();
            Request request=new Request.Builder()
                    .url("wss://toon.at:8071/"+payload)
                    .build();
            socket =client.newWebSocket(request, this);
            client.dispatcher().executorService().shutdown();

            donationSubject= PublishSubject.create();
            chattingSubject= PublishSubject.create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            JsonObject json= new Gson().fromJson(text, JsonObject.class);
            Donation donation=getDonation(json);
            if (donation==null) {
                Chatting chatting=getChatting(json);
                for (ToonationEventListener listener : listeners)
                    listener.onChat(chatting);
            } else {
                for (ToonationEventListener listener: listeners)
                    listener.onDonation(donation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parsePayload(String script) {
        Pattern p=Pattern.compile("\"payload\":\"(.*)\",");
        if (p.matcher(script).find()) return p.matcher(script).group(1);
        return null;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        if(!timeout) {
            debugger.debug("투네이션에 연결되었습니다");
            for (ToonationEventListener listener : listeners) {
                listener.onConnect();
            }
        } else {timeout=false;}
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        timeout = true;
        webSocket.close(1000, null);
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();
        Request request = new Request.Builder()
                .url("wss://toon.at:8071/"+payload)
                .build();
        socket = client.newWebSocket(request, this);
        for (ToonationEventListener listener : listeners) {
            listener.onFail();
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
            return null;
        }
    }
    private Chatting getChatting(JsonObject json) {
        try {
            if (!json.has("content")) return null;
            json= json.get("content").getAsJsonObject();
            return new Chatting(
                    json.get("account").toString(),
                    json.get("name").toString(),
                    json.get("message").toString()
            );
        } catch (Exception e) {
            return null;
        }
    }

    public String getKey() {
        return key;
    }

    public boolean isTimeout() {
        return timeout;
    }
}
