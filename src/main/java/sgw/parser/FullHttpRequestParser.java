package sgw.parser;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * stateless data parser
 */
public interface FullHttpRequestParser {

    /**
     *
     * @param request FullHttpRequest
     * @return A Object array represents the parameters included in thrift method call.
     */
    Object[] parse(FullHttpRequest request);

}
