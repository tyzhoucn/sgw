package sgw.core.http_channel.thrift;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sgw.core.http_channel.HttpChannelContext;
import sgw.core.service_channel.RpcInvokerDef;
import sgw.core.service_channel.thrift.ThriftCallWrapper;
import sgw.core.data_convertor.FullHttpRequestParser;

import java.util.List;

public class HttpReqToThrift extends MessageToMessageDecoder<FullHttpRequest>{

    private final Logger logger = LoggerFactory.getLogger(HttpReqToThrift.class);

    // TODO: support configuration
    private static final String ARG_PATH_FORMAT = "examples.thrift_service.%s$%s_args";
    private static final String RESULT_PATH_FORMAT = "examples.thrift_service.%s$%s_result";

    private HttpChannelContext httpCtx;

    public HttpReqToThrift(HttpChannelContext httpCtx) {
        this.httpCtx = httpCtx;
    }

    @Override
    public void decode(ChannelHandlerContext ctx, FullHttpRequest request, List<Object> out) throws Exception {
        // FullHttpRequestParser has been created before this.
        FullHttpRequestParser requestParser = httpCtx.getFullHttpRequestParser();
        logger.info("Converting Http request to Thrift request BY {}", requestParser.getClass().getName());
        // parse http request into an array of parameters.
        Object[] params = requestParser.parse(request);

        RpcInvokerDef invokerDef = httpCtx.getInvokerDef();

        TBase<?, TFieldIdEnum> args = createThriftArg(params, invokerDef);
        TBase result = createThriftResult(invokerDef);
        TMessage message = new TMessage(invokerDef.getMethodName(), TMessageType.CALL, 0);
        String serviceName = invokerDef.getServiceName().toLowerCase();
        ThriftCallWrapper wrapper = new ThriftCallWrapper(args, result, message, serviceName);
        out.add(wrapper);
    }

    private TBase<?, TFieldIdEnum> createThriftArg(Object[] params, RpcInvokerDef invokerDef) throws Exception {
        TBase<?, TFieldIdEnum> args;
        String clazzName = String.format(ARG_PATH_FORMAT,
                invokerDef.getServiceName(), invokerDef.getMethodName());
        try {
            Class<?> clazz = Class.forName(clazzName);
            args = (TBase<?, TFieldIdEnum>) clazz.newInstance();

            for (int fieldId = 1; fieldId <= params.length; fieldId++) {
                TFieldIdEnum field = args.fieldForId(fieldId);
                args.setFieldValue(field, params[fieldId - 1]);
            }
        } catch (ClassNotFoundException e) {
            // Deal wiht ClassNotFoundException separately here. Later all Exceptions will be
            // converted into DecoderException.
            logger.error("Thrift class named as {} can not be found.", clazzName);
            throw e;
        }
        return args;
    }

    private TBase createThriftResult(RpcInvokerDef invokerDef) throws Exception {
        TBase result;
        String clazzName = String.format(RESULT_PATH_FORMAT,
                invokerDef.getServiceName(), invokerDef.getMethodName());
        try {
            Class<?> clazz = Class.forName(clazzName);
            result = (TBase) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            logger.error("Thrift class named as {} can not be found.", clazzName);
            throw e;
        }
        return result;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}