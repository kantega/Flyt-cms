package org.kantega.openaksess.plugins.metrics.dao;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kantega.openaksess.plugins.metrics.MetricsDatapoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:testContext.xml")
public class DatabaseMetricsDaoTest {

    @Autowired
    private MetricsDao metricsDao;

    @Test
    public void shouldSaveToDb(){
        LocalDateTime now = LocalDateTime.now();
        List<MetricsDatapoint> metrics = metricsDao.getMetrics(now.minusDays(1), now.plusDays(1));
        assertEquals(0, metrics.size());


        MetricsDatapoint model = new MetricsDatapoint();
        model.setActiveRequests(1);
        model.setCapturetime(now);
        model.setHeapCommitted(123);
        model.setHeapInit(12);
        model.setHeapMax(222);
        model.setHeapUsage(123);
        model.setHeapUsed(124);
        model.setIdleDbConnections(0);
        model.setMaxDbConnections(12);
        model.setMemoryCommitted(222);
        model.setMemoryInit(12);
        model.setMemoryMax(321);
        model.setMemoryUsed(213);
        model.setNonHeapUsage(123);
        model.setOpenDbConnections(2);

        model.setUptime(1234);
        model.setDaemonThreadCount(12);
        model.setThreadCount(4);
        model.setTotalStartedThreadCount(1);
        model.setProcessCpuTime(135);
        model.setSystemCpuLoad(135);
        model.setProcessCpuLoad(55);
        model.setCommittedVirtualMemorySize(41);
        model.setOpenFileDescriptorCount(111);
        model.setMaxFileDescriptorCount(1111);
        model.setSystemLoadAverage(133);
        model.setPeakThreadCount(333);
        model.setLoadedClassCount(51);
        model.setTotalLoadedClassCount(123);
        model.setUnloadedClassCount(513);
        
        model = metricsDao.saveMetrics(model);
        assertNotNull("ID was null", model.getId());

        metrics = metricsDao.getMetrics(now.minusDays(1), now.plusDays(1));
        assertEquals(1, metrics.size());
        MetricsDatapoint saved = metrics.get(0);
        assertEquals(model.getId(), saved.getId());
        assertEquals(model.getCapturetime(), saved.getCapturetime());
        assertEquals(model.getActiveRequests(), saved.getActiveRequests(), 0.5);
        assertEquals(model.getCapturetime(), saved.getCapturetime());
        assertEquals(model.getHeapCommitted(), saved.getHeapCommitted(), 0.5);
        assertEquals(model.getHeapInit(), saved.getHeapInit(), 0.5);
        assertEquals(model.getHeapMax(), saved.getHeapMax(), 0.5);
        assertEquals(model.getHeapUsage(), saved.getHeapUsage(), 0.5);
        assertEquals(model.getHeapUsed(), saved.getHeapUsed(), 0.5);
        assertEquals(model.getIdleDbConnections(), saved.getIdleDbConnections(), 0.5);
        assertEquals(model.getMaxDbConnections(), saved.getMaxDbConnections(), 0.5);
        assertEquals(model.getMemoryCommitted(), saved.getMemoryCommitted(), 0.5);
        assertEquals(model.getMemoryInit(), saved.getMemoryInit(), 0.5);
        assertEquals(model.getMemoryMax(), saved.getMemoryMax(), 0.5);
        assertEquals(model.getMemoryUsed(), saved.getMemoryUsed(), 0.5);
        assertEquals(model.getNonHeapUsage(), saved.getNonHeapUsage(), 0.5);
        assertEquals(model.getOpenDbConnections(), saved.getOpenDbConnections(), 0.5);
        assertEquals(model.getUptime(), saved.getUptime(), 0.5);
        assertEquals(model.getDaemonThreadCount(), saved.getDaemonThreadCount(), 0.5);
        assertEquals(model.getThreadCount(), saved.getThreadCount(), 0.5);
        assertEquals(model.getTotalStartedThreadCount(), saved.getTotalStartedThreadCount(), 0.5);
        assertEquals(model.getProcessCpuTime(), saved.getProcessCpuTime(), 0.5);
        assertEquals(model.getSystemCpuLoad(), saved.getSystemCpuLoad(), 0.5);
        assertEquals(model.getProcessCpuLoad(), saved.getProcessCpuLoad(), 0.5);
        assertEquals(model.getCommittedVirtualMemorySize(), saved.getCommittedVirtualMemorySize(), 0.5);
        assertEquals(model.getOpenFileDescriptorCount(), saved.getOpenFileDescriptorCount(), 0.5);
        assertEquals(model.getMaxFileDescriptorCount(), saved.getMaxFileDescriptorCount(), 0.5);
        assertEquals(model.getSystemLoadAverage(), saved.getSystemLoadAverage(), 0.5);
        assertEquals(model.getUptime(), saved.getUptime(), 0.5);
        assertEquals(model.getDaemonThreadCount(), saved.getDaemonThreadCount(), 0.5);
        assertEquals(model.getThreadCount(), saved.getThreadCount(), 0.5);
        assertEquals(model.getTotalStartedThreadCount(), saved.getTotalStartedThreadCount(), 0.5);
        assertEquals(model.getProcessCpuTime(), saved.getProcessCpuTime(), 0.5);
        assertEquals(model.getSystemCpuLoad(), saved.getSystemCpuLoad(), 0.5);
        assertEquals(model.getProcessCpuLoad(), saved.getProcessCpuLoad(), 0.5);
        assertEquals(model.getCommittedVirtualMemorySize(), saved.getCommittedVirtualMemorySize(), 0.5);
        assertEquals(model.getOpenFileDescriptorCount(), saved.getOpenFileDescriptorCount(), 0.5);
        assertEquals(model.getMaxFileDescriptorCount(), saved.getMaxFileDescriptorCount(), 0.5);
        assertEquals(model.getSystemLoadAverage(), saved.getSystemLoadAverage(), 0.5);
        assertEquals(model.getPeakThreadCount(), saved.getPeakThreadCount(), 0.5);
        assertEquals(model.getLoadedClassCount(), saved.getLoadedClassCount(), 0.5);
        assertEquals(model.getTotalLoadedClassCount(), saved.getTotalLoadedClassCount(), 0.5);
        assertEquals(model.getUnloadedClassCount(), saved.getUnloadedClassCount(), 0.5);
    }
}