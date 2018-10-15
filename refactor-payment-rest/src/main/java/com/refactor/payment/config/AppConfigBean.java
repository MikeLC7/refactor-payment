package com.refactor.payment.config;

import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@RefreshScope
@ConfigurationProperties
@Component("appConfigBean")
@Getter
@ToString
public class AppConfigBean {

    //---------------- test config -----------------------
    //@Value("${test.config}")
    private String testConfig;

}
