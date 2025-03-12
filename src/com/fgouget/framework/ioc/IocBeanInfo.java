package com.fgouget.framework.ioc;

import java.util.List;

public class IocBeanInfo<T> {

    private String beanName;
    private Class<T> beanClass;
    private final List<IocCandidateInfo<T>> candidates;
    private T instance;

    public IocBeanInfo(String beanName, Class<T> beanClass, List<IocCandidateInfo<T>> candidates){
        this.beanName = beanName;
        this.beanClass = beanClass;
        this.candidates = candidates;
    }

    @Override
    public String toString() {
        return "IocBeanInfo{" +
                "beanName='" + beanName + '\'' +
                ", beanClass=" + beanClass +
                ", candidates=" + candidates.toString() +
                ", instance=" + instance +
                '}';
    }

    public T getInstance() {
        return instance;
    }

    public void setInstance(T instance) {
        this.instance = instance;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Class<T> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<T> beanClass) {
        this.beanClass = beanClass;
    }

    public List<IocCandidateInfo<T>> getCandidates() {
        return candidates;
    }

    //    public T getInstance(){
//        if (this.instance == null){
//            var c = candidates.getFirst();
//            try {
//
//                return c.getConstructor().newInstance();
//            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
//                     NoSuchMethodException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return null;
//    }


}
