package com.tracker.talent_screening;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;
import org.slf4j.Logger;

@SpringBootApplication
@EnableScheduling
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@Deployment(resources = {
		"classpath:/bpmn/*.bpmn",
		"classpath:/forms/*.form"
})
public class TalentScreeningApplication implements CommandLineRunner {
	@Autowired
	private ZeebeClient zeebeClient;
	private static final Logger LOG = LoggerFactory.getLogger(TalentScreeningApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(TalentScreeningApplication.class, args);
	}

	@Override
	public void run(final String... args) {
		var bpmnProcessId = "hr_upload";
		var event = zeebeClient.newCreateInstanceCommand()
				.bpmnProcessId(bpmnProcessId)
				.latestVersion()
				.variables(Map.of("total", 100))
				.send()
				.join();
		LOG.info("started a process instance: {}", event.getProcessInstanceKey());
		System.out.println("Hello Osama!");
	}
}
