package no.kantega.publishing.spring.scheduling;

import no.kantega.publishing.api.runtime.ServerType;
import no.kantega.publishing.api.scheduling.DisableOnServertype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Job {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public boolean hasRun;
    public boolean hasRunCron;
    public boolean hasRunAnnotatedCron;
    public boolean hasRunAnnotatedFixed;
    public boolean hasRunAnnotatedCronSlave;
    public boolean hasRunAnnotatedSlave;

    public void doSomethingFixedRate(){
        hasRun = true;
        log.info("doSomethingFixedRate");
    }

    public void doSomethingCron(){
        hasRunCron = true;
        log.info("doSomethingCron");
    }

    @DisableOnServertype(value = ServerType.MASTER)
    public void doNotSomethingFixedRate(){
        hasRunAnnotatedFixed = true;
        log.info("doNotSomethingFixedRate");
    }

    @DisableOnServertype(value = ServerType.MASTER)
    public void doNotSomethingCron(){
        hasRunAnnotatedCron = true;
        log.info("doNotSomethingCronMaster");
    }

    @DisableOnServertype(value = ServerType.SLAVE)
    public void doNotSomethingFixedRateSlave(){
        hasRunAnnotatedSlave = true;
        log.info("doNotSomethingFixedRateSlave");
    }

    @DisableOnServertype(value = ServerType.SLAVE)
    public void doNotSomethingCronSlave(){
        hasRunAnnotatedCronSlave = true;
        log.info("doNotSomethingCronSlave");
    }
}
