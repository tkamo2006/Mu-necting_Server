package com.munecting.server.domain.archive.repository;

import com.munecting.server.domain.archive.dto.get.ArchiveRes;
import com.munecting.server.domain.archive.dto.get.MyArchivePageRes;
import com.munecting.server.domain.archive.dto.get.MyArchivesRes;
import com.munecting.server.domain.archive.entity.QArchive;
import com.munecting.server.domain.member.entity.Member;
import com.munecting.server.domain.pick.dto.get.PicksPageRes;
import com.munecting.server.domain.pick.dto.get.PicksRes;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.munecting.server.domain.archive.entity.QArchive.archive;
import static com.munecting.server.domain.pick.entity.QPick.pick;

@RequiredArgsConstructor
public class ArchiveRepositoryImpl implements ArchiveRepositoryCustom{
    private final EntityManager em;

    //주변 아카이브
    @Override
    public List<ArchiveRes> findNearArchive(double x,double y,int range){
        return em.createQuery("SELECT new com.munecting.server.domain.archive.dto.get.ArchiveRes(a.musicId.name,a.musicId.coverImg,a.musicId.genre,a.musicId.musicPre,a.musicId.musicPull,a.replyCnt,a.id," +
                        "a.musicId.artist) " +
                        " FROM Archive a " +
                        "where ST_Distance_Sphere(Point(:y,:x),Point(a.pointY,a.pointX)) <= :range " +
                        "and a.endTime > now()",ArchiveRes.class)
                .setParameter("x",x)
                .setParameter("y",y)
                .setParameter("range",range)
                .getResultList();
    }
    /**----------------------------------------------------------------*/

    //내가 업로드한 아카이브 조회
    @Override
    public MyArchivePageRes findArchiveByMember(Member member, Pageable pageable){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<MyArchivesRes> myArchivesRes = queryFactory
                .select(Projections.constructor(MyArchivesRes.class,
                        archive.musicId.coverImg,
                        archive.id))
                .from(archive)
                .where(archive.memberId.eq(member))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(archive.createAt.desc())
                .fetch();
        long totalCnt = queryFactory
                .select(archive.id.count())
                .from(archive)
                .where(archive.memberId.eq(member))
                .fetchOne();
        totalCnt = totalCnt/2+(totalCnt%2==0?0:1);
        return new MyArchivePageRes(myArchivesRes,totalCnt-1);
    }
}
