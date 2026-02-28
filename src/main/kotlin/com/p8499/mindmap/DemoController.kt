package com.p8499.mindmap

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DemoController {

    @GetMapping("/public/hello")
    fun hello(): Map<String, String> = mapOf("message" to "Hello, world!")

    @GetMapping("/api/me")
    fun me(@AuthenticationPrincipal jwt: Jwt): Map<String, String?> = mapOf(
        "sub" to jwt.subject,
        "email" to jwt.getClaimAsString("email"),
    )
}
