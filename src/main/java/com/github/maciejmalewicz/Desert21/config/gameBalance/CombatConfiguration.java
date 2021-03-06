package com.github.maciejmalewicz.Desert21.config.gameBalance;

import com.github.maciejmalewicz.Desert21.models.balance.CombatUnitConfig;
import com.github.maciejmalewicz.Desert21.models.balance.GeneralCombatConfig;
import com.github.maciejmalewicz.Desert21.models.balance.ScarabConfig;
import com.github.maciejmalewicz.Desert21.utils.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CombatConfiguration {

    @Bean(name = "droidsConfig")
    public CombatUnitConfig droidsConfig() {
        return BeanFactory.getBean("balance/combatConfig.yml", CombatUnitConfig.class, "droidsConfig");
    }

    @Bean(name = "tanksConfig")
    public CombatUnitConfig tanksConfig() {
        return BeanFactory.getBean("balance/combatConfig.yml", CombatUnitConfig.class, "tanksConfig");
    }

    @Bean(name = "cannonsConfig")
    public CombatUnitConfig cannonsConfig() {
        return BeanFactory.getBean("balance/combatConfig.yml", CombatUnitConfig.class, "cannonsConfig");
    }

    @Bean(name = "scarabsConfig")
    public ScarabConfig scarabsConfig() {
        return BeanFactory.getBean("balance/combatConfig.yml", ScarabConfig.class, "scarabsConfig");
    }

    @Bean(name = "generalCombatConfig")
    public GeneralCombatConfig generalCombatConfig() {
        return BeanFactory.getBean("balance/combatConfig.yml", GeneralCombatConfig.class, "generalCombatConfig");
    }
}
