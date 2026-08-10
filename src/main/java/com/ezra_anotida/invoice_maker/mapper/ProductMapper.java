package com.ezra_anotida.invoice_maker.mapper;

import com.ezra_anotida.invoice_maker.dto.product.CreateProductRequest;
import com.ezra_anotida.invoice_maker.dto.product.ProductResponse;
import com.ezra_anotida.invoice_maker.dto.product.ProductSummaryResponse;
import com.ezra_anotida.invoice_maker.dto.product.UpdateProductRequest;
import com.ezra_anotida.invoice_maker.entity.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;\nimport org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "organization", ignore = true)\n    Product toEntity (CreateProductRequest request);

    ProductResponse toResponse(Product product);

    ProductSummaryResponse toSummaryResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> products);

    List<ProductSummaryResponse> toSummaryResponseList(List<Product> products);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateToEntityFromRequest(UpdateProductRequest request, @MappingTarget Product product);


}
