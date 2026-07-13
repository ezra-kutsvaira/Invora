package mapper;

import dto.invoice.CreateInvoiceRequest;
import entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring" , uses = {InvoiceItemMapper.class, PaymentMapper.class })
public interface InvoiceMapper {

    @Mapping(target = "customer", source = "customerId")
    Invoice toEntity(CreateInvoiceRequest request);
}
