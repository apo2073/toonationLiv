import kr.apo2073.Toonation;
import kr.apo2073.ToonationBuilder;
import kr.apo2073.data.Chatting;
import kr.apo2073.listener.ToonationEventListener;

public class Main {
    static {
        Toonation toonation=new ToonationBuilder()
                .setKey("") // https://toon.at/widget/alertbox/(here)
                .addListener(new ToonationEventListener() {
                    @Override
                    public void onChat(Chatting chatting) {
                        System.out.println(chatting.getNickName()
                                +" :: " + chatting.getComment());
                    }
                })
                .build();

    }
}
