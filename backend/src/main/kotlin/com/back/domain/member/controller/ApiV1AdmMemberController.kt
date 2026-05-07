package com.back.domain.member.controller

import com.back.domain.member.dto.MemberWithUsernameDto
import com.back.domain.member.service.MemberService
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/adm/members")
class ApiV1AdmMemberController(
    private val memberService: MemberService
) {

    @GetMapping
    fun list(
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("pageSize", defaultValue = "2") pageSize: Int
    ): Page<MemberWithUsernameDto> {

        val page = if(page >= 0) page else 0
        val pageSize = if(pageSize >= 4) 4 else pageSize

        val pagedResult = memberService.findByPaged(page, pageSize)

        return pagedResult
            .map { MemberWithUsernameDto(it) }
    }
}
