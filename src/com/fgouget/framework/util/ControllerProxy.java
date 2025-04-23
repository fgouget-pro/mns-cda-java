package com.fgouget.framework.util;

import com.fgouget.framework.context.ApplicationContext;
import com.fgouget.framework.server.ITemplateEngine;
import com.fgouget.framework.server.model.Request;
import com.fgouget.framework.server.annotations.Controller;
import com.fgouget.framework.server.annotations.HANDLE;
import com.fgouget.framework.server.enums.HttpMethods;
import com.fgouget.framework.server.model.Response;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ControllerProxy extends DynamicProxy {

    private String route;
    private final Map<String, Method> mappings = new HashMap<>();

    public ControllerProxy(Object target, ApplicationContext context) {
        super(target, context);
        if (target.getClass().isAnnotationPresent(Controller.class)){
            this.route = target.getClass().getAnnotation(Controller.class).route();
            Arrays.stream(target.getClass().getMethods()).forEach((method) -> {
                if (method.isAnnotationPresent(HANDLE.class)){
                    HttpMethods httpMethod = method.getAnnotation(HANDLE.class).httpMethod();
                    mappings.put(httpMethod.name(), method);
                }
            });
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> About to invoke " + method.getName() + " from " + target.getClass().getName());
        if (method.getName().equals("getRoute")) {
            return this.route;
        }

        if (method.getName().equals("handle")) {
            if (args[0] instanceof Request req) {
                Method m = mappings.get(req.getMethod());
                Response response = new Response();
                Object[] mappedArgs = determineArgs(m, args, response);
                Object r = m.invoke(target, mappedArgs);
                if (m.getReturnType() == String.class){
                  ITemplateEngine templateEngine = context.getBean(ITemplateEngine.class);
                  response.setBody(templateEngine.process("WEB-INF/templates/" + r.toString() + ".html",
                          response.getContext() != null  ? response.getContext() : new HashMap<>(),
                          req));
                }
                return proxy.getClass()
                        .getMethod("sentWithResponse", Request.class, Response.class)
                        .invoke(proxy, req, response);
            }
        }
        return method.invoke(target, args);
    }

    private Object[] determineArgs(Method method, Object[] args, Response response) {
        return Arrays
                .stream(method.getParameters())
                .map(param -> {
                    if (param.getType() == Request.class) {
                        return args[0];
                    }
                    if (param.getType() == Response.class) {
                        return response;
                    }
                    return null;
        }).toArray();



    }
}
