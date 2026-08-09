package com.ezra_anotida.invoice_maker.mapper;

import com.ezra_anotida.invoice_maker.dto.invoiceitem.CreateInvoiceItemRequest;
import com.ezra_anotida.invoice_maker.dto.invoiceitem.InvoiceItemResponse;
import com.ezra_anotida.invoice_maker.dto.invoiceitem.UpdateInvoiceItemRequest;
import com.ezra_anotida.invoice_maker.entity.InvoiceItem;
import com.ezra_anotida.invoice_maker.entity.Product;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceItemMapper {

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    @Mapping(target = "lineTotal", ignore = true)
    InvoiceItem toEntity(CreateInvoiceItemRequest request);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.productName")
    InvoiceItemResponse toResponse(InvoiceItem item);

    List<InvoiceItemResponse> toResponseList(List<InvoiceItem> invoiceItems);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    @Mapping(target = "lineTotal", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateInvoiceItemRequest request, @MappingTarget InvoiceItem item);

    default Product mapProduct(Long productId) {
        if (productId == null){
            return null;
    }

    Product product = new Product();
    product.setId(productId);
    return product;
}

}
