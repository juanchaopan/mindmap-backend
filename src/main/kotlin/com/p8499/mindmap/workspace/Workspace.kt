package com.p8499.mindmap.workspace

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "workspaces")
class Workspace(
    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, updatable = false)
    val owner: String,

    @Id
    val id: UUID = UUID.randomUUID(),
) {
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now()
        private set

    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
        private set
}
