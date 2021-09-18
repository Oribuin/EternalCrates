package xyz.oribuin.eternalcrates.crate;

import java.util.HashMap;
import java.util.Map;

import xyz.oribuin.eternalcrates.animation.Animation;

public class Crate {

    private final String id;
    private String displayName;
    private Map<Reward, Integer> rewardMap;
    private Animation animation;

    public Crate(final String id) {
        this.id = id;
        this.setDisplayName(id);
        this.setRewardMap(new HashMap<>());
        this.setAnimation(null);
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<Reward, Integer> getRewardMap() {
        return rewardMap;
    }

    public void setRewardMap(Map<Reward, Integer> rewardMap) {
        this.rewardMap = rewardMap;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

}
