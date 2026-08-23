package com.ezra_anotida.invoice_maker;

import com.ezra_anotida.invoice_maker.config.TestContainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestContainersConfiguration.class)
class InvoiceMakerApplicationTests {

	@Test
	void contextLoads() {
	}

}
