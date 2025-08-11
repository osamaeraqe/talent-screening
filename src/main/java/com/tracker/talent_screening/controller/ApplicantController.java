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

    /**
     * Handles POST requests to start the HR process for an applicant.
     *
     * Delegates to ApplicantService.startHrProcess() and returns its result.
     *
     * The returned String is used as the MVC view name (or response body depending on controller configuration).
     *
     * @return the view name or response produced by ApplicantService.startHrProcess()
     */
    @PostMapping("/start")
    public String startHrProcess(){
        return applicantService.startHrProcess();
    }
}
