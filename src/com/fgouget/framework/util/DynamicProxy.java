package com.fgouget.framework.util;

import com.fgouget.framework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class DynamicProxy implements InvocationHandler {

    protected final Object target;
    protected final ApplicationContext context;

    public DynamicProxy(Object target, ApplicationContext context) {
        this.target = target;
        this.context = context;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> About to invoke " + method.getName() + " from " + target.getClass().getName());
        return method.invoke(target, args);
    }
}
