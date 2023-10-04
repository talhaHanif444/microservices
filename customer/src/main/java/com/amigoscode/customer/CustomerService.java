package com.amigoscode.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public record CustomerService(CustomerRepository customerRepository, RestTemplate restTemplate)
{
	public void registerCustomer(CustomerRegistrationRequest request)
	{
		Customer customer = Customer.builder()
				.firstName(request.firstName())
				.lastName(request.lastName())
				.email(request.email())
				.build();
		customerRepository.saveAndFlush(customer);
		log.info("going to check that is customer is fraudster {}", customer.getId());
		FraudCheckResponse fraudCheckResponse = restTemplate.getForObject(
				"http://localhost:8081/api/v1/fraud-check/{customerId}",
				FraudCheckResponse.class,
				customer.getId());
		if(fraudCheckResponse.isFraudster())
			throw new IllegalStateException("fraudster");
	}
}