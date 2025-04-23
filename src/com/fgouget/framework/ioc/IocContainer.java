package com.fgouget.framework.ioc;

import com.fgouget.framework.context.ApplicationContext;
import com.fgouget.framework.server.annotations.Controller;
import com.fgouget.framework.server.IRequestHandler;
import com.fgouget.framework.server.IRouter;
import com.fgouget.framework.util.ControllerProxy;
import com.fgouget.framework.util.DynamicProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.*;

public class IocContainer {

    private final Map<String, IocBeanInfo<?>> beansInfo = new HashMap<>();
    private final List<String> controllers = new ArrayList<>();
    private final List<Class<?>> autoStarts = new ArrayList<>();
    private final ApplicationContext context;

    public IocContainer(ApplicationContext context) {
        this.context = context;
        scanPackage("com.fgouget.framework");
    }

    public void scanPackage(String packageName) {
        var scanner = new PackageScanner();
        scanner.scanPackage(packageName);
        controllers.addAll(scanner.getControllers());
        autoStarts.addAll(scanner.getAutoStarts());
        beansInfo.putAll(scanner.getBeansInfo());
        refresh();
    }


    public void refresh() {
        startAutoStarts();
        startControllers();
    }

    private void startAutoStarts() {
        this.autoStarts.forEach(c -> {
            getInstance(c, c.getSimpleName());
        });
    }

    private void startControllers() {
        this.controllers.forEach(c -> {
            var controller = getInstance(IRequestHandler.class, c);
            var route = controller.getRoute();
            getInstance(IRouter.class, "IRouter").addHandler(route, controller);
        });
    }


    public <T> T getInstance(Class<T> clazz, String name) {
        System.out.println("Trying to get instance of " + clazz.getName() + " with name " + name);
        IocBeanInfo<T> beanInfo = (IocBeanInfo<T>) beansInfo.get(name);
        if (beanInfo == null) {
            return null;
        }
        if (beanInfo.getInstance() == null) {
            System.out.println("No Existing instance");
            //TODO handle several candidates
            var candidate = beanInfo.getCandidates().getFirst();
            if (candidate.getDependencies().length == 0) {
                System.out.println("Found 0 dependency constructor");
                beanInfo.setInstance(instantiate(clazz, candidate, null));
            } else {
                System.out.println("Constructor needs dependencies");
                var deps = Arrays.stream(candidate.getDependencies()).map(c -> {
                    IocBeanInfo<?> b = findBeanByClass(c);
                    if (b == null) {
                        throw new RuntimeException();
                    }
                    return getInstance(b.getBeanClass(), b.getBeanName());
                }).toArray();
                beanInfo.setInstance(instantiate(clazz, candidate, deps));
            }
        }
        return beanInfo.getInstance();
    }


    private <T> T instantiate(Class<T> clazz, IocCandidateInfo<T> candidate, Object[] deps) {
        try {
            T inst;
            boolean isController = candidate.getCandidate().isAnnotationPresent(Controller.class);
            if (deps == null){
                inst = candidate.getCandidate().getConstructor().newInstance();
            } else {
                inst = candidate.getCandidate().getConstructor(candidate.getDependencies()).newInstance(deps);
            }
            Class[] classes = null;
            if (clazz.isInterface()) {
                classes = new Class[]{clazz};
            } else {
                if (isController) {
                    classes = new Class[]{IRequestHandler.class};
                }
            }
            System.out.println("####### PROXY class: " + clazz.getName() + " CLASSES = "  + Arrays.toString(classes) + " IS CONTROLLER = " + isController);
            if (classes != null){
                System.out.println("Creating proxy instance with class " + inst.getClass().getName());
                Object p;
                if (isController){
                    p = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), classes, new ControllerProxy(inst, context));
                } else {
                    p = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), classes, new DynamicProxy(inst, context));
                }
                System.out.println("INTERFACES" + p.getClass().getInterfaces()[0].getName());
                inst = (T) p;
            }
            return inst;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e){
            throw new RuntimeException(e);
        }
    }


    private IocBeanInfo findBeanByName(String name) {
        return this.beansInfo.get(name);
    }

    private <T> IocBeanInfo<T> findBeanByClass(Class<T> clazz) {
        return this.beansInfo.values()
                .stream()
                .filter(bean -> bean.getBeanClass().equals(clazz))
                .map(b -> (IocBeanInfo<T>) b)
                .findFirst().orElse(null);
    }






}
