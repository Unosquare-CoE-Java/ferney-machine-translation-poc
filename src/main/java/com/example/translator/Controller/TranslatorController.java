package com.example.translator.Controller;

import com.example.translator.Service.TranslatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/translate")
public class TranslatorController {

    private TranslatorService translatorService;

    @Autowired
    public TranslatorController(TranslatorService translatorService){
        this.translatorService = translatorService;
    }
    @GetMapping("/{source}/{target}/{parse}")
    public String translate(@PathVariable(value="source") String source,
                            @PathVariable(value="target") String target,
                            @PathVariable(value="parse") String parse){
        return translatorService.translate(source,target,parse);
    }
}
