package per.hrj.mq.msg;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class Message implements Serializable {


    // ����ֻʵ�ֶ���  ָ������
    private String targetName;

    private Object data;

}
