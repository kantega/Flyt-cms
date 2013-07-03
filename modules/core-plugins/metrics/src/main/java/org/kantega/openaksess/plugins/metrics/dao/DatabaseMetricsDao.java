package org.kantega.openaksess.plugins.metrics.dao;

import org.joda.time.LocalDateTime;
import org.kantega.openaksess.plugins.metrics.MetricsModel;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

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
    public void saveMetrics(MetricsModel model) {
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
        params.put("badRequests", model.getBadRequests());
        params.put("ok", model.getOk());
        params.put("serverError", model.getServerError());
        params.put("notFound", model.getNotFound());
        this.jdbcTemplate.update("INSERT INTO metrics(capturetime,memoryInit,memoryMax,memoryUsed,memoryCommitted,heapInit,heapMax,heapUsed,heapCommitted,heapUsage,nonHeapUsage,activeRequests,maxDbConnections,idleDbConnections,openDbConnections,badRequests,ok,serverError,notFound) " +
                                            "VALUES (:capturetime,:memoryInit,:memoryMax,:memoryUsed,:memoryCommitted,:heapInit,:heapMax,:heapUsed,:heapCommitted,:heapUsage,:nonHeapUsage,:activeRequests,:maxDbConnections,:idleDbConnections,:openDbConnections,:badRequests,:ok,:serverError,:notFound)", params);
    }

    @Override
    public List<MetricsModel> getMetrics(LocalDateTime start, LocalDateTime end) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("start", start.toDate());
        params.put("end", end.toDate());
        return jdbcTemplate.query("select * from metrics where capturetime > :start AND capturetime < :end ",
                params, rowMapper);
    }

    private final RowMapper<MetricsModel> rowMapper = new RowMapper<MetricsModel>() {
        public MetricsModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            MetricsModel actor = new MetricsModel();
            actor.setCapturetime(new LocalDateTime(rs.getDate("capturetime")));

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

//                        actor.setIdleDbConnections();
//                        actor.setMaxDbConnections();
//                        actor.setOpenDbConnections();
//
//                        actor.setActiveRequests();
//
//                        actor.setBadRequests();
//                        actor.setOk();
//                        actor.setOther();
//                        actor.setNoContent();
//                        actor.setNotFound();
//                        actor.setServerError();
//                        actor.setCreated();

            return actor;
        }
    };
}
