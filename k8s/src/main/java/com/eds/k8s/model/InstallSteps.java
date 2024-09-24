package com.eds.k8s.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "install_steps")
public class InstallSteps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "step_name", nullable = false, unique = true, length = 50)
    private String stepName;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }
}

