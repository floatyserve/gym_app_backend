package com.example.demo.unit.service.customer;

import com.example.demo.auth.domain.User;
import com.example.demo.customer.domain.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.customer.service.impl.CustomerServiceJpa;
import com.example.demo.exceptions.ReferenceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceJpaTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceJpa customerService;

    private User user;
    private Customer customer;

    private final Instant NOW = Instant.parse("2025-01-01T10:00:00Z");

    @BeforeEach
    void setUp() {
        user = new User("admin@test.com", "hash", null, NOW);
        customer = new Customer(
                "John Doe",
                "123456789",
                "john@test.com",
                user
        );
    }

    // ---------- create ----------

    @Test
    void create_savesAndReturnsCustomer() {
        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Customer result = customerService.create(
                "John Doe",
                "123456789",
                "john@test.com",
                user
        );

        assertThat(result.getFullName()).isEqualTo("John Doe");
        assertThat(result.getPhoneNumber()).isEqualTo("123456789");
        assertThat(result.getEmail()).isEqualTo("john@test.com");
        assertThat(result.getCreatedBy()).isEqualTo(user);

        verify(customerRepository).save(any(Customer.class));
    }


    // ---------- findById ----------

    @Test
    void findById_returnsCustomer_whenExists() {
        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        Customer result = customerService.findById(1L);

        assertThat(result).isSameAs(customer);
    }

    @Test
    void findById_throwsException_whenNotFound() {
        when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById(1L))
                .isInstanceOf(ReferenceNotFoundException.class)
                .hasMessage("Customer not found with id: 1");
    }

    // ---------- findByEmail ----------

    @Test
    void findByEmail_returnsCustomer_whenExists() {
        when(customerRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.of(customer));

        Customer result = customerService.findByEmail("john@test.com");

        assertThat(result).isSameAs(customer);
    }

    @Test
    void findByEmail_throwsException_whenNotFound() {
        when(customerRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findByEmail("john@test.com"))
                .isInstanceOf(ReferenceNotFoundException.class)
                .hasMessage("Customer not found with email: john@test.com");
    }

    // ---------- update ----------

    @Test
    void update_updatesProvidedFieldsAndReturnsCustomer() {
        User updatedBy = new User("staff@test.com", "hash", null, NOW);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        Customer result = customerService.update(
                1L,
                "Jane Doe",
                null,
                "jane@test.com",
                updatedBy
        );

        assertThat(result.getFullName()).isEqualTo("Jane Doe");
        assertThat(result.getPhoneNumber()).isEqualTo("123456789");
        assertThat(result.getEmail()).isEqualTo("jane@test.com");
        assertThat(result.getUpdatedBy()).isEqualTo(updatedBy);
    }

    @Test
    void update_throwsException_whenCustomerNotFound() {
        when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                customerService.update(
                        1L,
                        "Jane Doe",
                        null,
                        null,
                        user
                )
        ).isInstanceOf(ReferenceNotFoundException.class)
                .hasMessage("Customer not found with id: 1");
    }
}
