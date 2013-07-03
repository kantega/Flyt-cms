package org.kantega.openaksess.plugins.metrics.dao;

import org.joda.time.LocalDateTime;
import org.kantega.openaksess.plugins.metrics.MetricsModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DatabaseMetricsDao implements MetricsDao {

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void saveMetrics(MetricsModel model) {
        this.jdbcTemplate.update("INSERT INTO metrics VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", model);

    }

    @Override
    public List<MetricsModel> getMetrics(LocalDateTime start, LocalDateTime end) {
        MetricsModel actor = this.jdbcTemplate.queryForObject(                         // queryForList()?
                "select * from metrics where time > ? AND time < ? ",
                new Object[]{start, end},
                new RowMapper<MetricsModel>() {
                    public MetricsModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                        MetricsModel actor = new MetricsModel();
                        actor.setDatetime(new LocalDateTime(rs.getDate("time")));

                        actor.setMemoryInit(rs.getDouble("totalInit"));
                        actor.setMemoryCommitted(rs.getDouble("totalCommitted"));
                        actor.setMemoryUsed(rs.getDouble("totalUsed"));
                        actor.setMemoryMax(rs.getDouble("totalMax"));

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
                });

        return null;
    }

}
