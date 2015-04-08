package hu.tilos.radio.backend.data.types;

import java.util.ArrayList;
import java.util.List;

public class AuthorWithContribution extends AuthorSimple {

    private List<Contribution> contributions = new ArrayList<>();

    public List<Contribution> getContributions() {
        return contributions;
    }

    public void setContributions(List<Contribution> contributions) {
        this.contributions = contributions;
    }
}
