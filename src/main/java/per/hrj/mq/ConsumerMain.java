package per.hrj.mq;


import per.hrj.mq.conumser.Consumer;

public class ConsumerMain {

    public static void main(String[] args) {
        Consumer consumer = new Consumer();
        consumer.start("first-mq");
    }
}
