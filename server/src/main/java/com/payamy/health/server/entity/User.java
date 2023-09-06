package com.payamy.health.server.entity;

import com.payamy.health.server.type.BloodType;
import com.payamy.health.server.type.EyeColor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;
    private String name;
    private EyeColor eyeColor;
    private BloodType bloodType;
    private Date createdAt;
}
