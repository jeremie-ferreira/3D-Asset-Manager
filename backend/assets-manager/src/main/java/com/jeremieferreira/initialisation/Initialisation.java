package com.jeremieferreira.initialisation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.annotation.PostConstruct;

@Component
public class Initialisation {

    private static final Logger logger = LoggerFactory.getLogger(Initialisation.class);

    @Autowired
    private InitialisationService initialisationService;

    @Autowired
    @Qualifier("transactionManager")
    protected PlatformTransactionManager txManager;

    @PostConstruct
    public void init() {
        TransactionTemplate tmpl = new TransactionTemplate(txManager);
        tmpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {	
                Long start = System.currentTimeMillis();
                initialisationService.init();
                logger.info("Init complete duration :  {}", System.currentTimeMillis() - start);
            }
        });

    }

}
