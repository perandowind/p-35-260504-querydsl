package com.back.domain.member.repository

import com.back.domain.member.entity.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MemberRepositoryCustom {
    fun findQById(id: Int): Member?
    fun findQByUsername(username: String): Member?
    fun findQByIdIn(ids: List<Int>): List<Member>
    fun findQByUsernameAndNickname(username: String, nickname: String): Member?
    fun findQByUsernameOrNickname(username: String, nickname: String): List<Member> // Or이므로 여러개 반환가능
    fun findQByUsernameAndEitherPasswordOrNickname(username: String, password: String, nickname: String): List<Member>
    fun findQByNicknameContaining(nickname: String): List<Member>
    fun findQByNicknameContaining(nickname: String, pageable: Pageable): Page<Member>
    fun countQByNicknameContaining(nickname: String): Long
    fun existsQByNicknameContaining(nickname: String): Boolean
    fun findQByNicknameContainingOrderByIdDesc(nickname: String): List<Member>
    fun findQByUsernameContaining(username: String, pageable: Pageable): Page<Member>
    fun findByKwPaged(kw: String, pageable: Pageable): Page<Member>
}