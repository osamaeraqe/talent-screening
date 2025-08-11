package com.tracker.talent_screening.controller;

import com.tracker.talent_screening.TalentScreeningApplication;
import com.tracker.talent_screening.service.ApplicantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping(value = "/applicant")
public class ApplicantController {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicantController.class);
    @Autowired private ApplicantService applicantService;

    @PostMapping("/start")
    public String startHrProcess(){
        return applicantService.startHrProcess();
    }
}
