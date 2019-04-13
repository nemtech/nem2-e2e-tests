package io.nem.automationHelpers.common;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {
    private Map<String, Object> scenarioContext;

    public ScenarioContext(){
        scenarioContext = new HashMap<>();
    }

    public void setContext(String key, Object value) {
        scenarioContext.put(key, value);
    }

    public <T> T getContext(String key){
        return (T)scenarioContext.get(key);
    }

    public Boolean isContains(String key){
        return scenarioContext.containsKey(key);
    }

}
