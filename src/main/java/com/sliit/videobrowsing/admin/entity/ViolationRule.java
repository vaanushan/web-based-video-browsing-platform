package com.sliit.videobrowsing.admin.entity;

import com.sliit.videobrowsing.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Owner: Vaanushan - Admin Panel module. */
@Entity
@Table(name = "violation_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViolationRule extends BaseEntity {

    @Column(nullable = false)
    private Long definedBy;   // references User.id (admin)

    @Column(nullable = false)
    private String type;      // e.g. COPYRIGHT, HATE_SPEECH, SPAM

    @Column(columnDefinition = "TEXT")
    private String description;
}
