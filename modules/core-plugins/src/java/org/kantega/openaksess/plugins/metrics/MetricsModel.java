package org.kantega.openaksess.plugins.metrics;

import org.joda.time.LocalDateTime;

public class MetricsModel {

    private LocalDateTime datetime;

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

    private double activeRequests;

    private double maxDbConnections;
    private double idleDbConnections;
    private double openDbConnections;

    private double badRequests;
    private double ok;
    private double serverError;
    private double notFound;
    private double noContent;
    private double created;
    private double other;

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
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

    public double getActiveRequests() {
        return activeRequests;
    }

    public void setActiveRequests(double activeRequests) {
        this.activeRequests = activeRequests;
    }

    public double getMaxDbConnections() {
        return maxDbConnections;
    }

    public void setMaxDbConnections(double maxDbConnections) {
        this.maxDbConnections = maxDbConnections;
    }

    public double getIdleDbConnections() {
        return idleDbConnections;
    }

    public void setIdleDbConnections(double idleDbConnections) {
        this.idleDbConnections = idleDbConnections;
    }

    public double getOpenDbConnections() {
        return openDbConnections;
    }

    public void setOpenDbConnections(double openDbConnections) {
        this.openDbConnections = openDbConnections;
    }

    public double getBadRequests() {
        return badRequests;
    }

    public void setBadRequests(double badRequests) {
        this.badRequests = badRequests;
    }

    public double getOk() {
        return ok;
    }

    public void setOk(double ok) {
        this.ok = ok;
    }

    public double getServerError() {
        return serverError;
    }

    public void setServerError(double serverError) {
        this.serverError = serverError;
    }

    public double getNotFound() {
        return notFound;
    }

    public void setNotFound(double notFound) {
        this.notFound = notFound;
    }

    public double getNoContent() {
        return noContent;
    }

    public void setNoContent(double noContent) {
        this.noContent = noContent;
    }

    public double getCreated() {
        return created;
    }

    public void setCreated(double created) {
        this.created = created;
    }

    public double getOther() {
        return other;
    }

    public void setOther(double other) {
        this.other = other;
    }
}
