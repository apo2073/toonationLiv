[![](https://jitpack.io/v/apo2073/toonationLiv.svg)](https://jitpack.io/#apo2073/toonationLiv)


# 💸ToonationLiv

__[@outstanding1301](https://github.com/outstanding1301)님의 [Donation Alert API](https://github.com/outstanding1301/donation-alert-api)를 업데이트한 API입니다__

---

# Gradle
```gradle
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}


dependencies {
    implementation("com.github.apo2073:toonationLiv:(VERSION )")
}
```

# Example
```java
new ToonationBuilder()
    .setKey("") // https://toon.at/widget/alertbox/(here)
    .addListener(new ToonationEventListener() {
        @Override
        public void onDonation(Donation donation) {
            System.out.println(donation.getNickName()+":"+donation.getAmount());
        }
        
        @Override
        public void onFail() {
            System.out.println("error");
        }
    }).build();
```

---

## 🗃️TODO
 - [ ] Toonation Channel Info getter
 - [ ] Toonation Video Info
 - [x] Test
