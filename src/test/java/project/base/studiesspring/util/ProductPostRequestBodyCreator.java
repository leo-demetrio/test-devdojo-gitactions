package project.base.studiesspring.util;

import project.base.studiesspring.domain.Product;
import project.base.studiesspring.requests.ProductPostRequestBody;

public class ProductPostRequestBodyCreator {
    public static ProductPostRequestBody createProductPostRequestBody(){

        return ProductPostRequestBody.builder()
                .name(ProductCreator.createProductForBeSaved().getName())
                .build();
    }

}
