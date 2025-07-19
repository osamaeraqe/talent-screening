package com.tracker.talent_screening;

import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableScheduling
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
//@Deployment(resources = "classpath*:/bpmn/*.bpmn")
public class TalentScreeningApplication {

	public static void main(String[] args) {
		SpringApplication.run(TalentScreeningApplication.class, args);
	}

}
