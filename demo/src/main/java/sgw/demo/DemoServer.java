package sgw.demo;

import sgw.core.NettyGatewayServer;
import sgw.core.NettyGatewayServerConfig;
import sgw.core.ThreadPoolStrategy;
import sgw.core.routing.Router;
import sgw.core.routing.RouterScanner;
import sgw.core.service_discovery.RpcInvokerDiscoverer;

public class DemoServer {

    public static void main(String[] args) {
        try {
            NettyGatewayServerConfig config = NettyGatewayServerConfig.getDebugConfig();
            ThreadPoolStrategy strategy = new ThreadPoolStrategy(ThreadPoolStrategy.MULTI_WORKERS, 16, 0);
            config.setThreadPoolStrategy(strategy);

            NettyGatewayServer server = new NettyGatewayServer(config);
            /**
             * init Router by scanning annotation
             */
            Router router = new RouterScanner()
                    .ofPackage("sgw.demo.parser")
                    .init();
            RpcInvokerDiscoverer discoverer = new RpcInvokerDiscoverer.Builder()
                    .loadFromConfig("demo/src/main/resources/discovery.properties")
                    .build("demo/src/main/resources/zookeeper.properties");

//            /**
//             * init Router from routing.yaml
//             */
//            Router router = Router.initFromConfig();

            server.setRouter(router);
            server.setDiscoverer(discoverer);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
