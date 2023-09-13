package com.example.translator.Controller;

import com.example.translator.DTO.TranslationDTO;
import com.example.translator.Service.TranslatorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/translate")
public class TranslatorController {

    private TranslatorService translatorService;

    @Autowired
    public TranslatorController(TranslatorService translatorService){
        this.translatorService = translatorService;
    }
    @PostMapping
    public TranslationDTO translate(@RequestBody @Valid TranslationDTO request){
        return translatorService.translate(request);
    }
}
