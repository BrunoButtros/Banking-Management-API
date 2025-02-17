package dev.bruno.banking.service;

import dev.bruno.banking.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ExcelTemplateService {

    @Value("classpath:templates/Transaction_Template.xlsx")
    private Resource transactionTemplate;

    public byte[] getTransactionTemplate() {
        try (InputStream inputStream = transactionTemplate.getInputStream()) {
            return StreamUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            throw new BusinessException("Failed to load Excel template", e);
        }
    }
}
