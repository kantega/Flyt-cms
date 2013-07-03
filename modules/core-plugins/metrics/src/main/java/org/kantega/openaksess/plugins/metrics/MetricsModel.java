package org.kantega.openaksess.plugins.metrics;

import org.joda.time.LocalDateTime;

public class MetricsModel {

    private LocalDateTime capturetime = LocalDateTime.now();

    private double memoryInit;
    private double memoryMax;
    private double memoryUsed;
    private double memoryCommitted;

    private double heapInit;
    private double heapMax;
    private double heapUsed;
    private double heapCommitted;

    private double heapUsage;
    private double nonHeapUsage;

    private int activeRequests;

    private int maxDbConnections;
    private int idleDbConnections;
    private int openDbConnections;

    private int badRequests;
    private int ok;
    private int serverError;
    private int notFound;

    public LocalDateTime getCapturetime() {
        return capturetime;
    }

    public void setCapturetime(LocalDateTime capturetime) {
        this.capturetime = capturetime;
    }

    public double getMemoryInit() {
        return memoryInit;
    }

    public void setMemoryInit(double memoryInit) {
        this.memoryInit = memoryInit;
    }

    public double getMemoryMax() {
        return memoryMax;
    }

    public void setMemoryMax(double memoryMax) {
        this.memoryMax = memoryMax;
    }

    public double getMemoryUsed() {
        return memoryUsed;
    }

    public void setMemoryUsed(double memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

    public double getMemoryCommitted() {
        return memoryCommitted;
    }

    public void setMemoryCommitted(double memoryCommitted) {
        this.memoryCommitted = memoryCommitted;
    }

    public double getHeapInit() {
        return heapInit;
    }

    public void setHeapInit(double heapInit) {
        this.heapInit = heapInit;
    }

    public double getHeapMax() {
        return heapMax;
    }

    public void setHeapMax(double heapMax) {
        this.heapMax = heapMax;
    }

    public double getHeapUsed() {
        return heapUsed;
    }

    public void setHeapUsed(double heapUsed) {
        this.heapUsed = heapUsed;
    }

    public double getHeapCommitted() {
        return heapCommitted;
    }

    public void setHeapCommitted(double heapCommitted) {
        this.heapCommitted = heapCommitted;
    }

    public double getHeapUsage() {
        return heapUsage;
    }

    public void setHeapUsage(double heapUsage) {
        this.heapUsage = heapUsage;
    }

    public double getNonHeapUsage() {
        return nonHeapUsage;
    }

    public void setNonHeapUsage(double nonHeapUsage) {
        this.nonHeapUsage = nonHeapUsage;
    }

    public int getActiveRequests() {
        return activeRequests;
    }

    public void setActiveRequests(int activeRequests) {
        this.activeRequests = activeRequests;
    }

    public int getMaxDbConnections() {
        return maxDbConnections;
    }

    public void setMaxDbConnections(int maxDbConnections) {
        this.maxDbConnections = maxDbConnections;
    }

    public int getIdleDbConnections() {
        return idleDbConnections;
    }

    public void setIdleDbConnections(int idleDbConnections) {
        this.idleDbConnections = idleDbConnections;
    }

    public int getOpenDbConnections() {
        return openDbConnections;
    }

    public void setOpenDbConnections(int openDbConnections) {
        this.openDbConnections = openDbConnections;
    }

    public int getBadRequests() {
        return badRequests;
    }

    public void setBadRequests(int badRequests) {
        this.badRequests = badRequests;
    }

    public int getOk() {
        return ok;
    }

    public void setOk(int ok) {
        this.ok = ok;
    }

    public int getServerError() {
        return serverError;
    }

    public void setServerError(int serverError) {
        this.serverError = serverError;
    }

    public int getNotFound() {
        return notFound;
    }

    public void setNotFound(int notFound) {
        this.notFound = notFound;
    }
}
