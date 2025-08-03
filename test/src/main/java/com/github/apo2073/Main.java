package com.github.apo2073;

import kr.apo2073.Toonation;
import kr.apo2073.ToonationBuilder;
import kr.apo2073.data.Donation;
import kr.apo2073.listener.ToonationEventListener;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("apo2073's profile: "+Toonation.getStreamer("apo2073").getNickname());
            new ToonationBuilder()
                    .setKey(args[0]) // https://toon.at/widget/alertbox/(here)
                    .addListener(new ToonationEventListener() {
                        @Override
                        public void onDonation(Donation donation) {
                            String line = donation.getNickName() + ":" + donation.getAmount();
                            System.out.println(line);
                            try (PrintWriter out = new PrintWriter(new FileWriter("donations.txt", true))) {
                                out.println(line);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFail() {
                            System.out.println("error");
                        }
                    })
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
