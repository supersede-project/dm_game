/*
(C) Copyright 2015-2018 The SUPERSEDE Project Consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package eu.supersede.dm.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.dm.DMGame;
import eu.supersede.dm.ProcessManager;
import eu.supersede.fe.exception.NotFoundException;
import eu.supersede.gr.jpa.AlertsJpa;
import eu.supersede.gr.jpa.ReceivedUserRequestsJpa;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.jpa.RequirementsPropertiesJpa;
import eu.supersede.gr.model.HAlert;
import eu.supersede.gr.model.HReceivedUserRequest;
import eu.supersede.gr.model.HRequirementProperty;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.RequirementProperties;

@RestController
@RequestMapping("processes/alerts")
public class ProcessAlertsRest
{
    @Autowired
    private AlertsJpa alertsJpa;

    @Autowired
    private ReceivedUserRequestsJpa receivedUserRequestsJpa;

    @Autowired
    private RequirementsJpa requirementsJpa;

    @Autowired
    private RequirementsPropertiesJpa requirementsPropertiesJpa;

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public void importAlerts(@RequestParam Long processId, @RequestParam(required = false) List<String> alertsId)
    {
        ProcessManager proc = DMGame.get().getProcessManager(processId);
        List<HAlert> alerts = proc.getAlerts();

        for (HAlert alert : alerts)
        {
            proc.removeAlert(alert.getId());
        }

        if (alertsId == null)
        {
            // No alerts have been added to the process.
            return;
        }

        for (String alertId : alertsId)
        {
            HAlert alert = DMGame.get().getJpa().alerts.findOne(alertId);

            if (alert == null)
            {
                throw new NotFoundException(
                        "Can't add alert with id " + alertId + " to the process because it does not exist");
            }

            proc.addAlert(alert);
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<HAlert> getAlertsList(@RequestParam Long processId)
    {
        return DMGame.get().getProcessManager(processId).getAlerts();
    }

    @RequestMapping(value = "/convert", method = RequestMethod.PUT)
    public void convertAlertToRequirement(@RequestParam String alertId, Long processId)
    {
        ProcessManager proc = DMGame.get().getProcessManager(processId);
        HAlert alert = alertsJpa.findOne(alertId);

        if (alert == null)
        {
            throw new NotFoundException(
                    "Can't convert alert with id " + alertId + " to a requirement because it does not exist");
        }

        List<HReceivedUserRequest> requests = receivedUserRequestsJpa.findRequestsForAlert(alertId);

        if (requests == null || requests.size() == 0)
        {
            System.out.println("No user requests for alert " + alertId + ", no requirement added");
            return;
        }

        for (HReceivedUserRequest request : requests)
        {
            Requirement requirement = new Requirement();
            requirement.setName(request.getDescription());
            requirement.setDescription("");
            Requirement savedRequirement = requirementsJpa.save(requirement);
            proc.addRequirement(savedRequirement);

            requirementsPropertiesJpa.save(new HRequirementProperty(savedRequirement.getRequirementId(),
                    RequirementProperties.CLASSIFICATION, request.getClassification()));
            requirementsPropertiesJpa.save(new HRequirementProperty(savedRequirement.getRequirementId(),
                    RequirementProperties.ACCURACY, "" + request.getAccuracy()));
            requirementsPropertiesJpa.save(new HRequirementProperty(savedRequirement.getRequirementId(),
                    RequirementProperties.POSITIVE_SENTIMENT, "" + request.getPositiveSentiment()));
            requirementsPropertiesJpa.save(new HRequirementProperty(savedRequirement.getRequirementId(),
                    RequirementProperties.NEGATIVE_SENTIMENT, "" + request.getNegativeSentiment()));
            requirementsPropertiesJpa.save(new HRequirementProperty(savedRequirement.getRequirementId(),
                    RequirementProperties.OVERALL_SENTIMENT, "" + request.getOverallSentiment()));
            requirementsPropertiesJpa.save(new HRequirementProperty(savedRequirement.getRequirementId(),
                    "Original feedback", "" + request.getDescription()));
        }

        // Discard alert
        alertsJpa.delete(alert);
    }

    @RequestMapping(value = "/userrequests/import", method = RequestMethod.POST)
    public void importUserRequests(@RequestParam Long processId,
            @RequestParam(required = false) List<String> userRequests)
    {
        ProcessManager proc = DMGame.get().getProcessManager(processId);

        if (userRequests == null)
        {
            // nothing to do
            return;
        }

        for (String userRequestId : userRequests)
        {
            HReceivedUserRequest userRequest = DMGame.get().getJpa().receivedUserRequests.findOne(userRequestId);

            if (userRequest == null)
            {
                throw new NotFoundException("Can't add user request with id " + userRequestId
                        + " to the process because it does not exist");
            }

            Requirement requirement = new Requirement();
            requirement.setName(userRequest.getDescription());
            requirement.setDescription("Features:");
            Requirement savedRequirement = requirementsJpa.save(requirement);
            proc.addRequirement(savedRequirement);

            requirementsPropertiesJpa.save(new HRequirementProperty(savedRequirement.getRequirementId(),
                    RequirementProperties.CLASSIFICATION, userRequest.getClassification()));
            requirementsPropertiesJpa.save(new HRequirementProperty(savedRequirement.getRequirementId(),
                    RequirementProperties.ACCURACY, "" + userRequest.getAccuracy()));
            requirementsPropertiesJpa.save(new HRequirementProperty(savedRequirement.getRequirementId(),
                    RequirementProperties.POSITIVE_SENTIMENT, "" + userRequest.getPositiveSentiment()));
            requirementsPropertiesJpa.save(new HRequirementProperty(savedRequirement.getRequirementId(),
                    RequirementProperties.NEGATIVE_SENTIMENT, "" + userRequest.getNegativeSentiment()));
            requirementsPropertiesJpa.save(new HRequirementProperty(savedRequirement.getRequirementId(),
                    RequirementProperties.OVERALL_SENTIMENT, "" + userRequest.getOverallSentiment()));

            receivedUserRequestsJpa.delete(userRequest);
        }
    }
}