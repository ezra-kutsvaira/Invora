package mapper;

import dto.invoice.*;
import entity.Customer;
import entity.Invoice;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring" , uses = {InvoiceItemMapper.class, PaymentMapper.class })
public interface InvoiceMapper {

    @Mapping(target = "customer", source = "customerId")
    Invoice toEntity(CreateInvoiceRequest request);

    @Mapping(target = "customerId" , source = "customer.id")
    @Mapping(target = "customerName" , source = "customer.customerName")
    InvoiceResponse toResponse (Invoice invoice);

    @Mapping(target = "customerName", source = "customer.customerName")
    InvoiceSummaryResponse toSummaryResponse(Invoice invoice);

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.customerName")
    @Mapping(target = "customerEmail", source = "customer.email")
    @Mapping(target = "customerPhone", source = "customer.phone")
    @Mapping(target = "customerAddress", source = "customer.address")
    @Mapping(target = "customerCity", source = "customer.city")
    @Mapping(target = "customerCountry", source = "customer.country")
    @Mapping(target = "customerTaxNumber", source = "customer.taxNumber")
    InvoiceDetailsResponse toDetailsResponse(Invoice invoice);

    List<InvoiceResponse> toResponseList(List<Invoice> invoices);

    List<InvoiceSummaryResponse> toSummaryResponseList(List<Invoice> invoices);

    @Mapping(target = "customer" , source = "customerId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityRequest(UpdateInvoiceRequest request, @MappingTarget Invoice invoice);

    default Customer mapCustomer (Long customerId){
        if(customerId == null){
            return null;
        }

        Customer customer = new Customer();
        customer.setId(customerId);
        return customer;
    }

}
