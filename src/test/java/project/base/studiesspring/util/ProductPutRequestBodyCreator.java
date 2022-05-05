package project.base.studiesspring.util;

import project.base.studiesspring.requests.ProductPostRequestBody;
import project.base.studiesspring.requests.ProductPutRequestBody;

public class ProductPutRequestBodyCreator {
    public static ProductPutRequestBody createProductPutRequestBody(){

        return ProductPutRequestBody.builder()
                .id(ProductCreator.createProductValid().getId())
                .name(ProductCreator.createProductValid().getName())
                .build();
    }
}
