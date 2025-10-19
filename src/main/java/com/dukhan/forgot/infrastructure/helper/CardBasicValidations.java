package com.dukhan.forgot.infrastructure.helper;

import com.dukhan.forgot.domain.model.entity.CardBinMaster;
import com.dukhan.forgot.domain.repository.CardBinMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CardBasicValidations {
    private static final Logger logger = LoggerFactory.getLogger(CardBasicValidations.class);

    @Autowired
    private CardBinMasterRepository cardBinMasterRepository;

    public CardBinMaster findMatchingBin(String cardNumber) {
        logger.debug("Extracting BIN from card number of length: {}", cardNumber != null ? cardNumber.length() : 0);

        if (cardNumber == null) {
            logger.warn("Card number is null, cannot extract BIN");
            return null;
        }

        int[] binLengths = {8, 7, 6};
        for (int len : binLengths) {
            if (cardNumber.length() >= len) {
                String binCandidate = cardNumber.substring(0, len);
                List<CardBinMaster> binMasterList = cardBinMasterRepository.findByBin(binCandidate);
                logger.debug("Searched for BIN: {}, found {} records", binCandidate, binMasterList.size());
                if (!binMasterList.isEmpty()) {
                    return binMasterList.get(0);
                }
            }
        }

        logger.debug("No BIN match found for card number: {}", cardNumber);
        return null;
    }
}
