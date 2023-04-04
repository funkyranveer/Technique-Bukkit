package scha.efer.technique.util.bootstrap;

import scha.efer.technique.TechniquePlugin;
import lombok.Getter;

@Getter
public class Bootstrapped {

    protected final TechniquePlugin Practice;

    public Bootstrapped(TechniquePlugin Practice) {
        this.Practice = Practice;
    }

}
