package pro.taskana.data.generation.util;

import pro.taskana.model.WorkbasketType;

/**
 * Class wraps the informations to generate a user-id with the pattern:
 * Domain + Role + Orglvl + TeamId
 *
 *
 * @author fe
 *
 */
public class UserWrapper {

    public static final String ID_PART_LINKING_CHAR = "#";
    public static final int NUMBER_LENGTH_IN_ID = 2;
    
    private String orglvl;
    private final String domain;
    private int highestWorkbasketLevel;
    private UserType type;
    private int teamMemberIndex;
    private String generatedId;

    public UserWrapper(String domain) {
        this.domain = domain;
        this.type = UserType.USER;
        teamMemberIndex = 1;
        this.generatedId = null;
        this.orglvl = null;
    }

    /**
     * Called by {@link WorkbasketWrapper} when the owner is set. The workbasket
     * informs his user about its {@link WorkbasketType} and level.
     *
     * @param level of the workbasket owned by the this user
     * @param ownedWbType {@link WorkbasketType} of the workbasket owned by this
     * user
     */
    public void newOwnerOfWorkbasket(int level, WorkbasketType ownedWbType) {
        if (highestWorkbasketLevel < level) {
            highestWorkbasketLevel = level;
        }

        if (ownedWbType == WorkbasketType.GROUP) {
            type = UserType.TEAMMANAGER;
        }
    }

    /**
     * Sets the index of this user in the team.
     *
     * @param index
     */
    public void setTeamMemberIndex(int index) {
        this.teamMemberIndex = index;
    }

    /**
     * Return index of this {@link UserWrapper} within the highest team.
     *
     * @return team index
     */
    public int getTeamIndex() {
        return teamMemberIndex;
    }

    /**
     * Updates the organisation level of this user.
     *
     * @param orgLvl organisation level
     * @param ownedWbType {@link WorkbasketType}
     */
    public void setOrgLvl(String orgLvl, WorkbasketType ownedWbType, int teamIndex) {
        if (this.orglvl != null) {
            int lvl = this.orglvl.length();
            int newLvl = orglvl.length();
            if (newLvl < lvl) {
                this.orglvl = orgLvl;
                this.teamMemberIndex = teamIndex;
            } 
        } else {
            this.orglvl = orgLvl;
            this.teamMemberIndex = teamIndex;
        }

        if (ownedWbType == WorkbasketType.GROUP) {
            type = UserType.TEAMMANAGER;
        }
    }

    /**
     * Generated the id of this user with pattern: DomainRoleOrglvlUsertTeamId.
     *
     * @return id
     */
    public String getId() {
        if (generatedId == null || generatedId.isEmpty()) {
            StringBuilder sb = new StringBuilder(domain);
            sb.append(type.getAsString());
            if (orglvl == null || orglvl.isEmpty()) {
                throw new IllegalStateException("Orglevel should be set!");
            }
            sb.append(orglvl);
            sb.append(ID_PART_LINKING_CHAR);
            sb.append(Formatter.format(teamMemberIndex, NUMBER_LENGTH_IN_ID));
            generatedId = sb.toString();
        }
        return generatedId;
    }
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder("user=");
    	sb.append(generatedId);
    	return sb.toString();
    }

}
