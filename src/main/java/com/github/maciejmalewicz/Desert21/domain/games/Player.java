package com.github.maciejmalewicz.Desert21.domain.games;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Player {

    private String id;
    private String nickname;
    private ResourceSet resources;

    private List<LabUpgrade> ownedUpgrades = new ArrayList<>();

    private ProductionAI productionAI;

    //beginning of the game, to be ignored later
    private Boolean isReady;

    private int rocketStrikesDone;

    private int builtFactories;
    private int builtTowers;

    public Player(String id, String nickname, ResourceSet resources) {
        this.id = id;
        this.nickname = nickname;
        this.resources = resources;
        this.isReady = false;
        this.ownedUpgrades = new ArrayList<>();
        this.productionAI = new ProductionAI();
        this.rocketStrikesDone = 0;
        this.builtFactories = 0;
        this.builtTowers = 0;
    }

    public boolean ownsUpgrade(LabUpgrade upgrade) {
        return ownedUpgrades.contains(upgrade);
    }
}
