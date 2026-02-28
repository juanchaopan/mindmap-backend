package com.p8499.mindmap.workspace

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class WorkspaceService(private val repo: WorkspaceRepository) {

    @Transactional(readOnly = true)
    fun listByOwner(owner: String): List<Workspace> =
        repo.findAllByOwner(owner)

    @Transactional(readOnly = true)
    fun getByIdAndOwner(id: UUID, owner: String): Workspace =
        repo.findByIdAndOwner(id, owner)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found")

    @Transactional
    fun create(name: String, owner: String): Workspace =
        repo.save(Workspace(name = name, owner = owner))

    @Transactional
    fun update(id: UUID, name: String, owner: String): Workspace {
        val workspace = getByIdAndOwner(id, owner)
        workspace.name = name
        return repo.save(workspace)
    }

    @Transactional
    fun delete(id: UUID, owner: String) {
        val workspace = getByIdAndOwner(id, owner)
        repo.delete(workspace)
    }
}
