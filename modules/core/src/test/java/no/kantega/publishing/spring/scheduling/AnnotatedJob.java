package no.kantega.publishing.spring.scheduling;

import no.kantega.publishing.api.runtime.ServerType;
import no.kantega.publishing.api.scheduling.DisableOnServertype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class AnnotatedJob {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public boolean hasRunFixed = false;
    public boolean hasRunCron = false;
    public boolean hasRunAnnotatedCronMaster = false;
    public boolean hasRunAnnotatedFixedMaster = false;
    public boolean hasRunAnnotatedFixedSlave = false;
    public boolean hasRunAnnotatedCronSlave = false;

    @Scheduled(fixedRate = 100)
    public void doSomethingFixedRate(){
        hasRunFixed = true;
        log.info("doSomethingFixedRate");
    }

    @Scheduled(cron = "* * * * * ?")
    public void doSomethingCron(){
        hasRunCron = true;
        log.info("doSomethingCron");
    }

    @DisableOnServertype(value = ServerType.MASTER)
    @Scheduled(fixedRate = 150)
    public void doNotSomethingFixedRateMaster(){
        hasRunAnnotatedFixedMaster = true;
        log.info("doNotSomethingFixedRateMaster");
    }

    @DisableOnServertype(ServerType.MASTER)
    @Scheduled(cron = "* * * * * ?")
    public void doNotSomethingCronMaster(){
        hasRunAnnotatedCronMaster = true;
        log.info("doNotSomethingCronMaster");
    }

    @DisableOnServertype(value = ServerType.SLAVE)
    @Scheduled(fixedRate = 150)
    public void doNotSomethingFixedRateSlave(){
        hasRunAnnotatedFixedSlave = true;
        log.info("doNotSomethingFixedRateSlave");
    }

    @DisableOnServertype(ServerType.SLAVE)
    @Scheduled(cron = "* * * * * ?")
    public void doNotSomethingCronSlave(){
        hasRunAnnotatedCronSlave = true;
        log.info("doNotSomethingCronSlave");
    }
}
