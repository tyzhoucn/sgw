package examples.thrift_server;

import org.apache.thrift.TException;

public class EchoServiceHandler implements EchoService.Iface {

    @Override
    public String echo(String param) throws TException {
        return param;
    }
}
