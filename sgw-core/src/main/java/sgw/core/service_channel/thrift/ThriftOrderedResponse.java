package sgw.core.service_channel.thrift;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessageType;
import sgw.core.util.ChannelOrderedMessage;

public class ThriftOrderedResponse implements ChannelOrderedMessage {

    private byte type;
    private TApplicationException exception;
    private TBase result;
    private long channelRequestId;

    @Override
    public long channelMessageId() {
        return channelRequestId;
    }

    public void setChannelRequestId(long channelRequestId) {
        this.channelRequestId = channelRequestId;
    }

    public void setException(TApplicationException exception) {
        this.exception = exception;
    }

    public void setResult(TBase result) {
        this.result = result;
    }

    public TBase getResult() {
        return result;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    public TApplicationException getException() {
        return exception;
    }
}
