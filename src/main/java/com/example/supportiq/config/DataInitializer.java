package com.example.supportiq.config;

import com.example.supportiq.entity.Organization;
import com.example.supportiq.entity.User;
import com.example.supportiq.entity.enums.UserRole;
import com.example.supportiq.repository.OrganizationRepository;
import com.example.supportiq.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Create Organization if not exists
            if (organizationRepository.count() == 0) {

                Organization org = new Organization();
                org.setName("TechCorp Solutions");
                organizationRepository.save(org);

                System.out.println("✅ Organization created: TechCorp Solutions");

                // Create Admin User
                User admin = new User();
                admin.setName("Admin User");
                admin.setEmail("admin@techcorp.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(UserRole.ADMIN);
                admin.setOrganization(org);
                userRepository.save(admin);

                System.out.println("✅ Admin created: admin@techcorp.com / admin123");

                // Create Agent User
                User agent = new User();
                agent.setName("Bob Agent");
                agent.setEmail("agent@techcorp.com");
                agent.setPassword(passwordEncoder.encode("agent123"));
                agent.setRole(UserRole.AGENT);
                agent.setOrganization(org);
                userRepository.save(agent);

                System.out.println("✅ Agent created: agent@techcorp.com / agent123");

                // Create Customer User
                User customer = new User();
                customer.setName("Alice Customer");
                customer.setEmail("customer@techcorp.com");
                customer.setPassword(passwordEncoder.encode("customer123"));
                customer.setRole(UserRole.CUSTOMER);
                customer.setOrganization(org);
                userRepository.save(customer);

                System.out.println("✅ Customer created: customer@techcorp.com / customer123");
            }
        };
    }
}