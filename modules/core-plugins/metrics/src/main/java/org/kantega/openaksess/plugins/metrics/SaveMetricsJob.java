package org.kantega.openaksess.plugins.metrics;

import com.sun.management.UnixOperatingSystemMXBean;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.*;
import org.kantega.openaksess.plugins.metrics.dao.MetricsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Map;

public class SaveMetricsJob {
    private static final Logger log = LoggerFactory.getLogger(SaveMetricsJob.class);
    private MetricsDao dao;

    private final VirtualMachineMetrics vm;
    private final MetricsRegistry registry;

    public SaveMetricsJob(VirtualMachineMetrics vm, MetricsRegistry registry) {
        this.vm = vm;
        this.registry = registry;
    }

    public SaveMetricsJob() {
        this(VirtualMachineMetrics.getInstance(), Metrics.defaultRegistry());
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void autoSaveMetrics(){
        log.info("Started gathering metrics");
        MetricsDatapoint model = new MetricsDatapoint();

        model.setMemoryInit(vm.totalInit());
        model.setMemoryCommitted(vm.totalCommitted());
        model.setMemoryUsed(vm.totalUsed());
        model.setMemoryMax(vm.totalMax());

        model.setHeapInit(vm.heapInit());
        model.setHeapCommitted(vm.heapCommitted());
        model.setHeapUsed(vm.heapUsed());
        model.setHeapMax(vm.heapMax());

        model.setHeapUsage(vm.heapUsage());
        model.setNonHeapUsage(vm.nonHeapUsage());
        model.setUptime(vm.uptime());

        model.setDaemonThreadCount(vm.daemonThreadCount());
        model.setThreadCount(vm.threadCount());

        setOperatingSystemMetrics(model);

        setThreadMetrisc(model);

        setClassloadingMetrics(model);

        setDbMetrics(model);

        setWebMetrics(model);

        log.info("Saving metrics datapoint");
        dao.saveMetrics(model);
    }

    private void setWebMetrics(MetricsDatapoint model) {
        Map<MetricName, Metric> webMetrics = registry.groupedMetrics().get("com.yammer.metrics.web.WebappMetricsFilter");
        if (webMetrics != null) {
            for (Map.Entry<MetricName, Metric> webMetric : webMetrics.entrySet()) {
                if(webMetric.getKey().getName().contains("activeRequests")){
                    model.setActiveRequests(((Counter)webMetric.getValue()).count());
                }
            }
        }
    }

    private void setDbMetrics(MetricsDatapoint model) {
        Map<MetricName, Metric> dbMetrics = registry.groupedMetrics().get("no.kantega.publishing.common.util.database.dbConnectionFactory");
        if (dbMetrics != null) {
            for (Map.Entry<MetricName, Metric> dbMetric : dbMetrics.entrySet()) {
                if(dbMetric.getKey().getName().contains("idle-connections")){
                    model.setIdleDbConnections((Integer) ((Gauge)dbMetric.getValue()).value());
                } else if(dbMetric.getKey().getName().contains("max-connections")){
                    model.setMaxDbConnections((Integer) ((Gauge)dbMetric.getValue()).value());
                } else if(dbMetric.getKey().getName().contains("open-connections")){
                    model.setOpenDbConnections((Integer) ((Gauge)dbMetric.getValue()).value());
                }
            }
        }
    }

    private void setClassloadingMetrics(MetricsDatapoint model) {
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        model.setLoadedClassCount(classLoadingMXBean.getLoadedClassCount());
        model.setTotalLoadedClassCount(classLoadingMXBean.getTotalLoadedClassCount());
        model.setUnloadedClassCount(classLoadingMXBean.getUnloadedClassCount());
    }

    private void setThreadMetrisc(MetricsDatapoint model) {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        model.setTotalStartedThreadCount(threadMXBean.getTotalStartedThreadCount());
        model.setThreadCount(threadMXBean.getThreadCount());
        model.setPeakThreadCount(threadMXBean.getPeakThreadCount());
        model.setDaemonThreadCount(threadMXBean.getDaemonThreadCount());
    }

    private void setOperatingSystemMetrics(MetricsDatapoint model) {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();

        if(operatingSystemMXBean instanceof com.sun.management.OperatingSystemMXBean){
            com.sun.management.OperatingSystemMXBean sunMBean = (com.sun.management.OperatingSystemMXBean) operatingSystemMXBean;
            model.setProcessCpuLoad(sunMBean.getProcessCpuLoad());
            model.setProcessCpuTime(sunMBean.getProcessCpuTime());
            model.setCommittedVirtualMemorySize(sunMBean.getCommittedVirtualMemorySize());
            model.setSystemCpuLoad(sunMBean.getSystemCpuLoad());
        }
        if(operatingSystemMXBean instanceof UnixOperatingSystemMXBean){
            UnixOperatingSystemMXBean unixMBean = ((UnixOperatingSystemMXBean)operatingSystemMXBean);
            model.setOpenFileDescriptorCount(unixMBean.getOpenFileDescriptorCount());
            model.setMaxFileDescriptorCount(unixMBean.getMaxFileDescriptorCount());
        }
        model.setSystemLoadAverage(operatingSystemMXBean.getSystemLoadAverage());
    }

    public void setDao(MetricsDao dao) {
        this.dao = dao;
    }
}
