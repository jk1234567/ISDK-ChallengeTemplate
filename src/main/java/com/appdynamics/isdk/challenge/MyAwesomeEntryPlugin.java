package com.appdynamics.isdk.challenge;

import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.SDKClassMatchType;
import com.appdynamics.instrumentation.sdk.SDKStringMatchType;
import com.appdynamics.instrumentation.sdk.contexts.ISDKUserContext;
import com.appdynamics.instrumentation.sdk.template.AEntry;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.IReflector;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.ReflectorException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipp on 01/12/17.
 */
public class MyAwesomeEntryPlugin extends AEntry {

    private IReflector _header;

    public MyAwesomeEntryPlugin() {
        super();
            _header = getNewReflectionBuilder().invokeInstanceMethod("getParameters", false).invokeInstanceMethod("get", false, Object.class.getCanonicalName()).build();
    }

    @Override
    public String unmarshalTransactionContext(Object o, String s, String s1, Object[] paramValues, ISDKUserContext isdkUserContext) throws ReflectorException {
        getLogger().info(paramValues[0].toString());
        String header = ""+_header.execute(paramValues[0].getClass().getClassLoader(), paramValues[0], new Object[]{}, new Object[]{"appd-header"});
        getLogger().info("Found Header "+header);
        return header;
    }

    @Override
    public String getBusinessTransactionName(Object o, String s, String s1, Object[] objects, ISDKUserContext isdkUserContext) throws ReflectorException {
        return "TCP-SERVER.processMessage";
    }

    @Override
    public boolean isCorrelationEnabled() {
        return true;
    }

    @Override
    public boolean isCorrelationEnabledForOnMethodBegin() {
        return true;
    }

    @Override
    public List<Rule> initializeRules() {
        List<Rule> rules = new ArrayList<Rule>();
        Rule r = new Rule.Builder("com.appdynamics.labs.isdk.apps.tcp.TcpServer")
                .classStringMatchType(SDKStringMatchType.EQUALS)
                .classMatchType(SDKClassMatchType.MATCHES_CLASS)
                .methodMatchString("processMessage")
                .methodStringMatchType(SDKStringMatchType.EQUALS)
                .build();
        rules.add(r) ;
        return rules;
    }
}
