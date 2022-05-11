package per.hrj.mq.msg;

import lombok.Data;

import java.io.Serializable;

@Data
public class BindingQueueMessage implements Serializable {

    private String queueName;
}
