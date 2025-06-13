import kr.apo2073.ToonationBuilder;
import kr.apo2073.data.Donation;
import kr.apo2073.listener.ToonationEventListener;

public class Main {
    public static void main(String[] args) {
        new ToonationBuilder()
                .setKey("567c401744b0dea791fb2a14f6be51ed") // https://toon.at/widget/alertbox/(here)
                .addListener(new ToonationEventListener() {
//                    @Override
//                    public void onChat(Chatting chatting) {
//                        System.out.println(chatting.getNickName()
//                                +" :: " + chatting.getComment());
//                    }

                    @Override
                    public void onDonation(Donation donation) {
                        System.out.println(donation.getNickName()+":"+donation.getAmount());
                    }

                    @Override
                    public void onFail() {
                        System.out.println("error");
                    }
                })
                .build();
    }
}
