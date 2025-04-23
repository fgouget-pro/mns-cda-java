package com.fgouget.framework.context;

import com.fgouget.framework.ioc.IocContainer;
import com.fgouget.framework.server.IServer;

public class ApplicationContext {

    private final IocContainer iocContainer;

    public ApplicationContext() {
        iocContainer = new IocContainer(this);
    }

    public void startServer(int port){
        IServer server = getBean(IServer.class);
        server.serve(port);
    }

    public void scanPackage(String... packageNames) {
        for (String packageName : packageNames) {
            iocContainer.scanPackage(packageName);
        }
    }

    public <T> T getBean(Class<T> clazz) {
        return getBean(clazz, clazz.getSimpleName());
    }

    public <T> T getBean(Class<T> clazz, String beanName) {
        return iocContainer.getInstance(clazz, beanName);
    }

}
