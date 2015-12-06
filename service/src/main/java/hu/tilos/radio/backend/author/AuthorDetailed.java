package hu.tilos.radio.backend.author;

import hu.tilos.radio.backend.contribution.Contribution;
import hu.tilos.radio.backend.author.AuthorBasic;
import hu.tilos.radio.backend.author.AuthorSimple;
import hu.tilos.radio.backend.data.types.UrlData;

import java.util.ArrayList;
import java.util.List;

public class AuthorDetailed extends AuthorSimple implements AuthorBasic {

    private String email;

    private List<UrlData> urls = new ArrayList();

    private String introduction;

    private List<Contribution> contributions = new ArrayList<>();

    public List<Contribution> getContributions() {
        return contributions;
    }

    public void setContributions(List<Contribution> contributions) {
        this.contributions = contributions;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public List<UrlData> getUrls() {
        return urls;
    }

    public void setUrls(List<UrlData> urls) {
        this.urls = urls;
    }
}
