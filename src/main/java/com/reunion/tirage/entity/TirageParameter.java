/**
 * 
 */
package com.reunion.tirage.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * @author pmekeze
 *
 */
@Entity
@Table(name = "TBL_PARAMETER", uniqueConstraints = @UniqueConstraint(columnNames = { "COMPAGNIE" }))
@NamedQueries({
        @NamedQuery(name = TirageParameter.DELETE_ALL, query = "delete from TirageParameter p"),
        @NamedQuery(name = TirageParameter.FIND_PARAM_BY_COMPAGNIE, query = "Select p from TirageParameter p where p.compagnie = :compagnie"),
        @NamedQuery(name = TirageParameter.DELETE_PARAM_BY_COMPAGNIE, query = "delete from TirageParameter p where p.compagnie = :compagnie") })
public class TirageParameter {

    public static final String DELETE_ALL = "deleteAllParams";
    public static final String DELETE_PARAM_BY_COMPAGNIE = "deleteParamByCompagnie";
    public static final String FIND_PARAM_BY_COMPAGNIE = "getParamByCompagnie";

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Basic
    @Column(name = "NB_PARTICIPANT", nullable = true)
    private int nbParticipant;
    @Basic
    @Column(name = "LIST_TIRE", nullable = true)
    private List<String> listTire;
    @Basic
    @Column(name = "LIST_RESTANTE")
    private List<String> listRestante;
    @Basic
    @Column(name = "COMPAGNIE")
    private String compagnie;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATE_DATE")
    private Date updateTimestamp;
    
    @Basic
    @Column(name = "ADMINS")
    private List<User> adminList;

    public TirageParameter() {

    }

    public TirageParameter(int nbParticipant, List<String> listTire, List<String> listRestante, String compagnie, List<User> adminList) {
        this.nbParticipant = nbParticipant;
        this.listTire = listTire;
        this.listRestante = listRestante;
        this.compagnie = compagnie;
        this.adminList = adminList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNbParticipant() {
        return nbParticipant;
    }

    public void setNbParticipant(int nbParticipant) {
        this.nbParticipant = nbParticipant;
    }

    public List<String> getListTire() {
        return listTire;
    }

    public void setListTire(List<String> listTire) {
        this.listTire = listTire;
    }

    public List<String> getListRestante() {
        return listRestante;
    }

    public void setListRestante(List<String> listRestante) {
        this.listRestante = listRestante;
    }

    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Date updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public String getCompagnie() {
        return compagnie;
    }

    public void setCompagnie(String compagnie) {
        this.compagnie = compagnie;
    }

    public List<User> getAdminList() {
        return adminList;
    }

    public void setAdminList(List<User> adminList) {
        this.adminList = adminList;
    }

    @Override
    public String toString() {
        return null;
    }
}
