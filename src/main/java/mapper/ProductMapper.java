package mapper;

import dto.product.CreateProductRequest;
import dto.product.ProductResponse;
import dto.product.ProductSummaryResponse;
import dto.product.UpdateProductRequest;
import entity.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity (CreateProductRequest request);

    ProductResponse toResponse(Product product);

    ProductSummaryResponse toSummaryResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> products);

    List<ProductSummaryResponse> toSummaryResponseList(List<Product> products);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateToEntityFromRequest(UpdateProductRequest request, @MappingTarget Product product);


}
