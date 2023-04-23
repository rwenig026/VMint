package io.wero.archway.fdrop.controller;

import io.wero.archway.fdrop.model.Collection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CollectionController {

    /**
     * Get collection details about ongoing nft mints
     * @return
     */
    @GetMapping("/collection")
    public Collection getCollection() {
        return new Collection(
                "archway1rnhrmtgpgftlx6z69c5qmtyz2te44huvnwa7xe",
                "Archway Premium Pass",
                """
                        As an Archway premium pass holder, you have the opportunity to become part of the premium community and enjoy exclusive benefits.\s
                        By joining the premium Archway community, you will gain access to a variety of features and services that are not available to regular users.""",
                "http://localhost:8080/img/premium_pass.png",
                1200L);
    }
}
