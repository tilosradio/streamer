package hu.tilos.radio.backend.data.types;

import hu.tilos.radio.backend.data.types.EpisodeData;
import hu.tilos.radio.backend.data.types.ShowNamed;
import hu.tilos.radio.backend.data.types.ShowSimple;

import javax.persistence.*;
import java.util.Date;

public class BookmarkSimple {

    public int id;

    public String title;

    public Date realFrom;

    public Date realTo;

    public ShowNamed show;

}
