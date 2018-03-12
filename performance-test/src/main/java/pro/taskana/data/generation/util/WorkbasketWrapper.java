package pro.taskana.data.generation.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pro.taskana.data.generation.builder.WorkbasketBuilder;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.WorkbasketType;

/**
 * Class wraps the {@link WorkbasketImpl} to generate the id according to the
 * following pattern: Domain_+ WorkbasketType_+ CompoundOrgLayer_+ GroupId
 *
 * @author fe
 *
 */
public class WorkbasketWrapper extends WorkbasketImpl {

    public static final int NUMBER_LENGTH_IN_ID = 2;
    
    private static final String WORKBASKET_ID_PREFIX = "WB";
    private static final String ID_PART_LINKING_CHAR = "#";
    private static final int INITIAL_MEMBER_ID = 0;

    private Integer memberId;
    private int layer;
    private String formattedOrgLevel;
    
    private WorkbasketWrapper parent;
    private List<WorkbasketWrapper> directOrIndirectChildren;
    private List<WorkbasketWrapper> directChildren;
    private UserWrapper userWrapper;
    
    private List<TaskImpl> tasks;

    public WorkbasketWrapper(WorkbasketType type, String domain) {
        this.setType(type);
        this.setDomain(domain);
        this.layer = 0;
        directOrIndirectChildren = new ArrayList<>();
        directChildren = new ArrayList<>();
        parent = null;
        formattedOrgLevel = null;
        memberId = null;
    }

    /**
     * Sets the layer of this {@link Workbasket}.
     *
     * @param layer index
     */
    public void setLayer(int layer) {
        this.layer = layer;
    }

    /**
     * Set the id and return an instance of {@link Workbasket}.
     *
     * @return this as {@link Workbasket}
     */
    public WorkbasketImpl getAsWorkbasket() {
        if (getId() == null || getId().isEmpty()) {
            calculateOrgLvl();
            if (!directChildren.isEmpty()) {            
                directChildren.stream().map(c -> c.getAsWorkbasket());
            }
            setOwner(userWrapper.getId());
            generateAndSetId();
            setName(getId());
            setDescription(getId());
        }

        return this;
    }

    /**
     * Supplies a list of {@link Workbasket} which represents the direct
     * children of this {@link Workbasket} in the tree. Direct children are also
     * the direct distribution targets of this {@link Workbasket}.
     *
     * @return list of {@link Workbasket}
     */
    public List<WorkbasketWrapper> getDirectChildren() {
        return directChildren;
    }

    /**
     * Provides information about the layer of this {@link Workbasket}
     *
     * @return layer index of this {@link Workbasket}
     */
    public int getLayer() {
        return layer;
    }

    /**
     * Provides all direct or indirect children. Direct children are the
     * distribution targets. Indirect children are the direct children of the
     * distribution targets.
     *
     * @return direct or indirect connected {@link Workbasket} of a lower level
     */
    public List<WorkbasketWrapper> getDirectOrIndirectChildren() {
        return directOrIndirectChildren;
    }
    
    /**
     * Sets the parent of this {@link Workbasket}.
     *
     * @param parent {@link Workbasket}
     */
    public void setParent(WorkbasketWrapper parent) {
        this.parent = parent;
    }

    /**
     * Sets the distribution targets of this {@link Workbasket}. The
     * distribution target will be set as direct children of this
     * {@link Workbasket}.
     *
     * @param distributionTargets distribution targets
     */
    public void addDistributionTargets(List<WorkbasketWrapper> distributionTargets) {
        this.directChildren.addAll(distributionTargets);
        distributionTargets.forEach(dt -> dt.setParent(this));

        this.directOrIndirectChildren.addAll(distributionTargets);
        distributionTargets.forEach(child -> this.directOrIndirectChildren.addAll(child.getDirectOrIndirectChildren()));
    }

    /**
     * Set owner as {@link UserWrapper}.
     *
     * @param user {@link UserWrapper} providing the user id
     */
    public void setUserAsOwner(UserWrapper user) {
        this.userWrapper = user;
        this.userWrapper.newOwnerOfWorkbasket(layer, getType());
    }

    /**
     * Returns the owner as {@link UserWrapper}.
     *
     * @return owner as {@link UserWrapper}
     */
    public UserWrapper getOwnerAsUser() {
        return userWrapper;
    }

    /**
     * Returns the organisation level of this {@link Workbasket}.
     *
     * @return organisation level.
     */
    public String getOrgLvl() {
        if ((formattedOrgLevel == null || formattedOrgLevel.isEmpty())) {
            calculateOrgLvl();
        }
        return formattedOrgLevel;
    }
    

    /**
	 * Returns the member id of this {@link Workbasket}. Member id is the index of
	 * this {@link Workbasket} within the list of the parents direct children.
	 * 
	 * @return id
	 */
    public int getMemberId() {
        if (parent == null) {
            if (memberId == null) {
                memberId = INITIAL_MEMBER_ID;
            }
        } else if (memberId == null) {
            memberId = parent.getDirectChildren().indexOf(this);
        }
        return memberId;
    }

    private void calculateOrgLvl() {
        if (getOrgLevel1() == null || getOrgLevel1().isEmpty()) {
            if (parent == null) {
            	int orgLevelValue = WorkbasketBuilder.WORKBASKETS_IN_ORG_LVL_1++;
                formattedOrgLevel = Formatter.format(orgLevelValue, NUMBER_LENGTH_IN_ID);
                setOrgLevel1(formattedOrgLevel);
            } else {
                String parentOrgLvl = parent.getOrgLvl();
                String formattedParentMemberId = Formatter.format(parent.getMemberId(), NUMBER_LENGTH_IN_ID);
                formattedOrgLevel = parentOrgLvl + formattedParentMemberId;
                copyOrgLvlFromParent();
                writeCurrentOrgLevelValue(formattedParentMemberId);
            }
            userWrapper.setOrgLvl(formattedOrgLevel, getType(), getMemberId());
        }
    }
    
    private void copyOrgLvlFromParent() {
        setOrgLevel1(parent.getOrgLevel1());
        setOrgLevel2(parent.getOrgLevel2());
        setOrgLevel3(parent.getOrgLevel3());
        setOrgLevel4(parent.getOrgLevel4());
    }
    
    private void writeCurrentOrgLevelValue(String orgLvlValue) {
        if (getOrgLevel2() == null || getOrgLevel2().isEmpty()) {
            setOrgLevel2(orgLvlValue);
        } else if (getOrgLevel3() == null || getOrgLevel3().isEmpty()) {
            setOrgLevel3(orgLvlValue);
        } else if (getOrgLevel4() == null || getOrgLevel4().isEmpty()) {
            setOrgLevel4(orgLvlValue);
        }
    }

    private void generateAndSetId() {
        if (getId() == null || getId().isEmpty()) {
            StringBuilder sb = new StringBuilder(getDomain());
            sb.append(WORKBASKET_ID_PREFIX);
            sb.append(formattedOrgLevel);
            sb.append(ID_PART_LINKING_CHAR);
            sb.append(Formatter.format(getMemberId(), NUMBER_LENGTH_IN_ID));
            setKey(sb.toString());
            setId(sb.toString());
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((directChildren == null) ? 0 : directChildren.hashCode());
        result = prime * result + ((directOrIndirectChildren == null) ? 0 : directOrIndirectChildren.hashCode());
        result = prime * result + ((formattedOrgLevel == null) ? 0 : formattedOrgLevel.hashCode());
        result = prime * result + layer;
        result = prime * result + ((memberId == null) ? 0 : memberId.hashCode());
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
        result = prime * result + ((userWrapper == null) ? 0 : userWrapper.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        WorkbasketWrapper other = (WorkbasketWrapper) obj;
        if (formattedOrgLevel == null) {
            if (other.formattedOrgLevel != null)
                return false;
        } else if (!formattedOrgLevel.equals(other.formattedOrgLevel))
            return false;
        if (layer != other.layer)
            return false;
        if (memberId == null) {
            if (other.memberId != null)
                return false;
        } else if (!memberId.equals(other.memberId))
            return false;
        if (parent == null) {
            if (other.parent != null)
                return false;
        } else if (!parent.equals(other.parent))
            return false;
        if (userWrapper == null) {
            if (other.userWrapper != null)
                return false;
        } else if (!userWrapper.equals(other.userWrapper))
            return false;
        return true;
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder("[");
    	sb.append("id=");
    	sb.append(getId());
    	sb.append(", ");
    	sb.append(userWrapper.toString());
    	sb.append("]");
    	return sb.toString();
    }

}
