package hu.tilos.radio.backend.data.types;

import java.util.ArrayList;
import java.util.List;

public class AuthorDetailed extends AuthorSimple implements AuthorBasic{



    private List<UrlData> urls = new ArrayList();

    private String introduction;

    private List<Contribution> contributions = new ArrayList<>();

    public List<Contribution> getContributions() {
        return contributions;
    }

    public void setContributions(List<Contribution> contributions) {
        this.contributions = contributions;
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
