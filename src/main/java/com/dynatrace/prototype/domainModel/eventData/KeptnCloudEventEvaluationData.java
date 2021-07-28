package com.dynatrace.prototype.domainModel.eventData;

import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnCloudEventSLIResult;
import com.dynatrace.prototype.domainModel.KeptnCloudEventSLITarget;
import com.dynatrace.prototype.domainModel.SLIEvaluationResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class KeptnCloudEventEvaluationData extends KeptnCloudEventData {
    private LinkedHashMap<String, ?> test;
    private LinkedHashMap<String, ?> deployment;
    private LinkedHashMap<String, ?> evaluation;

    public LinkedHashMap<String, ?> getTest() {
        return test;
    }

    public LinkedHashMap<String, ?> getDeployment() {
        return deployment;
    }

    public LinkedHashMap<String, ?> getEvaluation() {
        return evaluation;
    }

    public String getEvaluationStart() {
        return getValueOfLinkedHashMap(evaluation, "start", "timeStart");
    }

    public String getEvaluationEnd() {
        return getValueOfLinkedHashMap(evaluation, "end", "timeEnd");
    }

    public String getEvaluationResult() {
        return getValueOfLinkedHashMap(evaluation, "result");
    }

    public String getEvaluationScore() {
        return getValueOfLinkedHashMap(evaluation, "score");
    }

    public String getGitCommit() {
        return getValueOfLinkedHashMap(evaluation, "gitCommit");
    }

    public HashSet<SLIEvaluationResult> getSLIEvaluationResults() {
        HashSet<SLIEvaluationResult> sliEvaluationResults = null;

        if (evaluation != null) {
            Object indicatorResultsObject = evaluation.get("indicatorResults");

            if (indicatorResultsObject instanceof ArrayList) {
                ArrayList<LinkedHashMap> indicatorResultsArrayList = (ArrayList<LinkedHashMap>) indicatorResultsObject;
                sliEvaluationResults = new HashSet<>();

                for (LinkedHashMap indicatorResult : indicatorResultsArrayList ) {
                    try {
                        String name = indicatorResult.get("displayName").toString();
                        float score = numberToFloatParser(indicatorResult.get("score"));
                        KeptnCloudEventSLIResult value;
                        Object indicatorResultValueObject = indicatorResult.get("value");

                        if (indicatorResultValueObject instanceof LinkedHashMap) {
                            LinkedHashMap indicatorResultValue = (LinkedHashMap) indicatorResultValueObject;

                            value = new KeptnCloudEventSLIResult(indicatorResultValue.get("metric").toString(), numberToFloatParser(indicatorResultValue.get("value")), (boolean) indicatorResultValue.get("success"), indicatorResultValue.get("message").toString());
                        } else {
                            throw new Exception("Could not get value of indicatorResultValue!");
                        }

                        KeptnCloudEventDataResult status = KeptnCloudEventDataResult.parseResult(indicatorResult.get("status").toString());
                        HashSet<KeptnCloudEventSLITarget> passTargets = getSLITargets(indicatorResult, "passTargets");
                        HashSet<KeptnCloudEventSLITarget> warningTargets = getSLITargets(indicatorResult, "warningTargets");

                        sliEvaluationResults.add(new SLIEvaluationResult(name, score, value, passTargets, warningTargets, status));
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        System.err.println("ERROR: Could not create SLIEvaluationResult out of the payload!");
                    }
                }
            }
        } else {
            System.out.println("WARN: Cannot perform linkedHashMap.get() when it is null!");
        }

        return sliEvaluationResults;
    }

    private HashSet<KeptnCloudEventSLITarget> getSLITargets(LinkedHashMap indicatorResult, String targetType) { //TODO: maybe enum "passTargets", "warningTargets"
        final String CRITERIA = "criteria", TARGET_VALUE = "targetValue", VIOLATED = "violated";
        HashSet<KeptnCloudEventSLITarget> sliTargets = null;

        if (indicatorResult != null) {
            Object sliTargetsObject = indicatorResult.get(targetType);

            if (sliTargetsObject instanceof ArrayList) {
                ArrayList<LinkedHashMap> sliTargetsArrayList = (ArrayList<LinkedHashMap>) sliTargetsObject;

                sliTargets = new HashSet<>();
                for (LinkedHashMap<?,?> element : sliTargetsArrayList) {
                    try {
                        String criteria = element.get(CRITERIA).toString();
                        float targetValue = numberToFloatParser(element.get(TARGET_VALUE));
                        boolean violated = (boolean) element.get(VIOLATED);

                        sliTargets.add(new KeptnCloudEventSLITarget(criteria, targetValue, violated));
                    } catch (Exception e) {
                        System.err.println("ERROR: At lease one values of the sliTarget has the wrong type!");
                    }
                }
            }
        } else {
            System.out.println("WARN: Cannot perform linkedHashMap.get() when it is null");
        }

        return sliTargets;
    }

    /**
     * Parses the given number object to a float.
     * Throws an exception if the given object is not an instance of Integer, Double or Float.
     * @param number object to extract the float value of.
     * @return The float value of the given number object.
     * @throws Exception Object not a number.
     */
    private float numberToFloatParser(Object number) throws Exception {
        float result;

        if (number instanceof Integer) {
            result = ((Integer) number).floatValue();
        } else if (number instanceof Double) {
            result = ((Double) number).floatValue();
        } else if (number instanceof Float) {
            result = (float) number;
        } else {
            throw new Exception("Cannot parse non number object to float!");
        }

        return  result;
    }
/*
    TODO: add test, deployment and evaluation classes probably for all events (evaluation 2 different types)
    //Triggered:
    private Object test;
            private String start;
            private String end;

    private Object evaluation;
            private String start;
            private String end;
            private String timeframe;

    private Object deployment;
            private String[] deploymentNames;

    //Started
    //Status changed
    //Finished
    private Object evaluation;
            private String timeStart;
            private String timeEnd;
            private String result;
            private String score;
            private String sloFileContent;
            private LinkedHashMap indicatorResults; //(SLIEvaluationResult)
                    private float score;
                    private Object value; //(SLIResult)
                            private String metric;
                            private float value;
                            private boolean success;
                            private String message;
                    private String displayName;
                    private Object[] passTargets; //(SLITarget)
                            private String criteria;
                            private float targetValue;
                            private boolean violated;
                    private Object[] warningTargets; //(SLITarget)

                    private boolean keySli;
                    private String/Enum status;
            private String[] comparedEvents;
            private String gitCommit;

     */
}

