package sgw.core.data_convertor;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.apache.commons.codec.Charsets;
import sgw.core.filters.FastMessage;

/**
 * HttpResponse Convertor for filtered requests.
 */
public class FastResponseGenerator implements FullHttpResponseGenerator {

    @Override
    public FullHttpResponse generate(Object[] results, ByteBuf buf) {
        FastMessage message = (FastMessage) results[0];
        HttpResponseStatus status = message.getStatus();
        String body = message.getResponseBody();
        buf.writeCharSequence(body, Charsets.UTF_8);
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf);
    }
}
