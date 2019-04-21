package com.appdynamics.isdk.challenge;

import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.SDKClassMatchType;
import com.appdynamics.instrumentation.sdk.SDKStringMatchType;
import com.appdynamics.instrumentation.sdk.contexts.ISDKUserContext;
import com.appdynamics.instrumentation.sdk.template.AExit;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.IReflector;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.ReflectorException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by philipp on 01/12/17.
 */
public class MyAwesomeExitPlugin extends AExit {

    private IReflector _host;
    private IReflector _header;

    public MyAwesomeExitPlugin() {
        super();
        _host = getNewReflectionBuilder().accessFieldValue("hostname",false).build();
        _header = getNewReflectionBuilder().invokeInstanceMethod("getParameters", false).invokeInstanceMethod("put", false, Object.class.getCanonicalName(), Object.class.getCanonicalName()).build();
    }

    @Override
    public void marshalTransactionContext(String transactionContext, Object invokedObject, String s1, String s2, Object[] paramValues, Throwable throwable, Object o1, ISDKUserContext isdkUserContext) throws ReflectorException {
        _header.execute(paramValues[1].getClass().getClassLoader(), paramValues[1], new Object[]{}, new Object[]{"appd-header", transactionContext});
        getLogger().info(paramValues[1].toString());
        getLogger().info(transactionContext);
    }

    @Override
    public Map<String, String> identifyBackend(Object invokedObject, String s, String s1, Object[] objects, Throwable throwable, Object o1, ISDKUserContext isdkUserContext) throws ReflectorException {
        Map<String, String> idMap = new HashMap<String, String>();
        ClassLoader cl = invokedObject.getClass().getClassLoader();
        idMap.put("host",""+_host.execute(cl,invokedObject));
        return idMap;
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
        Rule r = new Rule.Builder("com.appdynamics.labs.isdk.apps.tcp.TcpClient")
                .classStringMatchType(SDKStringMatchType.EQUALS)
                .classMatchType(SDKClassMatchType.MATCHES_CLASS)
                .methodMatchString("sendMessage")
                .methodStringMatchType(SDKStringMatchType.EQUALS)
                .build();
        rules.add(r) ;
        return rules;
    }
}
