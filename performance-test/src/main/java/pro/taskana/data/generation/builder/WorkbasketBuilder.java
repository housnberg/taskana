package pro.taskana.data.generation.builder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pro.taskana.data.generation.util.DateHelper;
import pro.taskana.data.generation.util.WorkbasketWrapper;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.model.WorkbasketType;

/**
 * Class wraps the functionality for creating {@link Workbasket}.
 * 
 * @author fe
 *
 */
public class WorkbasketBuilder {

	public static int WORKBASKETS_IN_ORG_LVL_1 = 0;
	
	private final UserBuilder userBuilder;
	private final String domainName;
	private final DateHelper dateHelper;

	private List<WorkbasketWrapper> generatedWorkbaskets;

	public WorkbasketBuilder(String domainName, UserBuilder userBuilder) {
		this.dateHelper = new DateHelper();
		this.userBuilder = userBuilder;
		this.domainName = domainName;
		generatedWorkbaskets = new ArrayList<>();
		WORKBASKETS_IN_ORG_LVL_1 = 0;
	}

	/**
	 * Creates new personal {@link Workbasket}.
	 * 
	 * @param amount
	 *            number of new {@link Workbasket}
	 * @return all created {@link Workbasket}
	 */
	public List<WorkbasketWrapper> generateWorkbaskets(int amount) {
		List<WorkbasketWrapper> workbaskets = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			WorkbasketWrapper wb = generatePersonalWorkbasketWithOwner();
			workbaskets.add(wb);
		}
		return workbaskets;
	}

	/**
	 * Creates new group {@link Workbasket}. The {@link Workbasket} have no owner.
	 * 
	 * @param amount
	 *            number of new {@link Workbasket}
	 * @return all created {@link Workbasket}
	 */
	public List<WorkbasketWrapper> generateManagingWorkbaskets(int amount, boolean withOwner) {
		List<WorkbasketWrapper> workbaskets = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			if (withOwner) {
				workbaskets.add(generateGroupWorkbasketWithOwner());
			} else {
				workbaskets.add(generateGroupWorkbasketWithoutOwner());
			}
		}
		return workbaskets;
	}

	/**
	 * Supplies all generated {@link Workbasket}.
	 * 
	 * @return created workba{@link Workbasket}skets
	 */
	public List<WorkbasketImpl> getGeneratedWorkbaskets() {
		return generatedWorkbaskets.stream().map(wrapper -> wrapper.getAsWorkbasket()).collect(Collectors.toList());
	}

	private WorkbasketWrapper generateGroupWorkbasketWithoutOwner() {
		WorkbasketWrapper wb = createWorkbasket(WorkbasketType.GROUP);
		return wb;
	}

	private WorkbasketWrapper generateGroupWorkbasketWithOwner() {
		WorkbasketWrapper wb = generateGroupWorkbasketWithoutOwner();
		wb.setUserAsOwner(userBuilder.generateNewUser());
		return wb;
	}

	private WorkbasketWrapper generatePersonalWorkbasketWithOwner() {
		WorkbasketWrapper wb = createWorkbasket(WorkbasketType.PERSONAL);
		wb.setUserAsOwner(userBuilder.generateNewUser());
		return wb;
	}

	private WorkbasketWrapper createWorkbasket(WorkbasketType type) {
		WorkbasketWrapper wb = new WorkbasketWrapper(type, domainName);
		Instant created = dateHelper.getNextTimestampForWorkbasket();
		wb.setCreated(created);
		wb.setModified(created);
		generatedWorkbaskets.add(wb);
		return wb;
	}

}
