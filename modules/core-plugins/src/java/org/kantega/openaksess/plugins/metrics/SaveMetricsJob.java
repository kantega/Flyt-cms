package org.kantega.openaksess.plugins.metrics;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.*;
import org.joda.time.LocalDateTime;
import org.kantega.openaksess.plugins.metrics.dao.MetricsDao;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.SortedMap;

public class SaveMetricsJob {

    private MetricsDao dao;

    private final Clock clock;
    private final VirtualMachineMetrics vm;
    private MetricsRegistry registry;

    public SaveMetricsJob(Clock clock, VirtualMachineMetrics vm, MetricsRegistry registry) {
        this.clock = clock;
        this.vm = vm;
        this.registry = registry;
    }

    public SaveMetricsJob() {
        this(Clock.defaultClock(), VirtualMachineMetrics.getInstance(), Metrics.defaultRegistry());
    }

    @Scheduled(fixedRate = 5000)              // (cron = "${plugin.oastatistics.jobcron:0 0/5 * * * ?")
    public void autoSaveMetrics(){
        MetricsModel model = new MetricsModel();

        model.setDatetime(new LocalDateTime());                                  // 22 metrics

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

//        model.setIdleDbConnections();
//        model.setMaxDbConnections();
//        model.setOpenDbConnections();

//        model.setActiveRequests();
//
//        model.setBadRequests();
//        model.setOk();
//        model.setOther();
//        model.setNoContent();
//        model.setNotFound();
//        model.setServerError();
//        model.setCreated();


        for (Map.Entry<String, SortedMap<MetricName, Metric>> entry : registry.groupedMetrics().entrySet()) {
            if (entry.getKey().equals("no.kantega.publishing.common.util.database.dbConnectionFactory")) {
                for (Map.Entry<MetricName, Metric> subEntry : entry.getValue().entrySet()) {
                    if(subEntry.getKey().getName().equals("value")){

                    }
                }
            }
        }

//        dao.saveMetrics(model);
    }

    public MetricsDao getDao() {
        return dao;
    }

    public void setDao(MetricsDao dao) {
        this.dao = dao;
    }
}
