package pro.taskana.data.generation.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.data.generation.util.AccessType;
import pro.taskana.data.generation.util.ElementStack;
import pro.taskana.data.generation.util.UserWrapper;
import pro.taskana.data.generation.util.WorkbasketWrapper;
import pro.taskana.impl.WorkbasketImpl;

/**
 * Class encapsulates all configuration options for creating a domain.
 * 
 * @author fe
 *
 */
public class WorkbasketStructureBuilder {

	private final String domainName;
	private int quantity;
	private int numberOfDistTargetsFromPool;
	private List<WorkbasketWrapper> distributionTargets;
	private ElementStack<WorkbasketWrapper> poolOfGeneratedWorkbaskets;
	private boolean newOwner;
	private List<WorkbasketWrapper> lastGeneratedLayer;

	private AccessItemBuilder accesItemBuilder;
	private WorkbasketBuilder workbasketBuilder;
	private UserBuilder userBuilder;

	public WorkbasketStructureBuilder(String domainName) {
		this.domainName = domainName;
		this.userBuilder = new UserBuilder(domainName);
		this.accesItemBuilder = new AccessItemBuilder();
		this.workbasketBuilder = new WorkbasketBuilder(domainName, userBuilder);
		init();
	}

	/**
	 * Returns the name of the domain.
	 * 
	 * @return domain name
	 */
	public String getDomainName() {
		return domainName;
	}

	/**
	 * Resets all parameter. The default quantity of {@link Workbasket} in a layer
	 * is 1. The default number of direct distribution target for each
	 * {@link Workbasket} in a layer is 0. By default, existing users are assigned
	 * to created {@link Workbasket}. For this purpose, the owner of the first
	 * {@link Workbasket} of a group is selected.
	 * 
	 * @return configured {@link WorkbasketStructureBuilder} instance
	 */
	public WorkbasketStructureBuilder newLayer() {
		init();
		return this;
	}

	/**
	 * Sets the number of new {@link Workbasket} of this layer.
	 * 
	 * @param quantity
	 *            number new {@link Workbasket}
	 * @return configured {@link WorkbasketStructureBuilder} instance
	 */
	public WorkbasketStructureBuilder withWb(int quantity) {
		this.quantity = quantity;
		return this;
	}

	/**
	 * Determines whether new {@link Workbasket} will get an existing user as owner
	 * or if a new user will be created.
	 * 
	 * @return configured {@link WorkbasketStructureBuilder} instance
	 */
	public WorkbasketStructureBuilder withNewOwner() {
		this.newOwner = true;
		return this;
	}

	/**
	 * Sets the number of distribution targets for all {@link Workbasket} of this
	 * layer.
	 * 
	 * @param numberOfDistributionTargets
	 *            number of direct distribution targets
	 * @return configured {@link WorkbasketStructureBuilder} instance
	 */
	public WorkbasketStructureBuilder withNumberOfDistTargets(int numberOfDistributionTargets) {
		this.numberOfDistTargetsFromPool = numberOfDistributionTargets;
		return this;
	}

	/**
	 * Sets a quantity of {@link Workbasket} that each new {@link Workbasket} on
	 * this level will get as distribution target.
	 * 
	 * @param distributionTargets
	 *            a quantity of {@link Workbasket}
	 * @return configured {@link WorkbasketStructureBuilder} instance
	 */
	public WorkbasketStructureBuilder withDistTargets(List<WorkbasketWrapper> distributionTargets) {
		this.distributionTargets = distributionTargets;
		return this;
	}

	/**
	 * Sets a set of {@link Workbasket} to select the distribution targets of the
	 * current layer's {@link Workbasket}.
	 * 
	 * @param availableWorkbaskets
	 *            set of {@link Workbasket}
	 * @return configured {@link WorkbasketStructureBuilder} instance
	 */
	public WorkbasketStructureBuilder selectFrom(ElementStack<WorkbasketWrapper> availableWorkbaskets) {
		this.poolOfGeneratedWorkbaskets = availableWorkbaskets;
		return this;
	}

	/**
	 * Creates a set of new personal {@link Workbasket}.
	 * 
	 * @param amount
	 *            number of new {@link Workbasket}
	 * @return configured {@link WorkbasketStructureBuilder} instance
	 */
	public ElementStack<WorkbasketWrapper> createSimpleWorkbaskets(int amount) {
		List<WorkbasketWrapper> wbs = workbasketBuilder.generateWorkbaskets(amount);
		lastGeneratedLayer = wbs;
		return new ElementStack<>(wbs);
	}

	/**
	 * Builds a new layer in the current domain. Therefore necessary
	 * {@link Workbasket} will be created and equipped with the
	 * {@link WorkbasketAccessItem} and distribution targets according to the
	 * configuration.
	 * 
	 * @return generated {@link Workbasket} in the configured layer
	 */
	public List<WorkbasketWrapper> build() {
		List<WorkbasketWrapper> generatedParentWorkbaskets = new ArrayList<>();

		if (distributionTargets != null && !distributionTargets.isEmpty()) {
			generatedParentWorkbaskets = buildWorkbaskets(quantity, distributionTargets);
			if (numberOfDistTargetsFromPool > 0) {
				addAdditionalDistributionTargetsToWorkbaskets(numberOfDistTargetsFromPool, generatedParentWorkbaskets,
						poolOfGeneratedWorkbaskets);
			}
		} else if (numberOfDistTargetsFromPool > 0) {
			generatedParentWorkbaskets = buildWorkbaskets(numberOfDistTargetsFromPool, quantity,
					poolOfGeneratedWorkbaskets);
		}
		this.lastGeneratedLayer = generatedParentWorkbaskets;
		return generatedParentWorkbaskets;
	}
	
	/**
	 * Return the {@link WorkbasketWrapper} of the last generated layer.
	 * 
	 * @return list of {@link WorkbasketWrapper}
	 */
	public List<WorkbasketWrapper> getLastGeneratedLayer() {
		return lastGeneratedLayer;
	}

	/**
	 * Supplies all created {@link Workbasket} for the domain.
	 * 
	 * @return created {@link Workbasket}
	 */
	public List<WorkbasketImpl> getGeneratedWorkbaskets() {
		return workbasketBuilder.getGeneratedWorkbaskets();
	}

	/**
	 * Supplies all user ids as {@link UserWrapper} which are used in the domain.
	 * 
	 * @return all {@link UserWrapper}
	 */
	public List<UserWrapper> getGeneratedUsers() {
		return userBuilder.getGeneratedUsers();
	}

	/**
	 * Supplies all created {@link WorkbasketAccessItem}.
	 * 
	 * @return all {@link WorkbasketAccessItem}
	 */
	public List<WorkbasketAccessItem> getGeneratedAccessItems() {
		return accesItemBuilder.getGeneratedAccessItems();
	}

	private List<WorkbasketWrapper> buildWorkbaskets(int numberOfDirectDistTargets, int quantity,
			ElementStack<WorkbasketWrapper> workbasketPool) {
		List<WorkbasketWrapper> newWorkbaskets = workbasketBuilder.generateManagingWorkbaskets(quantity, newOwner);
		addAdditionalDistributionTargetsToWorkbaskets(numberOfDirectDistTargets, newWorkbaskets, workbasketPool);
		return newWorkbaskets;
	}

	private List<WorkbasketWrapper> buildWorkbaskets(int quantity, List<WorkbasketWrapper> distTargets) {
		List<WorkbasketWrapper> newWorkbaskets = workbasketBuilder.generateManagingWorkbaskets(quantity, newOwner);
		newWorkbaskets.forEach(wb -> buildGroup(wb, distTargets));
		return newWorkbaskets;
	}

	private void addAdditionalDistributionTargetsToWorkbaskets(int numberOfDirectDistTargets,
			List<WorkbasketWrapper> generatedworkbaskets, ElementStack<WorkbasketWrapper> workbasketPool) {

		for (WorkbasketWrapper workbasket : generatedworkbaskets) {
			List<WorkbasketWrapper> childWorkbaskets = null;
			if (!workbasketPool.isEmpty()) {
				if (workbasketPool.getSize() >= numberOfDirectDistTargets) {
					childWorkbaskets = workbasketPool.pop(numberOfDirectDistTargets);
				} else {
					int elementsInPool = workbasketPool.getSize();
					childWorkbaskets = workbasketPool.pop(elementsInPool);
					int newWb = numberOfDirectDistTargets - elementsInPool;
					childWorkbaskets.addAll(workbasketBuilder.generateWorkbaskets(newWb));
				}
			} else {
				childWorkbaskets = workbasketBuilder.generateWorkbaskets(numberOfDirectDistTargets);
			}

			buildGroup(workbasket, childWorkbaskets);
		}
	}

	private void buildGroup(WorkbasketWrapper parent, List<WorkbasketWrapper> children) {
		if (parent.getOwnerAsUser() == null) {
			parent.setUserAsOwner(children.get(0).getOwnerAsUser());
		}
		parent.addDistributionTargets(children);
		accesItemBuilder.forUser(parent.getOwnerAsUser()).hasAccess(AccessType.APPEND, AccessType.READ)
				.transitiveTo(children).build();
		List<UserWrapper> member = children.stream().map(wb -> wb.getOwnerAsUser()).collect(Collectors.toList());
		accesItemBuilder.forUsers(member).hasAccess(AccessType.READ).to(parent).build();
	}

	private void init() {
		numberOfDistTargetsFromPool = 0;
		quantity = 1;
		newOwner = false;
		distributionTargets = new ArrayList<>();
		poolOfGeneratedWorkbaskets = new ElementStack<>();
	}

}
