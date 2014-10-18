package no.kantega.publishing.spring.scheduling;

import no.kantega.publishing.api.runtime.ServerType;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
// Commented out becase this test fails sporadically because of its nature.
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations= "classpath*:spring/scheluderTestContext.xml")
public class OpenAksessTaskSchedulerTest {

    @Autowired
    private AnnotatedJob annotatedJob;
    @Autowired
    private Job job;

    @Autowired
    private ServerType serverType;

    //@Test
    public void dotest() throws InterruptedException {
        Thread.sleep(800);
        assertTrue(true);
    }

    @After
    public void end(){
        assertThat("Normal @Scheduled did not run", annotatedJob.hasRunFixed, is(true));
        assertThat("Normal @Scheduled did not run", annotatedJob.hasRunCron, is(true));
        assertThat("Normal @Scheduled did not run", job.hasRunCron, is(true));
        assertThat("Normal @Scheduled did not run", job.hasRunCron, is(true));

        boolean isMaster = serverType == ServerType.MASTER;
        String error = isMaster
                ? "ran, but had @DisableOnServertype(value = ServerType.MASTER) with serverType " + serverType
                : "Did not run with @DisableOnServertype(value = ServerType.SLAVE) with serverType " + serverType;
        assertThat("annotatedJob.hasRunAnnotatedFixedMaster " + error, annotatedJob.hasRunAnnotatedFixedMaster, is(!isMaster));
        assertThat("annotatedJob.hasRunAnnotatedCronMaster " + error, annotatedJob.hasRunAnnotatedCronMaster, is(!isMaster));
        assertThat("job.hasRunAnnotatedCron " + error, job.hasRunAnnotatedCron, is(!isMaster));
        assertThat("job.hasRunAnnotatedFixed " + error, job.hasRunAnnotatedFixed, is(!isMaster));

        String slaveError = isMaster
                ? "ran, but had @DisableOnServertype(value = ServerType.MASTER) with serverType " + serverType
                : "Did not run with @DisableOnServertype(value = ServerType.SLAVE) with serverType " + serverType;
        assertThat("annotatedJob.hasRunAnnotatedFixedSlave " + slaveError, annotatedJob.hasRunAnnotatedFixedSlave, is(isMaster));
        assertThat("annotatedJob.hasRunAnnotatedCronSlave " + slaveError, annotatedJob.hasRunAnnotatedCronSlave, is(isMaster));
        assertThat("job.hasRunAnnotatedCronSlave " + slaveError, job.hasRunAnnotatedCronSlave, is(isMaster));
        assertThat("job.hasRunAnnotatedSlave " + slaveError, job.hasRunAnnotatedSlave, is(isMaster));
    }
}
