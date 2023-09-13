package com.example.translator.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TranslationDTO {
    @NotNull
    private String sourceLanguage;
    @NotNull
    private String targetLanguage;
    @NotNull
    private String textToTranslate;
    private String translatedText;
    private LocalDateTime timestamp;
}
