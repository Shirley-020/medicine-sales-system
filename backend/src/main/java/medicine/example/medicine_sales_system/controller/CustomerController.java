package medicine.example.medicine_sales_system.controller;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import medicine.example.medicine_sales_system.common.ApiResponse;
import medicine.example.medicine_sales_system.entity.Customer;
import medicine.example.medicine_sales_system.repository.CustomerRepository;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/list")
    public ApiResponse<List<Map<String, Object>>> list() {
        List<Map<String, Object>> out = new java.util.ArrayList<>();
        for (Customer c : customerRepository.findAll()) {
            Map<String, Object> customerMap = new HashMap<>();
            customerMap.put("id", c.getId());
            customerMap.put("name", c.getName() != null ? c.getName() : "");
            customerMap.put("phone", c.getPhone() != null ? c.getPhone() : "");
            customerMap.put("address", c.getAddress() != null ? c.getAddress() : "");
            customerMap.put("creditCode", c.getCreditCode() != null ? c.getCreditCode() : "");
            out.add(customerMap);
        }
        return ApiResponse.success(out);
    }
}
