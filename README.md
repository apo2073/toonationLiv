![jitpack]()
# 💸ToonationLiv

__[@outstanding1301](https://github.com/outstanding1301)님의 [Donation Alert API](https://github.com/outstanding1301/donation-alert-api)를 더 쉽게 만든 API입니다__

---

# Gradle
```gradle
...
```

# Example
```java
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
```

---

## 🗃️TODO
 - [ ] Toonation Channel Info getter
 - [ ] Toonation Video Info
 - [ ] Test