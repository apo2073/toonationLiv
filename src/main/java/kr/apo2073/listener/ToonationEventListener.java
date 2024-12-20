package kr.apo2073.listener;

import kr.apo2073.data.Chatting;
import kr.apo2073.data.Donation;

public interface ToonationEventListener {
    default void onChat(Chatting chatting) {}
    default void onDonation(Donation donation) {}
    default void onConnect() {}
    default void onDisconnect() {}
    default void onFail() {}
}
