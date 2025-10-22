package pro.thinhha.core.enums;

/**
 * Common status enum.
 * Use this for entities that have active/inactive states.
 */
public enum Status {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    PENDING("Pending"),
    SUSPENDED("Suspended"),
    DELETED("Deleted");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }
}
