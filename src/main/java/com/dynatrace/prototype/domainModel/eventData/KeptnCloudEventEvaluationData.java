package com.dynatrace.prototype.domainModel.eventData;

import com.dynatrace.prototype.domainModel.KeptnCloudEventDataResult;
import com.dynatrace.prototype.domainModel.KeptnCloudEventSLIResult;
import com.dynatrace.prototype.domainModel.KeptnCloudEventSLITarget;
import com.dynatrace.prototype.domainModel.SLIEvaluationResult;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class KeptnCloudEventEvaluationData extends KeptnCloudEventData {
    private static final Logger LOG = Logger.getLogger(KeptnCloudEventEvaluationData.class);

    private static final String EVALUATION_RESULT = "result";
    private static final String EVALUATION_SCORE = "score";
    private static final String EVALUATION_GIT_COMMIT = "gitCommit";
    private static final String EVALUATION_START = "start";
    private static final String EVALUATION_TIME_START = "timeStart";
    private static final String EVALUATION_END = "end";
    private static final String EVALUATION_TIME_END = "timeEnd";
    private static final String SLI_INDICATOR_RESULT = "indicatorResults";
    private static final String SLI_DISPLAY_NAME = "displayName";
    private static final String SLI_SCORE = "score";
    private static final String SLI_STATUS = "status";
    private static final String SLI_PASS_TARGETS = "passTargets";
    private static final String SLI_WARNING_TARGETS = "warningTargets";
    private static final String SLI_VALUE = "value";
    private static final String SLI_VALUE_VALUE = "value";
    private static final String SLI_VALUE_METRIC = "metric";
    private static final String SLI_VALUE_SUCCESS = "success";
    private static final String SLI_VALUE_MESSAGE = "message";
    private static final String SLI_TARGET_CRITERIA = "criteria";
    private static final String SLI_TARGET_VALUE = "targetValue";
    private static final String SLI_TARGET_VIOLATED = "violated";
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
        return getValueOfLinkedHashMap(evaluation, EVALUATION_START, EVALUATION_TIME_START);
    }

    public String getEvaluationEnd() {
        return getValueOfLinkedHashMap(evaluation, EVALUATION_END, EVALUATION_TIME_END);
    }

    public String getEvaluationResult() {
        return getValueOfLinkedHashMap(evaluation, EVALUATION_RESULT);
    }

    public String getEvaluationScore() {
        return getValueOfLinkedHashMap(evaluation, EVALUATION_SCORE);
    }

    public String getGitCommit() {
        return getValueOfLinkedHashMap(evaluation, EVALUATION_GIT_COMMIT);
    }

    public HashSet<SLIEvaluationResult> getSLIEvaluationResults() {
        HashSet<SLIEvaluationResult> sliEvaluationResults = null;

        if (evaluation != null) {
            Object indicatorResultsObject = evaluation.get(SLI_INDICATOR_RESULT);

            if (indicatorResultsObject instanceof ArrayList) {
                ArrayList<LinkedHashMap<String, ?>> indicatorResultsArrayList = (ArrayList<LinkedHashMap<String, ?>>) indicatorResultsObject;
                sliEvaluationResults = new HashSet<>();

                for (LinkedHashMap<String, ?> indicatorResult : indicatorResultsArrayList ) {
                    try {
                        String name = indicatorResult.get(SLI_DISPLAY_NAME).toString();
                        float score = numberToFloatParser(indicatorResult.get(SLI_SCORE));
                        KeptnCloudEventSLIResult value;
                        Object indicatorResultValueObject = indicatorResult.get(SLI_VALUE);

                        if (indicatorResultValueObject instanceof LinkedHashMap) {
                            LinkedHashMap<String, ?> indicatorResultValue = (LinkedHashMap<String, ?>) indicatorResultValueObject;

                            value = new KeptnCloudEventSLIResult(indicatorResultValue.get(SLI_VALUE_METRIC).toString(), numberToFloatParser(indicatorResultValue.get(SLI_VALUE_VALUE)), (boolean) indicatorResultValue.get(SLI_VALUE_SUCCESS), indicatorResultValue.get(SLI_VALUE_MESSAGE).toString());
                        } else {
                            throw new Exception("Could not get value of indicatorResultValue!");
                        }

                        KeptnCloudEventDataResult status = KeptnCloudEventDataResult.parseResult(indicatorResult.get(SLI_STATUS).toString());
                        LinkedHashSet<KeptnCloudEventSLITarget> passTargets = getSLITargets(indicatorResult, SLI_PASS_TARGETS);
                        LinkedHashSet<KeptnCloudEventSLITarget> warningTargets = getSLITargets(indicatorResult, SLI_WARNING_TARGETS);

                        sliEvaluationResults.add(new SLIEvaluationResult(name, score, value, passTargets, warningTargets, status));
                    } catch (Exception e) {
                        LOG.error("Could not create SLIEvaluationResult out of the payload!", e);
                    }
                }
            }
        } else {
            LOG.warn("Cannot perform linkedHashMap.get() when it is null!");
        }

        return sliEvaluationResults;
    }

    private LinkedHashSet<KeptnCloudEventSLITarget> getSLITargets(LinkedHashMap<String, ?> indicatorResult, String targetType) {
        LinkedHashSet<KeptnCloudEventSLITarget> sliTargets = null;

        if (indicatorResult != null) {
            Object sliTargetsObject = indicatorResult.get(targetType);

            if (sliTargetsObject instanceof ArrayList) {
                ArrayList<LinkedHashMap<String, ?>> sliTargetsArrayList = (ArrayList<LinkedHashMap<String, ?>>) sliTargetsObject;

                sliTargets = new LinkedHashSet<>();
                for (LinkedHashMap<?,?> element : sliTargetsArrayList) {
                    try {
                        String criteria = element.get(SLI_TARGET_CRITERIA).toString();
                        float targetValue = numberToFloatParser(element.get(SLI_TARGET_VALUE));
                        boolean violated = (boolean) element.get(SLI_TARGET_VIOLATED);

                        sliTargets.add(new KeptnCloudEventSLITarget(criteria, targetValue, violated));
                    } catch (Exception e) {
                        LOG.error("At lease one values of the sliTarget has the wrong type!", e);
                    }
                }
            }
        } else {
            LOG.warn("Cannot perform linkedHashMap.get() when it is null");
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
}
