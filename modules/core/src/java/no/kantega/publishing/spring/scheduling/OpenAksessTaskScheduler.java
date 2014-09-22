package no.kantega.publishing.spring.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

public class OpenAksessTaskScheduler extends ConcurrentTaskScheduler {

    @Autowired
    private ScheduledTaskRegistrar scheduledTaskRegistrar;

    @PostConstruct
    public void setup(){
        scheduledTaskRegistrar.setTaskScheduler(this);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(task);
    }

    @Override
    public ScheduledFuture schedule(Runnable task, Trigger trigger) {
        return super.schedule(task, trigger);
    }

    @Override
    public ScheduledFuture schedule(Runnable task, Date startTime) {
        return super.schedule(task, startTime);
    }

    @Override
    public ScheduledFuture scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        return super.scheduleAtFixedRate(task, startTime, period);
    }

    @Override
    public ScheduledFuture scheduleAtFixedRate(Runnable task, long period) {
        return super.scheduleAtFixedRate(task, period);
    }

    @Override
    public ScheduledFuture scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        return super.scheduleWithFixedDelay(task, startTime, delay);
    }

    @Override
    public ScheduledFuture scheduleWithFixedDelay(Runnable task, long delay) {
        return super.scheduleWithFixedDelay(task, delay);
    }

    @Override
    public void execute(Runnable task) {
        super.execute(task);
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        super.execute(task, startTimeout);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(task);
    }


}
