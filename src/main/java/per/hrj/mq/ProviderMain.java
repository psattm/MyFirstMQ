package per.hrj.mq;

import per.hrj.mq.provider.Provider;

public class ProviderMain {

    public static void main(String[] args) {

        Provider provider = new Provider();
        provider.start();
        provider.sendMessage("first-mq", "hello world");
    }
}
