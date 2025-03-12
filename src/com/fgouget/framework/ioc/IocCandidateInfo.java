package com.fgouget.framework.ioc;

import java.util.Arrays;
import java.util.List;

public class IocCandidateInfo<T> {

    private Class<? extends T> candidate;
    private Class<?>[] dependencies;


    public IocCandidateInfo(Class<? extends T> candidate, Class<?>[] dependencies) {
        this.candidate = candidate;
        this.dependencies = dependencies;
    }

    public Class<? extends T> getCandidate() {
        return candidate;
    }

    public void setCandidate(Class<? extends T> candidate) {
        this.candidate = candidate;
    }

    public Class<?>[] getDependencies() {
        return dependencies;
    }

    public void setDependencies(Class<?>[] dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public String toString() {
        return "IocCandidateInfo{" +
                "candidate=" + candidate +
                ", dependencies=" + Arrays.toString(dependencies) +
                '}';
    }
}
