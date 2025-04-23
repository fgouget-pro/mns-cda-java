package com.fgouget.framework.ioc;

import com.fgouget.framework.server.annotations.AutoStart;
import com.fgouget.framework.server.annotations.Controller;

import javax.inject.Named;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class PackageScanner {


    private final Map<String, IocBeanInfo<?>> beansInfo = new HashMap<>();
    private final Set<String> controllers = new HashSet<>();
    private final Set<Class<?>> autoStarts = new HashSet<>();

    public void scanPackage(String packageName) {
        System.out.println("Scanning package: " + packageName);
        var classes = findAllClassesUsingClassLoader(packageName);
        var namedClasses = getNamedClasses(classes);
        namedClasses.forEach(c -> {
            System.out.println("Processing class " + c.getName());
            Named n = c.getAnnotation(Named.class);
            var name = c.getSimpleName();
            if (n != null && !n.value().isBlank()) {
                name = n.value();
            }
            if (c.isAnnotationPresent(Controller.class)) {
                controllers.add(name);
            }
            if (c.isAnnotationPresent(AutoStart.class)) {
                //TODO Refactor with controllers
                autoStarts.add(c);
            }
            var beanInfo = getBeanInfo(c, classes, name);
            System.out.println("Found Bean with name " + name);
            beansInfo.put(name, beanInfo);
        });
    }

    public Map<String, IocBeanInfo<?>> getBeansInfo() {
        return beansInfo;
    }

    public Set<String> getControllers() {
        return controllers;
    }

    public Set<Class<?>> getAutoStarts() {
        return autoStarts;
    }

    private Set<Class<?>> getNamedClasses(Set<Class<?>> classes) {
        return classes.stream()
                .filter(this::isNamedClass)
                .collect(Collectors.toSet());
    }

    private boolean isNamedClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(Named.class)
                || clazz.isAnnotationPresent(Controller.class);
    }

    private Set<Class<?>> findAllClassesUsingClassLoader(String packageName) {
        System.out.println("Scanning package: " + packageName);
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        assert stream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        var found = reader.lines().collect(Collectors.groupingBy(line -> line.endsWith(".class")));
        Set<Class<?>> classes = found.getOrDefault(true, new ArrayList<>()).stream()
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
        var otherClasses = found
                .getOrDefault(false, new ArrayList<>()).stream()
                .map(s -> packageName + "." + s)
                .map(this::findAllClassesUsingClassLoader)
                .collect(Collectors.flatMapping(Collection::stream, Collectors.toSet()));
        classes.addAll(otherClasses);
        return classes;
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

    private <T> IocBeanInfo<T> getBeanInfo(Class<T> c, Set<Class<?>> classes, String name) {
        var candidates = classes.stream()
                .filter(candidate -> !candidate.isInterface() && c.isAssignableFrom(candidate))
                .map(k -> (Class<? extends T>) k)
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
}
