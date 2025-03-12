package com.fgouget.framework.server;

import javax.inject.Named;

@Named
public interface IServer {

    public void serve(int port);
}
