package iuh.fit.se.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String email;
    String password;
    @Column(name = "first_name")
    String firstName;
    @Column(name = "last_name")
    String lastName;
    @Enumerated(EnumType.STRING)
    Role role;
    int points;
    @Enumerated(EnumType.STRING)
    Tier tier;
    String address;
    Status status;
    @Column(name = "created_time")
    LocalDate createdTime;
    @Column(name = "modified_time")
    LocalDate modifiedTime;

    @PrePersist
    void GenerateValue(){
        this.role = Role.CUSTOMER;
        this.points = 0;
        this.tier = Tier.NONE;
        this.status = Status.OK;
        this.createdTime = LocalDate.now();
        this.modifiedTime = LocalDate.now();
    }
}
