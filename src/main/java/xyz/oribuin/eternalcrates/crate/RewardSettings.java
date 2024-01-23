package xyz.oribuin.eternalcrates.crate;

public record RewardSettings(int minRewards, int maxRewards, int multiplier, int requiredSlots) {

    public RewardSettings {
        if (minRewards < 0) throw new IllegalArgumentException("Min rewards cannot be less than 0.");
        if (maxRewards < 0) throw new IllegalArgumentException("Max rewards cannot be less than 0.");
        if (multiplier < 0) throw new IllegalArgumentException("Multiplier cannot be less than 0.");
        if (requiredSlots < 0) throw new IllegalArgumentException("Required slots cannot be less than 0.");
    }

    public static RewardSettings getDefault() {
        return new RewardSettings(1, 1, 1, 1);
    }

}
