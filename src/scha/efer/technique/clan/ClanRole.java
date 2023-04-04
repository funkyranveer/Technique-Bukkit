package scha.efer.technique.clan;

public enum ClanRole {
    LEADER("**"), CAPTAIN("*"), MEMBER("");

    private final String prefix;

    ClanRole(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
