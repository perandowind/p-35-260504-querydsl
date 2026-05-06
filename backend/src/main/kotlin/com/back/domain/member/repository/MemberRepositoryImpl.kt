package com.back.domain.member.repository

import com.back.domain.member.entity.Member
import com.back.domain.member.entity.QMember
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils

class MemberRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : MemberRepositoryCustom {

    override fun findQById(id: Int): Member? {

        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(member.id.eq(id)) // where member.id = id
            .fetchOne() // limit 1
    }

    override fun findQByUsername(username: String): Member? {

        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(member.username.eq(username)) // where member.username = name
            .fetchOne() // limit 1
    }

    override fun findQByIdIn(ids: List<Int>): List<Member> {

        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(member.id.`in`(ids)) // where member.username = name
            .fetch()
    }

    override fun findQByUsernameAndNickname(
        username: String,
        nickname: String
    ): Member? {
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(
                member.username.eq(username)
                    .and(member.nickname.eq(nickname))
            ) // where member.username = name and member.nickname = nickname
            .fetchOne() // limit 1
    }

    override fun findQByUsernameOrNickname(
        username: String,
        nickname: String
    ): List<Member> {
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(
                member.username.eq(username)
                    .or(member.nickname.eq(nickname))
            ) // where member.username = name or member.nickname = nickname
            .fetch() // 여러개 가져올때 = fetch() 사용
    }

    override fun findQByUsernameAndEitherPasswordOrNickname(
        username: String,
        password: String,
        nickname: String
    ): List<Member> {
        val member = QMember.member

        // select * from member where username = ? and (password = ? or nickname = ?)
        return jpaQueryFactory
            .selectFrom(member)
            .where(
                member.username.eq(username)
                    .and(
                        member.password.eq(password)
                            .or(member.nickname.eq(nickname))
                    )
            )
            .fetch()
    }

    // where nickname LIKE '%nickname%'
    override fun findQByNicknameContaining(nickname: String): List<Member> {
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(member.nickname.contains(nickname))
            .fetch()
    }

    override fun countQByNicknameContaining(nickname: String): Long {
        val member = QMember.member

        return jpaQueryFactory
            .select(member.count())
            .from(member)
            .where(
                member.nickname.contains(nickname)
            )
            .fetchOne() ?: 0L
    }

    override fun existsQByNicknameContaining(nickname: String): Boolean {
        val member = QMember.member

        return jpaQueryFactory
            .selectOne()
            .from(member)
            .where(
                member.nickname.contains(nickname)
            )
            .fetchFirst() != null

    }

    override fun findQByNicknameContainingOrderByIdDesc(nickname: String): List<Member> {
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(
                member.nickname.contains(nickname)
            )
            .orderBy(member.id.desc())
            .fetch()
    }

    override fun findQByNicknameContaining(
        nickname: String,
        pageable: Pageable
    ): Page<Member> {
        val member = QMember.member

        // content 쿼리
        val result = jpaQueryFactory
            .selectFrom(member)
            .where(
                member.nickname.contains(nickname)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

//        val totalCount = jpaQueryFactory
//            .select(member.count())
//            .from(member)
//            .where(
//                member.nickname.contains(nickname)
//            )
//            .fetchOne() ?: 0L

        return PageableExecutionUtils.getPage(
            result,
            pageable
        ) {
            jpaQueryFactory
                .select(member.count())
                .from(member)
                .where(
                    member.nickname.contains(nickname)
                )
                .fetchOne() ?: 0L
        } // 람다함수가 제일 나중 파라미터에 위치한 경우 밖으로 빼도됨

//        return PageImpl(result, pageable, totalCount)
    }


}