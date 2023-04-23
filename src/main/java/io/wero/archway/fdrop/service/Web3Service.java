package io.wero.archway.fdrop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class Web3Service {
    private static final Logger logger = LoggerFactory.getLogger(Web3Service.class);

    /**
     * Mints and transfer the NFT to the specified wallet
     * @param wallet the wallet which receives the NFT
     */
    public void mint(String wallet) {
        logger.info("Minting NFT to wallet {}", wallet);
    }
}
