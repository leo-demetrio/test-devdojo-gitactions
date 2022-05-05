package project.base.studiesspring.util;

import project.base.studiesspring.domain.Product;

public class ProductCreator {
    public static Product createProductForBeSaved(){
        return Product.builder().name("Leo test 01").build();
    }

    public static Product createProductValid(){
        return Product.builder().name("Leo test 01").id(1L).build();
    }
    public static Product createProductValidForUpdate(){
        return Product.builder().name("Leo test 01 update").id(1L).build();
    }

}
