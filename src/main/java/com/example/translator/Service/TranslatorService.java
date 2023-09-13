package com.example.translator.Service;

import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
import com.example.translator.DTO.TranslationDTO;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Service
public class TranslatorService {

    private AmazonTranslate amazonTranslate;

    @Autowired
    public TranslatorService(AmazonTranslate amazonTranslate){
        this.amazonTranslate = amazonTranslate;
    }
    public TranslationDTO translate(TranslationDTO tr){
        TranslateTextRequest request = new TranslateTextRequest()
                .withText(tr.getTextToTranslate())
                .withSourceLanguageCode(tr.getSourceLanguage())
                .withTargetLanguageCode(tr.getTargetLanguage());

        TranslateTextResult result  = amazonTranslate.translateText(request);
        tr.setTranslatedText(result.getTranslatedText());
        tr.setTimestamp(LocalDateTime.now());
        return tr;
    }
}
