import kr.apo2073.Toonation;
import kr.apo2073.ToonationBuilder;
import kr.apo2073.data.Donation;
import kr.apo2073.listener.ToonationEventListener;
import okhttp3.Request;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println(Toonation.getStreamer("apo2073").getNickname());
//            new ToonationBuilder()
//                    .setKey("567c401744b0dea791fb2a14f6be51ed") // https://toon.at/widget/alertbox/(here)
//                    .addListener(new ToonationEventListener() {
//                        @Override
//                        public void onDonation(Donation donation) {
//                            System.out.println(donation.getNickName()+":"+donation.getAmount());
//                        }
//
//                        @Override
//                        public void onFail() {
//                            System.out.println("error");
//                        }
//                    })
//                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
