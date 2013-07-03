package org.kantega.openaksess.plugins.metrics.dao;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kantega.openaksess.plugins.metrics.MetricsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:testContext.xml")
public class DatabaseMetricsDaoTest {

    @Autowired
    private MetricsDao metricsDao;

    @Test
    public void shouldSaveToDb(){
        LocalDateTime now = LocalDateTime.now();
        MetricsModel model = new MetricsModel();
        model.setActiveRequests(1);
        model.setBadRequests(2);
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
        model.setNotFound(22);
        model.setOk(124);
        model.setOpenDbConnections(2);
        model.setServerError(41);
        metricsDao.saveMetrics(model);

        List<MetricsModel> metrics = metricsDao.getMetrics(now.minusDays(1), now.plusDays(1));
        assertEquals(1, metrics.size());
        MetricsModel saved = metrics.get(0);
        assertEquals(model.getActiveRequests(), saved.getActiveRequests(), 0.5);
        assertEquals(model.getBadRequests(), saved.getBadRequests(), 0.5);
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
        assertEquals(model.getNotFound(), saved.getNotFound(), 0.5);
        assertEquals(model.getOk(), saved.getOk(), 0.5);
        assertEquals(model.getOpenDbConnections(), saved.getOpenDbConnections(), 0.5);
        assertEquals(model.getServerError(), saved.getServerError(), 0.5);
    }
}