package project.base.studiesspring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import project.base.studiesspring.domain.Product;
import project.base.studiesspring.requests.ProductPostRequestBody;
import project.base.studiesspring.requests.ProductPutRequestBody;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {
    public static  final ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);
    public abstract Product toProduct(ProductPostRequestBody productPostRequestBody);
    public abstract Product toProduct(ProductPutRequestBody productPutRequestBody);
}
