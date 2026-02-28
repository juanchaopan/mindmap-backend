package com.p8499.mindmap.workspace

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface WorkspaceRepository : JpaRepository<Workspace, UUID> {
    fun findAllByOwner(owner: String): List<Workspace>
    fun findByIdAndOwner(id: UUID, owner: String): Workspace?
}
