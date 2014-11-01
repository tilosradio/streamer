package hu.tilos.radio.backend;

import hu.radio.tilos.model.Mix;
import hu.radio.tilos.model.type.MixCategory;
import hu.radio.tilos.model.type.MixType;
import hu.tilos.radio.backend.converters.MappingFactory;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Temporary utility class to import mixes to the database.
 */
public class MixImporter {

    private static int idCounter = 0;

    Pattern date3 = Pattern.compile(".*(\\d{2})(\\d{2})(\\d{2}).*");

    public static void main(String[] args) throws Exception {
        new MixImporter().run();
        //new MixImporter().test();


    }

    private void test() {
        Matcher m = date3.matcher("mixek/direct-garazsmenet-120922.mp3");
        System.out.println(m.matches());
    }

    private void run() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("javax.persistence.jdbc.url", "jdbc:mysql://localhost:3306/tilos2");
        properties.setProperty("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
        properties.setProperty("javax.persistence.jdbc.user", "root");
        properties.setProperty("javax.persistence.jdbc.password", "");

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("tilos-test", properties);
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Mix").executeUpdate();
        importFile(new File("/home/elek/projects/hangtar.html"), MixCategory.DJ, MixType.MUSIC, em);
        importFile(new File("/home/elek/projects/guest.html"), MixCategory.GUESTDJ, MixType.MUSIC, em);
        importFile(new File("/home/elek/projects/hangtar-musorok.html"), MixCategory.SHOW, MixType.SPEECH, em);
        importFile(new File("/home/elek/projects/mese.html"), MixCategory.TALE, MixType.SPEECH, em);

        em.getTransaction().commit();
        em.close();
    }

    private void importFile(File f, MixCategory c, MixType type, EntityManager em) throws IOException {
        Document document = Jsoup.parse(f, "UTF-8");
        Element select = document.select("#mainIndex").get(0);
        Elements links = select.select("a");
        for (Element e : links) {
            String link = e.attr("href").trim();
            if (link.length() > 0 && e.text().length() > 0) {
                if (link.endsWith(".mp3")) {

                    Mix mix = new Mix();
                    link = link.replace("http://archive.tilos.hu/sounds/", "");

                    Matcher m = date3.matcher(link);
                    if (m.matches()) {
                        int year = Integer.parseInt(m.group(1));
                        if (year < 50) {
                            year += 100;
                        }
                        mix.setDate(new Date(year, Integer.parseInt(m.group(2)) - 1, Integer.parseInt(m.group(3))));
                    }
                    String[] parts = e.text().split(":");
                    String author, title;
                    if (parts.length < 2) {
                        mix.setAuthor("");
                        mix.setTitle(parts[0].trim());
                    } else {
                        mix.setAuthor(parts[0].trim());
                        mix.setTitle(parts[1].trim());
                    }

                    mix.setFile(link);
                    mix.setType(type);
                    mix.setCategory(c);
                    em.persist(mix);
                }
            }
        }
    }
}
