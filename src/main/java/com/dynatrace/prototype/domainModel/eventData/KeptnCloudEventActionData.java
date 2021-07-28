package com.dynatrace.prototype.domainModel.eventData;

import java.util.LinkedHashMap;

public class KeptnCloudEventActionData extends KeptnCloudEventData {
    private static final String PROBLEM_TITLE = "problemTitle", PROBLEM_ROOT_CAUSE = "rootCause", ACTION_NAME = "name", ACTION = "action", ACTION_DESCRIPTION = "description";
    private LinkedHashMap<String, ?> problem;
    private LinkedHashMap<String, ?> action;
    private int actionIndex;

    public LinkedHashMap<String, ?> getProblem() {
        return problem;
    }

    public LinkedHashMap<String, ?> getAction() {
        return action;
    }

    public int getActionIndex() {
        return actionIndex;
    }

    public String getProblemTitle() {
        return getValueOfLinkedHashMap(problem, PROBLEM_TITLE);
    }

    public String getProblemRootCause() {
        return getValueOfLinkedHashMap(problem, PROBLEM_ROOT_CAUSE);
    }

    public String getActionName() {
        return getValueOfLinkedHashMap(action, ACTION_NAME);
    }

    public String getRealAction() {
        return getValueOfLinkedHashMap(action, ACTION);
    }

    public String getActionDescription() {
        return getValueOfLinkedHashMap(action, ACTION_DESCRIPTION);
    }

    public LinkedHashMap<String, ?> getAdditionalActionValues() {
        LinkedHashMap<String, ?> additionalValues = null;

        if (action != null) {
            additionalValues = action;
            additionalValues.remove(ACTION_NAME);
            additionalValues.remove(ACTION);
            additionalValues.remove(ACTION_DESCRIPTION);
        }

        return additionalValues;
    }

/*
    TODO: add test, deployment and evaluation classes probably for all events (evaluation 2 different types)
    private Object problem; //(ProblemDetails)
            private String problenTitel;
            private String rootCause;

    private int actionIndex;

    private Object action; //(ActionInfo)
            private String name;
            private String action;
            private String description;
            private * *additional*; //(additional properties are allowed) //TODO: Danger

     */
    //TODO: maybe merch witch KeptnCloudEventActionData
}

