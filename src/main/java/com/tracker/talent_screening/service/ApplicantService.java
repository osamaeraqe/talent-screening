package com.tracker.talent_screening.service;


import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ApplicantService {
    private ZeebeClient zeebeClient;
    private static final Logger LOG = LoggerFactory.getLogger(ApplicantService.class);

    /**
     * Starts an HR process instance in Zeebe for the "hr_upload" BPMN and returns its BPMN process id.
     *
     * This method creates a new process instance using the latest deployed version, sets a single
     * process variable "total" to 100, and waits synchronously for the instance to be created.
     *
     * @return the BPMN process id of the created process instance
     */
    public String startHrProcess() {
        var bpmnProcessId = "hr_upload";
        var event = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(bpmnProcessId)
                .latestVersion()
                .variables(Map.of("total", 100))
                .send()
                .join();
        LOG.info("started a process instance: {}", event.getProcessInstanceKey());
        return event.getBpmnProcessId();
    }
}
