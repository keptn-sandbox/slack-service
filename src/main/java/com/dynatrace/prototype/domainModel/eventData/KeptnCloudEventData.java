package com.dynatrace.prototype.domainModel.eventData;

import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnCloudEventDataStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

public class KeptnCloudEventData {
    private String project;
    private String service;
    private String stage;

    private LinkedHashMap<String, String> labels;
    private String message;
    private KeptnCloudEventDataStatus status;
    private KeptnCloudEventDataResult result;

    public String getProject() {
        return project;
    }

    public String getService() {
        return service;
    }

    public String getStage() {
        return stage;
    }

    public LinkedHashMap<String, String> getLabels() {
        return labels;
    }

    public String getMessage() {
        return message;
    }

    public KeptnCloudEventDataStatus getStatus() {
        return status;
    }

    public KeptnCloudEventDataResult getResult() {
        return result;
    }

    /**
     * Return the value of the first matching key as a string or else null.
     *
     * @param key String array of keys to search.
     * @return String or null
     */
    protected String getValueOfLinkedHashMap(LinkedHashMap<String, ?> linkedHashMap, String... key) {
        String result = null;
        int i = 0;

        if (linkedHashMap != null && !linkedHashMap.isEmpty()) {
            while (result == null && i < key.length) {
                result = Objects.toString(linkedHashMap.get(key[i]));
                i++;
            }
        } else {
            System.out.println("WARN: Cannot perform linkedHashMap.get() when it is null or empty!");
        }

        return result;
    }

    /**
     * Checks if the given object is an ArrayList and if it returns it as such,
     * else it returns null.
     * @param object potential ArrayList
     * @return The object as ArrayList or null
     */
    protected ArrayList parseObjectToArrayList (Object object) {
        ArrayList result = null;

        if (object instanceof ArrayList) {
            result = (ArrayList) object;
        }

        return result;
    }

    /*
    //Project create
    private String projectName;
    private String gitRemoteURL;
    private String shipyard;

    private Object createdProject;
            private String projectName;
            private String gitRemoteURL;
            private String shipyard;

    //Service
    //basic KeptnCloudEvent

    //Approval
    private Object approval; //(Approval)
            private String/Enum pass;
            private String/Enum warning;

    //Deployment
    private Object configurationChange; //(ConfigurationChange)
            private String values; //collection with key value

    private Object deployment; //(DeploymentTriggeredData)
            private String[] deploymentURIsLocal;
            private String[] deploymentURIsPublic;
            private String/Enum deploymentstrategy;

    private Object deployment; //(DeploymentFinishedData)
            //#DeploymentTriggeredData +
            private String[] deploymentNames;
            private String gitCommit;

    //Test
    private Object deployment; //(TestTriggeredDeploymentDetails)
            private String[] deploymentURIsLocal;
            private String[] deploymentURIsPublic;

    private Object test; //(TestTriggeredDetails)
            private String/Enum teststrategy;

    private Object test; //(TestFinishedDetails)
            private String start;
            private String end;
            private String gitCommit;

    //Evaluation
    private Object deployment; //(Deployment)
            private String[] deploymentNames;

    private Object evaluation; //(Evaluation)
            private String start;
            private String end;
            private String timeframe;

    private Object test; //(Test)
            private String start;
            private String end;

    private Object evaluation; //(evaluationDetails)
            private String timeStart;
            private String timeEnd;
            private String result;
            private String score;
            private String sloFileContent;
            private Object indicatorResults; //(SLIEvaluationResult)
                    private int/float score;                //only integer?
                    private Object value; //(SLIResult)
                            private String metric;
                            private float value;        //only integer?
                            private boolean success;
                            private String message;
                    private String displayName;
                    private Object[] passTargets; //(SLITarget)
                            private String criteria;
                            private int/float targetValue;  //only integer?
                            private boolean violated;
                    private Object[] warningTargets; //(SLITarget)

                    private boolean keySli;
                    private String/Enum status;

            private String[] comparedEvents;
            private String gitCommit;

    //Release
    private Object deployment; //(deploymentFinishedData)
            private String/Enum deploymentstrategy;
            private String[] deploymentURIsLocal;
            private String[] deploymentURIsPublic;
            private String[] deploymentNames;
            private String gitCommit;

    private Object release; //(releaseData)
            private String gitCommit;

    //Get Action
    private Object problem; //(ProblemDetails)
            private String problemTitle;
            private String rootCause;

    private int actionIndex;

    private Object action; //(ActionInfo)
            private String name;
            private String action;
            private String description;
            private * *additional*; //(additional properties are allowed) //TODO: Danger

    //Action
    private Object action; //(ActionInfo)
            //#ActionInfo from Get Action

    private Object problem; //ProblemDetails)
            //#ProblemDetails from Get Action

    private Object action; //(ActionData)
            private String gitCommit;
    //TODO: need to continue: https://github.com/keptn/spec/blob/master/cloudevents.md
    //TODO: add propterties of Keptn cloud event Data here

    //Get-SLI
    private Object getSli; //(GetSLI)
            private String sliProvider;
            private String start;
            private String end;
            private String[] indicators;

    private Object customFilters; //(SLIFilter)
            private HashMap<String, String> key_value
                    private String key;
                    private String value;

    //PROBLEM
    private String state;
    private String problemID;
    private String problemTitle;
    private String problemDetails;
    private String PID;
    private String problemURL;
    private String impactedEntity;
    private String tags;

     */
}
