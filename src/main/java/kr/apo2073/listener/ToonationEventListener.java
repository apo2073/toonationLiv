package kr.apo2073.listener;

public interface ToonationEventListener {
    default void onChat() {}
    default void onDonation() {}
}
