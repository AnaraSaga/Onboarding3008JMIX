package com.company.onboarding3008.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@JmixEntity
@Table(name = "DEPARTMENT", indexes = {
        @Index(name = "IDX_DEPARTMENT_HR_MNGER", columnList = "HR_MNGER_ID")
}, uniqueConstraints = {
        @UniqueConstraint(name = "IDX_DEPARTMENT_UNQ_NAME", columnNames = {"NAME"})
})
@Entity
public class Department {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @InstanceName
    @Column(name = "NAME", nullable = false)
    @NotNull
    private String name;

    @JoinColumn(name = "HR_MNGER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private User hrMnger;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    public User getHrMnger() {
        return hrMnger;
    }

    public void setHrMnger(User hrMnger) {
        this.hrMnger = hrMnger;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}