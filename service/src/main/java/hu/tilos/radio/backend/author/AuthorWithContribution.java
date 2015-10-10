package hu.tilos.radio.backend.author;

import hu.tilos.radio.backend.contribution.Contribution;
import hu.tilos.radio.backend.author.AuthorSimple;

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
