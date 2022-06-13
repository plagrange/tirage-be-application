package com.dictao.dtp.persistence.data;

import java.util.ArrayList;
import java.util.List;

public class Signatures {

    private EntitySignature entitySignature;
    private List<PersonalSignature> personalSignatures;

    public Signatures() {
        personalSignatures = new ArrayList<PersonalSignature>();
    }

    public Signatures(EntitySignature entitySignature,
            List<PersonalSignature> personalSignatures) {
        this.entitySignature = entitySignature;
        this.personalSignatures = new ArrayList<PersonalSignature>(personalSignatures);
    }

    /**
     * @return the entitySignature
     */
    public EntitySignature getEntitySignature() {
        return entitySignature;
    }

    /**
     * @param entitySignature the entitySignature to set
     */
    public void setEntitySignature(EntitySignature entitySignature) {
        this.entitySignature = entitySignature;
    }

    /**
     * @return the personalSignatures
     */
    public List<PersonalSignature> getPersonalSignatures() {
        return personalSignatures;
    }
}
