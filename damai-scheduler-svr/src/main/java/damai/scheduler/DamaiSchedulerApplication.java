package damai.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringCloudApplication
@EnableScheduling
@EnableFeignClients(basePackages = { "damai.svr.feign", "rebue.pnt.svr.feign" })
public class DamaiSchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DamaiSchedulerApplication.class, args);
    }
}
