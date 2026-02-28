package com.p8499.mindmap.workspace

import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import java.util.UUID

data class WorkspaceRequest(val name: String)

data class WorkspaceResponse(
    val id: UUID,
    val name: String,
    val owner: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
) {
    constructor(w: Workspace) : this(w.id, w.name, w.owner, w.createdAt, w.updatedAt)
}

@RestController
@RequestMapping("/api/workspaces")
class WorkspaceController(private val service: WorkspaceService) {

    @GetMapping
    fun list(@AuthenticationPrincipal jwt: Jwt): List<WorkspaceResponse> =
        service.listByOwner(jwt.subject).map(::WorkspaceResponse)

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID, @AuthenticationPrincipal jwt: Jwt): WorkspaceResponse =
        WorkspaceResponse(service.getByIdAndOwner(id, jwt.subject))

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody req: WorkspaceRequest, @AuthenticationPrincipal jwt: Jwt): WorkspaceResponse =
        WorkspaceResponse(service.create(req.name, jwt.subject))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody req: WorkspaceRequest,
        @AuthenticationPrincipal jwt: Jwt,
    ): WorkspaceResponse =
        WorkspaceResponse(service.update(id, req.name, jwt.subject))

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID, @AuthenticationPrincipal jwt: Jwt) =
        service.delete(id, jwt.subject)
}
