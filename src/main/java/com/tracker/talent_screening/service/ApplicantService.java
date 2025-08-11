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
