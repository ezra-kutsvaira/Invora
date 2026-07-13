package mapper;

import dto.receipt.ReceiptResponse;
import entity.Receipt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReceiptMapper {

    @Mapping(target = "paymentId", source = "payment.id")
    @Mapping(target = "invoiceId", source = "payment.invoice.id")
    @Mapping(target = "invoiceNumber", source = "payment.invoice.invoiceNumber")
    @Mapping(target = "customerName", source = "payment.invoice.customer.customerName")
    ReceiptResponse toResponse(Receipt receipt);
}

