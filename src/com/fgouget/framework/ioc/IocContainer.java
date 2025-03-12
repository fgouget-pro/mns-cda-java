package com.fgouget.framework.ioc;

import com.fgouget.framework.server.Controller;
import com.fgouget.framework.server.IRouter;
import com.fgouget.framework.server.RequestHandler;

import javax.inject.Named;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class IocContainer {

    private final Map<String, IocBeanInfo<?>> beansInfo = new HashMap<>();
    private final List<String> controllers = new ArrayList<>();


    public IocContainer() {
        scanPackage("com.fgouget.framework.server");
        scanPackage("com.fgouget.framework.util");
        System.out.println(beansInfo);
    }

    public void scanPackage(String packageName) {
        System.out.println("Scanning package: " + packageName);
        var classes = findAllClassesUsingClassLoader(packageName);
        var namedClasses = getNamedClasses(classes);
        namedClasses.forEach(c -> {
            System.out.println("Processing class " + c.getName());
            Named n = c.getAnnotation(Named.class);
            var name = c.getSimpleName();
            if (n != null && !n.value().isBlank()){
                name = n.value();
            }
            if (c.isAnnotationPresent(Controller.class)) {
                controllers.add(name);
            }
            var beanInfo = getBeanInfo(c, classes, name);
            System.out.println("Found Bean with name " + name);
            beansInfo.put(name, beanInfo);
        });
        startControllers();
    }

    public void refresh(){
        startControllers();
    }

    private void startControllers(){
        this.controllers.forEach(c -> {
            var controller = getInstance(RequestHandler.class , c);
            var route = controller.getClass().getAnnotation(Controller.class).route();
            getInstance(IRouter.class, "IRouter").addHandler(route, controller);
        });
    }

    private <T> IocBeanInfo<T> getBeanInfo(Class<T> c, Set<Class<?>> classes, String name) {
        var candidates = classes.stream()
                .filter(candidate -> !candidate.isInterface() && c.isAssignableFrom(candidate))
                .map(k -> (Class<? extends  T>) k)
                .toList();
        var candidatesInfo = candidates.stream().map(candidate -> {
            if (candidate.getConstructors().length == 0) {
                // Not a valid candidate
                return null;
            }
            // Not sure how to handle multiple constructors, choosing to ignore it for now.
            var constructor = candidate.getConstructors()[0];
            var deps = constructor.getParameterTypes();

            return new IocCandidateInfo<T>(candidate, deps);
        }).toList();
        return new IocBeanInfo<T>(name, c, candidatesInfo);
    }

    private Set<Class<?>> getNamedClasses(Set<Class<?>> classes) {
        return classes.stream()
                .filter(this::isNamedClass)
                .collect(Collectors.toSet());
    }

    public <T> T getInstance(Class<T> clazz, String name){
        System.out.println("Trying to get instance of " + clazz.getName() + " with name " + name);
        IocBeanInfo<T> beanInfo = (IocBeanInfo<T>) beansInfo.get(name);
        if (beanInfo == null) { return null;}
        if (beanInfo.getInstance() == null) {
            System.out.println("No Existing instance");
            //TODO handle several candidates
            var candidate = beanInfo.getCandidates().getFirst();
            if (candidate.getDependencies().length == 0){
                System.out.println("Found 0 dependency constructor");
                try {
                    T inst = candidate.getCandidate().getConstructor().newInstance();
                    beanInfo.setInstance(inst);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Constructor needs dependencies");
                var deps = Arrays.stream(candidate.getDependencies()).map(c ->{
                        IocBeanInfo b = findBeanByClass(c);
                        if (b == null) { throw new RuntimeException();}
                        return getInstance(b.getBeanClass(), b.getBeanName());
                }).toArray();
                try {
                    T inst = (T) candidate.getCandidate().getConstructors()[0].newInstance(deps);
                    beanInfo.setInstance(inst);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return beanInfo.getInstance();
    }

    private IocBeanInfo findBeanByName(String name){
        return this.beansInfo.get(name);
    }

    private IocBeanInfo findBeanByClass(Class<?> clazz){
        return this.beansInfo.values()
                .stream()
                .filter(bean -> bean.getBeanClass().equals(clazz))
                .findFirst().orElse(null);
    }



    private Set<Class<?>> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    private Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return null;
    }

    private boolean isNamedClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(Named.class)
                || clazz.isAnnotationPresent(Controller.class);

    }


}
