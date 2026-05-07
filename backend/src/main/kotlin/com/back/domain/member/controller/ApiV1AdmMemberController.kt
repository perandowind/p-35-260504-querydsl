package com.back.domain.member.controller

import com.back.domain.member.dto.MemberWithUsernameDto
import com.back.domain.member.service.MemberService
import com.back.standard.dto.PageDto
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
        @RequestParam("pageSize", defaultValue = "2") pageSize: Int,
        @RequestParam("kw", defaultValue = "") kw: String,
        @RequestParam("kwType", defaultValue = "ALL") kwType: String,
    ): PageDto<MemberWithUsernameDto> {

        val page = if(page >= 0) page else 0
        val pageSize = if(pageSize >= 5) pageSize else 5

        val pagedResult = memberService.findByPaged(page, pageSize, kw, kwType)

        return PageDto<MemberWithUsernameDto>(
            pagedResult.map{
                MemberWithUsernameDto(it)
            }
        )
    }
}
