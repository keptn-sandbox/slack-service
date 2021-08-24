package com.dynatrace.prototype.payloadCreator;

import com.dynatrace.prototype.domainModel.KeptnCloudEvent;
import com.dynatrace.prototype.domainModel.KeptnEvent;
import com.dynatrace.prototype.domainModel.SLIEvaluationResult;
import com.dynatrace.prototype.domainModel.eventData.KeptnCloudEventEvaluationData;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class EvaluationMapper extends KeptnCloudEventMapper {

    @Override
    public List<LayoutBlock> getSpecificData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        if (KeptnEvent.EVALUATION.getValue().equals(event.getTaskName())) {
            layoutBlockList.addAll(getEvaluationData(event));
        }

        return layoutBlockList;
    }

    private List<LayoutBlock> getEvaluationData(KeptnCloudEvent event) {
        List<LayoutBlock> layoutBlockList = new ArrayList<>();
        Object eventDataObject = event.getData();

        if (eventDataObject instanceof KeptnCloudEventEvaluationData) {
            KeptnCloudEventEvaluationData eventData = (KeptnCloudEventEvaluationData) eventDataObject;
            StringBuilder specificDataSB = new StringBuilder();

            specificDataSB.append(ifNotNull("Start: ", eventData.getEvaluationStart(), "\t"));
            specificDataSB.append(ifNotNull("End: ", eventData.getEvaluationEnd(), "\n"));
            specificDataSB.append(ifNotNull("Result: ", eventData.getEvaluationResult(), "\n"));
            specificDataSB.append(ifNotNull("Score: ", eventData.getEvaluationScore(), "\n"));

            //TODO: message need to be changed
            HashSet<SLIEvaluationResult> sliResultSet = eventData.getSLIEvaluationResults();
            if (sliResultSet != null) {
                Iterator<SLIEvaluationResult> sliResIterator = sliResultSet.iterator();

                if (sliResIterator.hasNext()) {
                    specificDataSB.append("SLI:\n");
                    specificDataSB.append("Name").append(" | Value").append(" | pass Criteria").append(" | warning Criteria")
                            .append(" | Result").append(" | Score\n");
                    while (sliResIterator.hasNext()) {
                        SLIEvaluationResult element = sliResIterator.next();
                        specificDataSB.append(element.getDisplayName()).append(" | ").append(element.getValue().getValue())
                                .append(" | ").append(element.getPassTargets().toString()).append(" | ")
                                .append(element.getWarningTargets().toString()).append(" | ").append(element.getStatus())
                                .append(" | ").append(element.getScore()).append("\n");
                    }
                }
            }

            if (specificDataSB.length() > 0) {
                layoutBlockList.add(SlackCreator.createLayoutBlock(SectionBlock.TYPE, specificDataSB.toString()));
                layoutBlockList.add(SlackCreator.createDividerBlock());
            }
        } else {
            System.out.println("WARN: eventData is not an instance of KeptnCloudEventEvaluationData although the event type is \"Evaluation\"!");
        }

        return layoutBlockList;
    }
}
