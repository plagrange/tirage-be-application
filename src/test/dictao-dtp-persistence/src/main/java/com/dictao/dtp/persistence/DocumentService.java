package com.dictao.dtp.persistence;

import com.dictao.dtp.persistence.entity.Document;
import com.dictao.dtp.persistence.entity.DocumentSummary;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author msauvee
 */
@ApplicationScoped
// TODO : Review if request scoped was really required here !!!!
public class DocumentService extends DbService implements Serializable {

    private static final long serialVersionUID = -5560792789637838239L;

    @Transactional
    public void create(Document document) {
        document.setUpdateDate(new Date());
        getEntityManager().persist(document);
    }

    @Transactional
    public Document update(Document document) {
        EntityManager em = getEntityManager();
        document.setUpdateDate(new Date());
        if (document.getId() == null) {
            em.persist(document);
            return document;
        }
        document = em.merge(document);
        return document;
    }

    @Transactional
    public Document find(
            String applicationId, String folder, String fileName) {
        Query query = getEntityManager().createNamedQuery(Document.FIND_DOC_BY_APP_FOLDER_FILENAME);
        query.setParameter("applicationId", applicationId);
        query.setParameter("folder", folder);
        query.setParameter("filename", fileName);
        List<Document> list = query.getResultList();
        if (list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    @Transactional
    public List<DocumentSummary> findDocumentsByAppAndFodler(String applicationId, String folder) {
        Query query = getEntityManager().createNamedQuery(Document.FIND_DOCS_BY_APP_FOLDER);
        query.setParameter("applicationId", applicationId);
        query.setParameter("folder", folder);
        List<Document> qres = query.getResultList();
        List<DocumentSummary> result = new ArrayList<DocumentSummary>();
        for(Document doc : qres) {
            result.add(new DocumentSummary(doc));
        }
        return result;
    }

    @Transactional
    public Document delete(String applicationId, String folder, String fileName) {
        Query query = getEntityManager().createNamedQuery(Document.FIND_DOC_BY_APP_FOLDER_FILENAME);
        query.setParameter("applicationId", applicationId);
        query.setParameter("folder", folder);
        query.setParameter("filename", fileName);
        List<Document> docs = query.getResultList();
        Document doc = null;
        if (docs != null && docs.size() == 1) {
            doc = docs.get(0);
            getEntityManager().remove(doc);
        }
        return doc;
    }
}
