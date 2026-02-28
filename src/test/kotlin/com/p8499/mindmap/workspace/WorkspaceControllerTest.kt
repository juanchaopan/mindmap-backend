package com.p8499.mindmap.workspace

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class WorkspaceControllerTest {

    @Mock
    lateinit var service: WorkspaceService

    private lateinit var mvc: MockMvc
    private val owner = "user-123"
    private val otherId = UUID.randomUUID()

    @BeforeEach
    fun setup() {
        val jwt = Jwt.withTokenValue("token")
            .header("alg", "none")
            .subject(owner)
            .build()
        SecurityContextHolder.getContext().authentication = JwtAuthenticationToken(jwt, emptyList())
        mvc = MockMvcBuilders.standaloneSetup(WorkspaceController(service))
            .setCustomArgumentResolvers(AuthenticationPrincipalArgumentResolver())
            .build()
    }

    @AfterEach
    fun teardown() {
        SecurityContextHolder.clearContext()
    }

    private fun workspace(name: String = "Test WS") = Workspace(name, owner)

    // region GET /api/workspaces

    @Test
    fun `GET workspaces returns 200 with list`() {
        whenever(service.listByOwner(owner)).thenReturn(listOf(workspace("A"), workspace("B")))

        mvc.perform(get("/api/workspaces"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("A"))
            .andExpect(jsonPath("$[1].name").value("B"))
    }

    // endregion

    // region GET /api/workspaces/{id}

    @Test
    fun `GET workspace by id returns 200`() {
        val ws = workspace()
        whenever(service.getByIdAndOwner(ws.id, owner)).thenReturn(ws)

        mvc.perform(get("/api/workspaces/${ws.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Test WS"))
            .andExpect(jsonPath("$.owner").value(owner))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.updatedAt").exists())
    }

    @Test
    fun `GET workspace by id returns 404 when not found`() {
        whenever(service.getByIdAndOwner(any(), any()))
            .thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND))

        mvc.perform(get("/api/workspaces/$otherId"))
            .andExpect(status().isNotFound)
    }

    // endregion

    // region POST /api/workspaces

    @Test
    fun `POST workspace returns 201 with created workspace`() {
        val ws = workspace("New WS")
        whenever(service.create("New WS", owner)).thenReturn(ws)

        mvc.perform(
            post("/api/workspaces")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "New WS"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("New WS"))
            .andExpect(jsonPath("$.owner").value(owner))
    }

    // endregion

    // region PUT /api/workspaces/{id}

    @Test
    fun `PUT workspace returns 200 with updated workspace`() {
        val ws = workspace("Updated WS")
        whenever(service.update(ws.id, "Updated WS", owner)).thenReturn(ws)

        mvc.perform(
            put("/api/workspaces/${ws.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "Updated WS"}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Updated WS"))
    }

    @Test
    fun `PUT workspace returns 404 when not found`() {
        whenever(service.update(any(), any(), any()))
            .thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND))

        mvc.perform(
            put("/api/workspaces/$otherId")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "Updated WS"}""")
        )
            .andExpect(status().isNotFound)
    }

    // endregion

    // region DELETE /api/workspaces/{id}

    @Test
    fun `DELETE workspace returns 204`() {
        mvc.perform(delete("/api/workspaces/$otherId"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `DELETE workspace returns 404 when not found`() {
        whenever(service.delete(any(), any()))
            .thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND))

        mvc.perform(delete("/api/workspaces/$otherId"))
            .andExpect(status().isNotFound)
    }

    // endregion
}
