package com.notionAuto.notionAuto.Repository;

import com.notionAuto.notionAuto.Entity.NotionPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotionPageRepository extends JpaRepository<NotionPage,Long> {
    NotionPage findTop1ByOrderByIdDesc();
}
