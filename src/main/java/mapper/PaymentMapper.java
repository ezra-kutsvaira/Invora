package mapper;

import dto.payment.*;
import entity.Invoice;
import entity.Payment;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "invoice", source = "invoiceId")
    Payment toEntity(CreatePaymentRequest request);

    @Mapping(target = "invoiceId", source = "invoice.id")
    @Mapping(target = "invoiceNumber", source = "invoice.invoiceNumber")
    PaymentResponse toResponse(Payment payment);

    @Mapping(target = "invoiceNumber", source = "invoice.invoiceNumber")
    @Mapping(target = "customerName", source = "invoice.customer.customerName")
    PaymentSummaryResponse toSummaryResponse(Payment payment);

    List<PaymentResponse> toResponseList(List<Payment> payments);

    List<PaymentSummaryResponse> toSummaryResponseList(List<Payment> payments);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdatePaymentRequest request, @MappingTarget Payment payment);

    default Invoice mapInvoice(Long invoiceId) {
        if (invoiceId == null) {
            return null;
        }

        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        return invoice;
    }
}