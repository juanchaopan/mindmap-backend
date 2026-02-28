package com.p8499.mindmap.workspace

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.web.server.ResponseStatusException
import java.util.UUID
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class WorkspaceServiceTest {

    @Mock
    lateinit var repo: WorkspaceRepository

    @InjectMocks
    lateinit var service: WorkspaceService

    private val owner = "user-123"

    // region listByOwner

    @Test
    fun `listByOwner returns all workspaces for the owner`() {
        val workspaces = listOf(Workspace("A", owner), Workspace("B", owner))
        whenever(repo.findAllByOwner(owner)).thenReturn(workspaces)

        assertEquals(workspaces, service.listByOwner(owner))
    }

    @Test
    fun `listByOwner returns empty list when owner has no workspaces`() {
        whenever(repo.findAllByOwner(owner)).thenReturn(emptyList())

        assertEquals(emptyList(), service.listByOwner(owner))
    }

    // endregion

    // region getByIdAndOwner

    @Test
    fun `getByIdAndOwner returns workspace when found`() {
        val workspace = Workspace("My WS", owner)
        whenever(repo.findByIdAndOwner(workspace.id, owner)).thenReturn(workspace)

        assertEquals(workspace, service.getByIdAndOwner(workspace.id, owner))
    }

    @Test
    fun `getByIdAndOwner throws 404 when workspace does not exist`() {
        whenever(repo.findByIdAndOwner(any(), any())).thenReturn(null)

        val ex = assertThrows<ResponseStatusException> {
            service.getByIdAndOwner(UUID.randomUUID(), owner)
        }
        assertEquals(404, ex.statusCode.value())
    }

    @Test
    fun `getByIdAndOwner throws 404 when workspace belongs to another user`() {
        val workspace = Workspace("Other WS", "other-user")
        whenever(repo.findByIdAndOwner(workspace.id, owner)).thenReturn(null)

        val ex = assertThrows<ResponseStatusException> {
            service.getByIdAndOwner(workspace.id, owner)
        }
        assertEquals(404, ex.statusCode.value())
    }

    // endregion

    // region create

    @Test
    fun `create saves workspace with correct name and owner`() {
        val workspace = Workspace("New WS", owner)
        whenever(repo.save(any<Workspace>())).thenReturn(workspace)

        val result = service.create("New WS", owner)

        assertEquals("New WS", result.name)
        assertEquals(owner, result.owner)
        verify(repo).save(any())
    }

    // endregion

    // region update

    @Test
    fun `update changes workspace name and saves`() {
        val workspace = Workspace("Old Name", owner)
        whenever(repo.findByIdAndOwner(workspace.id, owner)).thenReturn(workspace)
        whenever(repo.save(workspace)).thenReturn(workspace)

        val result = service.update(workspace.id, "New Name", owner)

        assertEquals("New Name", result.name)
        verify(repo).save(workspace)
    }

    @Test
    fun `update throws 404 when workspace not found`() {
        whenever(repo.findByIdAndOwner(any(), any())).thenReturn(null)

        val ex = assertThrows<ResponseStatusException> {
            service.update(UUID.randomUUID(), "New Name", owner)
        }
        assertEquals(404, ex.statusCode.value())
    }

    // endregion

    // region delete

    @Test
    fun `delete removes workspace`() {
        val workspace = Workspace("WS", owner)
        whenever(repo.findByIdAndOwner(workspace.id, owner)).thenReturn(workspace)

        service.delete(workspace.id, owner)

        verify(repo).delete(workspace)
    }

    @Test
    fun `delete throws 404 when workspace not found`() {
        whenever(repo.findByIdAndOwner(any(), any())).thenReturn(null)

        val ex = assertThrows<ResponseStatusException> {
            service.delete(UUID.randomUUID(), owner)
        }
        assertEquals(404, ex.statusCode.value())
    }

    // endregion
}
