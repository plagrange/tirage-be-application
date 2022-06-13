package com.dictao.dtp.persistence;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;

import com.dictao.dtp.persistence.entity.Archive;

@RequestScoped
public class ArchiveRepository extends DbService implements Serializable {

    private static final long serialVersionUID = 5487792771951122300L;
    
    @Transactional
    public void create(Archive archive) {
        getEntityManager().persist(archive);
    }

}
