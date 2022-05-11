package per.hrj.mq.msg;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class Message implements Serializable {


    // 这里只实现队列  指队列名
    private String targetName;

    private Object data;

}
