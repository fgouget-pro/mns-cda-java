package com.mns.todo.model;

import java.util.ArrayList;
import java.util.List;

public class Student {

    private List<Long> maths = new ArrayList<>();
    private List<Long> french = new ArrayList<>();
    private List<Long> history = new ArrayList<>();
    private double mathsAverage;
    private double frenchAverage;
    private double historyAverage;
    private double totalAverage;

    private String name;

    @Override
    public String toString() {
        return "Student{" +
                "maths=" + maths +
                ", french=" + french +
                ", history=" + history +
                ", mathAverage=" + mathsAverage +
                ", frenchAverage=" + frenchAverage +
                ", historyAverage=" + historyAverage +
                ", totalAverage=" + totalAverage +
                ", name='" + name + '\'' +
                '}';
    }

    public Student(String name) {
        this.name = name;
        for (int i = 0; i <10; i++){
            maths.add((long)Math.floor(Math.random()*20));
            french.add((long)Math.floor(Math.random()*20));
            history.add((long)Math.floor(Math.random()*20));
        }
    }


    public void setAverages(){
        this.setFrenchAverage(this.getFrench().stream().mapToLong(a -> a).average().orElse(0));
        this.setHistoryAverage(this.getHistory().stream().mapToLong(a -> a).average().orElse(0));
        this.setMathsAverage(this.getMaths().stream().mapToLong(a -> a).average().orElse(0));
        this.setTotalAverage((this.getMathsAverage() + this.getFrenchAverage() + this.getHistoryAverage())/3);
    }

    public List<Long> getFrench() {
        return french;
    }

    public void setFrench(List<Long> french) {
        this.french = french;
    }

    public List<Long> getHistory() {
        return history;
    }

    public void setHistory(List<Long> history) {
        this.history = history;
    }

    public double getMathsAverage() {
        return mathsAverage;
    }

    public void setMathsAverage(double mathsAverage) {
        this.mathsAverage = mathsAverage;
    }

    public double getFrenchAverage() {
        return frenchAverage;
    }

    public void setFrenchAverage(double frenchAverage) {
        this.frenchAverage = frenchAverage;
    }

    public double getHistoryAverage() {
        return historyAverage;
    }

    public void setHistoryAverage(double historyAverage) {
        this.historyAverage = historyAverage;
    }

    public Long getTotalAverage() {
        return (long) totalAverage;
    }

    public void setTotalAverage(double totalAverage) {
        this.totalAverage = totalAverage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getMaths() {
        return maths;
    }

    public void setMaths(List<Long> maths) {
        this.maths = maths;
    }
}
