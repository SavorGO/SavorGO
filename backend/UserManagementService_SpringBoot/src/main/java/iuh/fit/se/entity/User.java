package iuh.fit.se.entity;

import iuh.fit.se.enums.Role;
import iuh.fit.se.enums.Status;
import iuh.fit.se.enums.Tier;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
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
