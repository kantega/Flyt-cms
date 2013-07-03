package org.kantega.openaksess.plugins.metrics;

import org.joda.time.LocalDateTime;

public class MetricsDatapoint {

    private long id;
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

    private long activeRequests;

    private int maxDbConnections;
    private int idleDbConnections;
    private int openDbConnections;

    private double uptime;
    private int daemonThreadCount;
    private int threadCount;
    private long totalStartedThreadCount;
    private double processCpuTime;
    private double systemCpuLoad;
    private double processCpuLoad;
    private long committedVirtualMemorySize;
    private long openFileDescriptorCount;
    private long maxFileDescriptorCount;
    private double systemLoadAverage;
    private int peakThreadCount;
    private int loadedClassCount;
    private long totalLoadedClassCount;
    private long unloadedClassCount;

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

    public long getActiveRequests() {
        return activeRequests;
    }

    public void setActiveRequests(long activeRequests) {
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getUptime() {
        return uptime;
    }

    public void setUptime(double uptime) {
        this.uptime = uptime;
    }

    public int getDaemonThreadCount() {
        return daemonThreadCount;
    }

    public void setDaemonThreadCount(int daemonThreadCount) {
        this.daemonThreadCount = daemonThreadCount;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public long getTotalStartedThreadCount() {
        return totalStartedThreadCount;
    }

    public void setTotalStartedThreadCount(long totalStartedThreadCount) {
        this.totalStartedThreadCount = totalStartedThreadCount;
    }

    public double getProcessCpuTime() {
        return processCpuTime;
    }

    public void setProcessCpuTime(double processCpuTime) {
        this.processCpuTime = processCpuTime;
    }

    public double getSystemCpuLoad() {
        return systemCpuLoad;
    }

    public void setSystemCpuLoad(double systemCpuLoad) {
        this.systemCpuLoad = systemCpuLoad;
    }

    public double getProcessCpuLoad() {
        return processCpuLoad;
    }

    public void setProcessCpuLoad(double processCpuLoad) {
        this.processCpuLoad = processCpuLoad;
    }

    public long getCommittedVirtualMemorySize() {
        return committedVirtualMemorySize;
    }

    public void setCommittedVirtualMemorySize(long committedVirtualMemorySize) {
        this.committedVirtualMemorySize = committedVirtualMemorySize;
    }

    public long getOpenFileDescriptorCount() {
        return openFileDescriptorCount;
    }

    public void setOpenFileDescriptorCount(long openFileDescriptorCount) {
        this.openFileDescriptorCount = openFileDescriptorCount;
    }

    public long getMaxFileDescriptorCount() {
        return maxFileDescriptorCount;
    }

    public void setMaxFileDescriptorCount(long maxFileDescriptorCount) {
        this.maxFileDescriptorCount = maxFileDescriptorCount;
    }

    public double getSystemLoadAverage() {
        return systemLoadAverage;
    }

    public void setSystemLoadAverage(double systemLoadAverage) {
        this.systemLoadAverage = systemLoadAverage;
    }

    public void setPeakThreadCount(int peakThreadCount) {
        this.peakThreadCount = peakThreadCount;
    }

    public int getPeakThreadCount() {
        return peakThreadCount;
    }

    public void setLoadedClassCount(int loadedClassCount) {
        this.loadedClassCount = loadedClassCount;
    }

    public int getLoadedClassCount() {
        return loadedClassCount;
    }

    public void setTotalLoadedClassCount(long totalLoadedClassCount) {
        this.totalLoadedClassCount = totalLoadedClassCount;
    }

    public long getTotalLoadedClassCount() {
        return totalLoadedClassCount;
    }

    public void setUnloadedClassCount(long unloadedClassCount) {
        this.unloadedClassCount = unloadedClassCount;
    }

    public long getUnloadedClassCount() {
        return unloadedClassCount;
    }
}
