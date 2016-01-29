package no.kantega.publishing.spring.scheduling;

import no.kantega.publishing.api.configuration.SystemConfiguration;
import no.kantega.publishing.api.runtime.ServerType;
import no.kantega.publishing.api.scheduling.DisableOnServertype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.support.ScheduledMethodRunnable;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Custom TaskScheduler that extends the original by supporting annotations for
 * disabling certain jobs.
 * The {@code @DisableOnServertype annotation} can be used to say that the job annotated should not be run when the server
 * is running with a particular {@code ServerType}
 *
 * It is also possible to disable jobs with name.methodname.disable = true. e.g.
 * DatabaseCleanupJob.cleanDatabase.disable = true
 */
public class OpenAksessTaskScheduler extends ConcurrentTaskScheduler {

    private static final Logger log = LoggerFactory.getLogger(OpenAksessTaskScheduler.class);
    private final NoopScheduledFuture noopScheduledFuture = new NoopScheduledFuture();

    @Autowired
    private ServerType serverType;

    @Autowired
    private SystemConfiguration configuration;

    @PostConstruct
    public void init() {
        setConcurrentExecutor(Executors.newFixedThreadPool(configuration.getInt("OpenAksessTaskScheduler.numThreads", 4)));
    }

    @Override
    public ScheduledFuture schedule(Runnable task, Trigger trigger) {
        return shouldScheduleTask(task) ? super.schedule(task, trigger): noopScheduledFuture;
    }

    @Override
    public ScheduledFuture schedule(Runnable task, Date startTime) {
        return shouldScheduleTask(task) ? super.schedule(task, startTime): noopScheduledFuture;
    }

    @Override
    public ScheduledFuture scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        return shouldScheduleTask(task) ? super.scheduleAtFixedRate(task, startTime, period): noopScheduledFuture;
    }

    @Override
    public ScheduledFuture scheduleAtFixedRate(Runnable task, long period) {
        return shouldScheduleTask(task) ? super.scheduleAtFixedRate(task, period): noopScheduledFuture;
    }

    @Override
    public ScheduledFuture scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        return shouldScheduleTask(task) ? super.scheduleWithFixedDelay(task, startTime, delay): noopScheduledFuture;
    }

    @Override
    public ScheduledFuture scheduleWithFixedDelay(Runnable task, long delay) {
        return shouldScheduleTask(task) ? super.scheduleWithFixedDelay(task, delay): noopScheduledFuture;
    }

    @Override
    public void execute(Runnable task) {
        if (shouldScheduleTask(task)) {
            super.execute(task);
        }
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        if (shouldScheduleTask(task)) {
            super.execute(task, startTimeout);
        }
    }

    private boolean shouldScheduleTask(Runnable task) {
        boolean isDisabled = isDisabledByServertype(task) || isDisabledByConfig(task);
        return !isDisabled;
    }

    private boolean isDisabledByConfig(Runnable task) {
        if(task instanceof ScheduledMethodRunnable){
            ScheduledMethodRunnable runnable = (ScheduledMethodRunnable) task;
            Method method = runnable.getMethod();
            String configKey = method.getDeclaringClass().getSimpleName() + "." + method.getName() + ".disable";
            return configuration.getBoolean(configKey, false);
        }
        return false;
    }

    private boolean isDisabledByServertype(Runnable task) {
        if(task instanceof ScheduledMethodRunnable){
            ScheduledMethodRunnable runnable = (ScheduledMethodRunnable) task;
            Method method = runnable.getMethod();
            DisableOnServertype annotation = AnnotationUtils.findAnnotation(method, DisableOnServertype.class);
            if(annotation != null){
                ServerType disabledOnServertype = annotation.value();
                return disabledOnServertype == serverType;
            }
        }
        return false;
    }

    @Override
    public Future<?> submit(Runnable task) {
        return shouldScheduleTask(task) ? super.submit(task): noopScheduledFuture;
    }


    private static class NoopScheduledFuture implements ScheduledFuture<Object> {
        @Override
        public long getDelay(TimeUnit unit) {
            log.debug("NoopScheduledFuture.getDelay({}) called", unit);
            return Long.MAX_VALUE;
        }

        @Override
        public int compareTo(Delayed o) {
            log.debug("NoopScheduledFuture.compareTo({}) called", o);
            return 1;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            log.debug("NoopScheduledFuture.cancel({}) called", mayInterruptIfRunning);
            return true;
        }

        @Override
        public boolean isCancelled() {
            log.debug("NoopScheduledFuture.isCancelled() called");
            return false;
        }

        @Override
        public boolean isDone() {
            log.debug("NoopScheduledFuture.isDone() called");
            return true;
        }

        @Override
        public Object get() throws InterruptedException, ExecutionException {
            log.debug("NoopScheduledFuture.get() called");
            return null;
        }

        @Override
        public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            log.debug("NoopScheduledFuture.get({}, {}) called", timeout, unit);
            return null;
        }
    }
}
