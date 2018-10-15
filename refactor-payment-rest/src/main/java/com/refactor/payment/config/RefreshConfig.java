package com.refactor.payment.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.stereotype.Component;

@Component
//@EnableApolloConfig
public class RefreshConfig {

    @Autowired
    private RefreshScope refreshScope;

   /* @ApolloConfigChangeListener
    private void onChange(ConfigChangeEvent changeEvent) {

        String appConfigBean = "appConfigBean";
        refreshScope.refresh(appConfigBean);

    }*/


}
