package no.kantega.publishing.spring.scheduling;

import no.kantega.publishing.api.configuration.SystemConfiguration;
import no.kantega.publishing.api.runtime.ServerType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class ScheduleConfig {

    @Bean
    public ServerType serverType(){
        return new Random().nextBoolean() ? ServerType.MASTER : ServerType.SLAVE;
    }

    @Bean
    public SystemConfiguration configuration(){
        SystemConfiguration mock = mock(SystemConfiguration.class);
        when(mock.getBoolean("AnnotatedJob.dohasRunAnnotatedCronConfig.disable", false)).thenReturn(true);
        when(mock.getBoolean("Job.doNotSomethingCronConfig.disable", false)).thenReturn(true);
        return mock;
    }
}
