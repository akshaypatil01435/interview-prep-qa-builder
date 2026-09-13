package com.interviewprep.backend.config;

import com.interviewprep.backend.entity.Difficulty;
import com.interviewprep.backend.entity.Question;
import com.interviewprep.backend.entity.Topic;
import com.interviewprep.backend.repository.QuestionRepository;
import com.interviewprep.backend.repository.TopicRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class QuestionDataInitializer {
    @Bean
    CommandLineRunner seedQuestionBank(TopicRepository topics, QuestionRepository questions) {
        return args -> {
            Map<String, String[]> dataset = new LinkedHashMap<>();
            dataset.put("Java", new String[]{"What is the difference between JDK, JRE, and JVM?|JDK contains development tools, JRE supplies runtime libraries, and JVM executes bytecode.", "Why is String immutable in Java?|Immutability enables safe sharing, string-pool reuse, stable hashing, and improved security."});
            dataset.put("OOP", new String[]{"What are the four pillars of object-oriented programming?|Encapsulation, abstraction, inheritance, and polymorphism.", "When should composition be preferred over inheritance?|Prefer composition when behavior should be assembled flexibly without creating a fragile is-a hierarchy."});
            dataset.put("Collections", new String[]{"How do ArrayList and LinkedList differ?|ArrayList provides fast indexed access; LinkedList is efficient for insertions/removals through known nodes.", "What is the difference between HashMap and ConcurrentHashMap?|ConcurrentHashMap supports safe concurrent access with finer-grained coordination and disallows null keys and values."});
            dataset.put("Multithreading", new String[]{"What is a race condition?|It occurs when multiple threads access shared mutable state and the result depends on timing.", "What is the difference between synchronized and volatile?|synchronized provides mutual exclusion and visibility; volatile provides visibility only."});
            dataset.put("Exception Handling", new String[]{"What is the difference between checked and unchecked exceptions?|Checked exceptions must be declared or handled; unchecked exceptions extend RuntimeException.", "Why should exceptions not be silently swallowed?|Silent failures hide defects and make recovery, observability, and debugging difficult."});
            dataset.put("JDBC", new String[]{"Why use PreparedStatement instead of Statement?|PreparedStatement parameterizes SQL, reduces injection risk, and can improve repeated query performance.", "What is JDBC connection pooling?|It reuses a managed set of database connections instead of opening a new connection for each request."});
            dataset.put("Spring Boot", new String[]{"What does dependency injection solve in Spring?|It separates object construction from use, improving configurability, testability, and loose coupling.", "What is the purpose of @Transactional?|It defines a transaction boundary so related database work commits or rolls back together."});
            dataset.put("Spring Security", new String[]{"What is the difference between authentication and authorization?|Authentication establishes identity; authorization decides what that identity may access.", "Why should passwords be hashed with BCrypt?|BCrypt is intentionally slow and salted, which reduces the impact of leaked password databases."});
            dataset.put("REST API", new String[]{"What makes an HTTP method idempotent?|Repeating an idempotent request has the same intended server state as making it once.", "When should an API return 201 Created?|After successfully creating a resource, usually with its representation or Location header."});
            dataset.put("SQL", new String[]{"What is the difference between INNER JOIN and LEFT JOIN?|INNER JOIN returns matching rows only; LEFT JOIN keeps every left row and fills unmatched right values with null.", "What is an index tradeoff?|Indexes speed reads but consume storage and add overhead to inserts, updates, and deletes."});
            dataset.put("DBMS", new String[]{"What are ACID properties?|Atomicity, consistency, isolation, and durability describe reliable transaction behavior.", "What is normalization?|It organizes data to reduce duplication and update anomalies while preserving dependencies."});
            dataset.put("Operating Systems", new String[]{"What is the difference between a process and a thread?|A process owns an isolated address space; threads share a process address space.", "What causes deadlock?|Mutual exclusion, hold-and-wait, no preemption, and circular wait together allow deadlock."});
            dataset.put("Computer Networks", new String[]{"What is the TCP three-way handshake?|SYN, SYN-ACK, and ACK establish a reliable TCP connection.", "How does DNS work?|Resolvers query DNS records to map a domain name to an IP address, often using cached results."});
            dataset.put("Git", new String[]{"What is the difference between merge and rebase?|Merge preserves branch history with a merge commit; rebase reapplies commits onto a new base.", "Why should teams avoid force-pushing shared branches?|It rewrites shared history and can discard collaborators' commits."});
            dataset.put("Docker", new String[]{"What is the difference between an image and a container?|An image is an immutable template; a container is a running instance of that template.", "Why use multi-stage Docker builds?|They keep build tools out of the final image, reducing image size and attack surface."});
            dataset.put("Linux", new String[]{"What do file permissions 755 mean?|Owner can read/write/execute; group and others can read and execute.", "What is the purpose of systemd?|It initializes and supervises services, dependencies, and system startup on many Linux distributions."});
            dataset.put("AWS", new String[]{"What is the difference between an IAM role and IAM user?|A user is a long-lived identity; a role provides temporary permissions assumed by principals or services.", "Why place an application in private subnets?|It limits direct internet exposure while allowing controlled access through load balancers or NAT gateways."});
            dataset.forEach((name, entries) -> {
                Topic topic = topics.findAll().stream().filter(item -> item.getName().equals(name)).findFirst().orElseGet(() -> { Topic item = new Topic(); item.setName(name); item.setDescription(name + " interview preparation"); return topics.save(item); });
                for (int index = 0; index < entries.length; index++) {
                    String[] parts = entries[index].split("\\|", 2);
                    if (questions.count((root, query, cb) -> cb.equal(root.get("questionText"), parts[0])) == 0) { Question question = new Question(); question.setTopic(topic); question.setQuestionText(parts[0]); question.setAnswerText(parts[1]); question.setDifficulty(index == 0 ? Difficulty.EASY : Difficulty.MEDIUM); questions.save(question); }
                }
            });
        };
    }
}
