package org.kantega.openaksess.plugins.metrics.dao;

import org.joda.time.LocalDateTime;
import org.kantega.openaksess.plugins.metrics.MetricsDatapoint;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class DatabaseMetricsDao implements MetricsDao {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public MetricsDatapoint saveMetrics(MetricsDatapoint model) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("capturetime", model.getCapturetime().toDate());
        params.put("memoryInit", model.getMemoryInit());
        params.put("memoryMax", model.getMemoryMax());
        params.put("memoryUsed", model.getMemoryUsed());
        params.put("memoryCommitted", model.getMemoryCommitted());
        params.put("heapInit", model.getHeapInit());
        params.put("heapMax", model.getHeapMax());
        params.put("heapUsed", model.getHeapUsed());
        params.put("heapCommitted", model.getHeapCommitted());
        params.put("heapUsage", model.getHeapUsage());
        params.put("nonHeapUsage", model.getNonHeapUsage());
        params.put("activeRequests", model.getActiveRequests());
        params.put("maxDbConnections", model.getMaxDbConnections());
        params.put("idleDbConnections", model.getIdleDbConnections());
        params.put("openDbConnections", model.getOpenDbConnections());
        params.put("uptime", model.getUptime());
        params.put("daemonThreadCount", model.getDaemonThreadCount());
        params.put("threadCount", model.getThreadCount());
        params.put("totalStartedThreadCount", model.getTotalStartedThreadCount());
        params.put("processCpuTime", model.getProcessCpuTime());
        params.put("systemCpuLoad", model.getSystemCpuLoad());
        params.put("processCpuLoad", model.getProcessCpuLoad());
        params.put("committedVirtualMemorySize", model.getCommittedVirtualMemorySize());
        params.put("openFileDescriptorCount", model.getOpenFileDescriptorCount());
        params.put("maxFileDescriptorCount", model.getMaxFileDescriptorCount());
        params.put("systemLoadAverage", model.getSystemLoadAverage());
        params.put("peakThreadCount", model.getPeakThreadCount());
        params.put("loadedClassCount", model.getLoadedClassCount());
        params.put("totalLoadedClassCount", model.getTotalLoadedClassCount());
        params.put("unloadedClassCount", model.getUnloadedClassCount());

        KeyHolder kh = new GeneratedKeyHolder();
        this.jdbcTemplate.update("INSERT INTO metrics(capturetime,memoryInit,memoryMax,memoryUsed,memoryCommitted,heapInit,heapMax,heapUsed,heapCommitted,heapUsage,nonHeapUsage,activeRequests,maxDbConnections,idleDbConnections,openDbConnections,uptime,daemonThreadCount,threadCount,totalStartedThreadCount,processCpuTime,systemCpuLoad,processCpuLoad,committedVirtualMemorySize,openFileDescriptorCount,maxFileDescriptorCount,systemLoadAverage,peakThreadCount,loadedClassCount,totalLoadedClassCount,unloadedClassCount) " +
                                            "VALUES (:capturetime,:memoryInit,:memoryMax,:memoryUsed,:memoryCommitted,:heapInit,:heapMax,:heapUsed,:heapCommitted,:heapUsage,:nonHeapUsage,:activeRequests,:maxDbConnections,:idleDbConnections,:openDbConnections,:uptime,:daemonThreadCount,:threadCount,:totalStartedThreadCount,:processCpuTime,:systemCpuLoad,:processCpuLoad,:committedVirtualMemorySize,:openFileDescriptorCount,:maxFileDescriptorCount,:systemLoadAverage,:peakThreadCount,:loadedClassCount,:totalLoadedClassCount,:unloadedClassCount)", new MapSqlParameterSource(params), kh);
        model.setId(kh.getKey().longValue());
        return model;
    }

    @Override
    public List<MetricsDatapoint> getMetrics(LocalDateTime start, LocalDateTime end) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("start", start.toDate());
        params.put("end", end.toDate());
        return jdbcTemplate.query("select * from metrics where capturetime > :start AND capturetime < :end ",
                params, rowMapper);
    }

    private final RowMapper<MetricsDatapoint> rowMapper = new RowMapper<MetricsDatapoint>() {
        public MetricsDatapoint mapRow(ResultSet rs, int rowNum) throws SQLException {
            MetricsDatapoint actor = new MetricsDatapoint();
            actor.setId(rs.getLong("Id"));
            actor.setCapturetime(new LocalDateTime(rs.getTimestamp("capturetime").getTime()));

            actor.setMemoryInit(rs.getDouble("memoryInit"));
            actor.setMemoryCommitted(rs.getDouble("memoryCommitted"));
            actor.setMemoryUsed(rs.getDouble("memoryUsed"));
            actor.setMemoryMax(rs.getDouble("memoryMax"));

            actor.setHeapInit(rs.getDouble("heapInit"));
            actor.setHeapCommitted(rs.getDouble("heapCommitted"));
            actor.setHeapUsed(rs.getDouble("heapUsed"));
            actor.setHeapMax(rs.getDouble("heapMax"));

            actor.setHeapUsage(rs.getDouble("heapUsage"));
            actor.setNonHeapUsage(rs.getDouble("nonHeapUsage"));

            actor.setUptime(rs.getDouble("uptime"));
            actor.setDaemonThreadCount(rs.getInt("daemonThreadCount"));
            actor.setThreadCount(rs.getInt("threadCount"));
            actor.setTotalStartedThreadCount(rs.getInt("totalStartedThreadCount"));
            actor.setProcessCpuTime(rs.getDouble("processCpuTime"));
            actor.setSystemCpuLoad(rs.getDouble("systemCpuLoad"));
            actor.setProcessCpuLoad(rs.getDouble("processCpuLoad"));
            actor.setCommittedVirtualMemorySize(rs.getLong("committedVirtualMemorySize"));
            actor.setOpenFileDescriptorCount(rs.getLong("openFileDescriptorCount"));
            actor.setMaxFileDescriptorCount(rs.getLong("maxFileDescriptorCount"));
            actor.setSystemLoadAverage(rs.getDouble("systemLoadAverage"));
            actor.setPeakThreadCount(rs.getInt("peakThreadCount"));
            actor.setLoadedClassCount(rs.getInt("loadedClassCount"));
            actor.setTotalLoadedClassCount(rs.getInt("totalLoadedClassCount"));
            actor.setUnloadedClassCount(rs.getLong("unloadedClassCount"));

            actor.setActiveRequests(rs.getInt("activeRequests"));

            actor.setMaxDbConnections(rs.getInt("maxDbConnections"));
            actor.setIdleDbConnections(rs.getInt("idleDbConnections"));
            actor.setOpenDbConnections(rs.getInt("openDbConnections"));
            return actor;
        }
    };
}
