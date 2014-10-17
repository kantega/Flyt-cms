package no.kantega.publishing.spring.scheduling;

import no.kantega.publishing.api.runtime.ServerType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class ScheduleConfig {

    @Bean
    public ServerType serverType(){
        return new Random().nextBoolean() ? ServerType.MASTER : ServerType.SLAVE;
    }
}
