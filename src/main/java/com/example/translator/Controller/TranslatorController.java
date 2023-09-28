package com.example.translator.Controller;

import com.example.translator.DTO.TranslationDTO;
import com.example.translator.Exceptions.NoResponseException;
import com.example.translator.Service.TranslatorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/translate")
public class TranslatorController {

    private final TranslatorService translatorService;

    private final Validator validator;

    @Autowired
    public TranslatorController(TranslatorService translatorService, Validator validator) {
        this.translatorService = translatorService;
        this.validator = validator;
    }

    @PostMapping
    public TranslationDTO translate(@RequestBody @Valid TranslationDTO request) throws NoResponseException, InterruptedException, JsonProcessingException {
        return translatorService.requestTraslation(request);
    }

}
