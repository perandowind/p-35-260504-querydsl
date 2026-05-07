package com.back.domain.member.repository

import com.back.domain.member.entity.Member
import com.back.domain.member.entity.QMember
import com.back.standard.enums.MemberSearchKeywordType
import com.back.standard.enums.MemberSearchSortType
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
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

    override fun findQByUsernameContaining(
        username: String,
        pageable: Pageable
    ): Page<Member> {
        val member = QMember.member

        val content = jpaQueryFactory
            .selectFrom(member)
            .where(member.username.contains(username))
            .orderBy(*getOrderSpecifier(pageable.sort))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

//        pageable.sort.forEach { order ->
//            when (order.property) {
//                "id" -> query.orderBy(if (order.isAscending) member.id.asc() else member.id.desc())
//                "username" -> query.orderBy(if (order.isAscending) member.username.asc() else member.username.desc())
//                "nickname" -> query.orderBy(if (order.isAscending) member.nickname.asc() else member.nickname.desc())
//            }
//        }

        // content 쿼리
//        val content = query
//            .offset(pageable.offset)
//            .limit(pageable.pageSize.toLong())
//            .fetch()

        return PageableExecutionUtils.getPage(
            content,
            pageable
        ) {
            jpaQueryFactory
                .select(member.count())
                .from(member)
                .where(
                    member.username.contains(username)
                )
                .fetchOne() ?: 0L
        }

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

    override fun findByKwPaged(kw: String, kwType: MemberSearchKeywordType, pageable: Pageable): Page<Member> {

        val member = QMember.member

        val builder = BooleanBuilder().apply {
            when(kwType) {
                MemberSearchKeywordType.USERNAME -> this.and(member.username.contains(kw))
                MemberSearchKeywordType.NICKNAME -> this.and(member.nickname.contains(kw))
                MemberSearchKeywordType.ALL -> {
                    this.and(
                        member.username.contains(kw).or(
                            member.nickname.contains(kw))
                    )
                }
            }
        }

        val query = jpaQueryFactory
            .selectFrom(member)
            .where(builder)

        pageable.sort.forEach { order ->
            val path = when (order.property.lowercase()) {
                MemberSearchSortType.ID.property -> member.id
                MemberSearchSortType.USERNAME.property -> member.username
                MemberSearchSortType.NICKNAME.property -> member.nickname
                else -> null
            }

            path?.let { property ->
                OrderSpecifier(
                    if (order.isAscending) Order.ASC else Order.DESC,
                    property
                ).also {
                    query.orderBy(it)
                }
            }
        }

        val content = query
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageableExecutionUtils.getPage(content, pageable) {
            jpaQueryFactory
                .select(member.count())
                .from(member)
                .where(builder)
                .fetchOne() ?: 0L
        }
    }






    /**커스텀 정렬 메서드 getOrderSpecifier 정의*/
    private fun getOrderSpecifier(sort: Sort): Array<OrderSpecifier<*>> {
        // 1. QClass의 실제 alias를 가져옴 (타입 안정성 확보)
        val pathBuilder = PathBuilder(Member::class.java, QMember.member.metadata.name)

        // 2. map을 이용해 Sort -> OrderSpecifier로 바로 변환
        return sort.map { order ->
            val direction = if (order.isAscending) Order.ASC else Order.DESC

            // 3. 가독성을 위해 불필요한 중간 변수 제거 및 캐스팅 처리
            OrderSpecifier(
                direction,
                pathBuilder.get(order.property) as Expression<out Comparable<*>>
            )
        }.toList().toTypedArray()
    }


}