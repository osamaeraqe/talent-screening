package com.tracker.talent_screening.service;


import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ApplicantService {
    private final ZeebeClient zeebeClient;
    private static final Logger LOG = LoggerFactory.getLogger(ApplicantService.class);

    public ApplicantService(ZeebeClient zeebeClient) {
                this.zeebeClient = zeebeClient;
            }

    public String startHrProcess() {
        var bpmnProcessId = "hr_upload";
        try {
                        var event = zeebeClient.newCreateInstanceCommand()
                                        .bpmnProcessId(bpmnProcessId)
                                        .latestVersion()
                                        .variables(Map.of("total", 100))
                                        .send()
                                        .join();
                        LOG.info("Started process instance: {}", event.getProcessInstanceKey());
                        return String.valueOf(event.getProcessInstanceKey());
                    } catch (Exception e) {
                        LOG.error("Failed to start process {}", bpmnProcessId, e);
                        throw e;
                    }
    }
}
