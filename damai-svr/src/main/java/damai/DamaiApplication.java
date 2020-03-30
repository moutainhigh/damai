package damai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

// 这个注解是为了使该包内的过滤器生效。
@ServletComponentScan("rebue.sbs.smx.filter")
@SpringCloudApplication
@EnableFeignClients(basePackages = { "rebue.suc.svr.feign", "rebue.afc.svr.feign", "rebue.ord.svr.feign",
        "rebue.pnt.svr.feign", "rebue.onl.svr.feign", "rebue.slr.svr.feign", "rebue.jwt.svr.feign" })
public class DamaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DamaiApplication.class, args);
    }

}