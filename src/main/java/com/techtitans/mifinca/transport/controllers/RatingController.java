package com.techtitans.mifinca.transport.controllers;

import com.techtitans.mifinca.domain.dtos.ReviewDTO;
import com.techtitans.mifinca.domain.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping("/tenants")
    public void rateTenant(@RequestBody ReviewDTO body) {
        //reviewService.saveReview(body);
    }
}