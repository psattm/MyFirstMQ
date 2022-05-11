package per.hrj.mq;

import per.hrj.mq.server.Broker;

public class BrokerMain {


    public static void main(String[] args) {
        Broker broker = new Broker();
        broker.createQueue("first-mq");
        broker.startServer();
    }

}
